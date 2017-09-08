package greencode.kernel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import greencode.exception.GreencodeError;
import greencode.jscript.dom.FunctionHandle;
import greencode.jscript.dom.Window;
import greencode.jscript.dom.window.annotation.Page;
import greencode.util.FileUtils;
import greencode.util.GenericReflection;
import greencode.util.LogMessage;
import greencode.util.MergedFile;
import greencode.util.StringUtils;

public final class FileWeb {
	final static Map<String, FileWeb> files = new HashMap<String, FileWeb>();
	private final static HashSet<String> requestsCached = new HashSet<String>();

	FileWeb(Class<? extends Window> window, Page pageAnnotation) {
		this.window = window;
		this.pageAnnotation = pageAnnotation;
	}

	FileWeb() {
		this(null, null);
	}

	final Class<? extends Window> window;
	final Page pageAnnotation;

	private File file;
	private String content, selector, selectedContent, ajaxSelector, ajaxSelectedContent;

	long lastModified;
	List<FileWeb> inserted;
	Document document;

	private FileWeb mobileFile;
	private boolean isMobile;

	private FileWeb getCurrent(GreenContext context) {
		return mobileFile != null && context.getRequest().isMobile() ? mobileFile : this;
	}

	String getSelectedContent(String selector, GreenContext context) {
		FileWeb current = getCurrent(context);

		if(current.selector == null) {
			current.selector = selector;
			current.selectedContent = current.document.select(selector).html();
		}

		return current.selectedContent;
	}

	String getAjaxSelectedContent(String selector, GreenContext context) {
		FileWeb current = getCurrent(context);

		if(current.ajaxSelector == null) {
			current.ajaxSelector = selector;
			current.ajaxSelectedContent = current.document.select(selector).html();
		}

		return current.ajaxSelectedContent;
	}

	String getContent(GreenContext context) {
		return getCurrent(context).content;
	}

	void setContent(String content) {
		this.content = content;
	}

	private void updateModifiedDate() {
		lastModified = file.lastModified();
	}

	private boolean changed() {
		return file != null && lastModified != file.lastModified();
	}

	private void verifyChanges() {
		verifyChanges(this, true);
	}

	private boolean verifyChanges(final FileWeb file, boolean isPrincipal) {
		boolean changed = false;
		if(file.inserted != null) {
			for(FileWeb i: file.inserted) {
				if(verifyChanges(i, false))
					changed = true;
			}
		}

		changed = changed || file.changed();

		if(changed) {
			try {
				file.selector = null;
				file.ajaxSelector = null;
				file.lastModified = 0;
				FileWeb.loadStructure(file.file, file.isMobile ? this : null, isPrincipal);
			} catch(IOException e) {
				throw new GreencodeError(e);
			}
		}

		return changed;
	}

	private static FileWeb loadStructure(File file) throws IOException {
		return loadStructure(file, null, false);
	}

	// TODO: Verificar futuramente para possíveis otimizações.
	private static FileWeb loadStructure(File file, FileWeb fileWeb, boolean importCoreJS) throws IOException {
		final String ext = FileUtils.getExtension(file.getName());
		
		final boolean
			isCss = ext.equals("css"),
			isJs = ext.equals("js"),
			isView = ext.equals("html") || ext.equals("xhtml") || ext.equals("jsp") || ext.equals("htm");

		if(isCss || isJs || isView) {
			List<FileWeb> inserted = null;
			String content = null, path = null;
			Document src = null;

			if(isView) {
				if(fileWeb == null) {
					path = file.toURI().toURL().getPath();
					path = path.substring(Core.PROJECT_CONTENT_PATH.length()+1);

					fileWeb = files.get(path);
				} else
					path = fileWeb.isMobile ? fileWeb.pageAnnotation.mobile().path() : fileWeb.pageAnnotation.path();

				if(fileWeb != null && !fileWeb.changed())
					return fileWeb;

				inserted = new ArrayList<FileWeb>();
				content = FileUtils.getContentFile(file.toURI().toURL(), GreenCodeConfig.Server.View.charset).replaceAll(Pattern.quote("GREENCODE:{CONTEXT_PATH}"), Core.CONTEXT_PATH);

				int lastIndex = 0;
				String startString = "GREENCODE:{(";
				while((lastIndex = content.indexOf(startString, lastIndex)) != -1) {
					final int listCloseIndex = content.indexOf('}', lastIndex);
					final String c = content.substring(lastIndex + startString.length(), listCloseIndex);

					try {
						int closeIndex = c.indexOf(')');
						Class<?> clazz = Class.forName(c.substring(0, closeIndex));
						content = content.replaceAll(Pattern.quote(startString + c + '}'), GenericReflection.getValue(clazz, c.substring(closeIndex + 2), null).toString());
					} catch(ClassNotFoundException e1) {
						e1.printStackTrace();
					}

					lastIndex = listCloseIndex;
				}
				
				src = Jsoup.parse(content, GreenCodeConfig.Server.View.charset);

				List<Element> listSelf = src.getElementsByTag("template:import");

				if(!listSelf.isEmpty()) {
					Element ele = listSelf.get(0);

					Document templateImported = null;

					String templateName = ele.attr("name");
					if(templateName != null && !templateName.isEmpty()) {
						File f = Cache.templates.get(templateName);
						if(f != null) {
							FileWeb template = loadStructure(f);

							if(!GreenCodeConfig.Server.View.bootable)
								inserted.add(template);

							templateImported = template.document;
						} else
							throw new GreencodeError(LogMessage.getMessage("green-0042", templateName));
					} else {
						FileWeb template = loadStructure(Cache.defaultTemplate);
						templateImported = template.document;

						if(!GreenCodeConfig.Server.View.bootable)
							inserted.add(template);
					}

					if(templateImported != null) {
						ele.remove();
						src.head().replaceWith(templateImported.head().clone());
						src.body().replaceWith(templateImported.body().clone().append(src.body().html()));
					}

					String title = ele.attr("title");
					if(title != null && !title.isEmpty()) {
						Elements e = src.getElementsByTag("title");

						if(!e.isEmpty())
							e.get(0).text(title);
					}

					List<Element> elementsHead = src.getElementsByTag("template:head");
					if(!elementsHead.isEmpty()) {
						for(Element e: elementsHead) {
							src.head().append(e.html());
							e.remove();
						}
					}

					List<Element> elementsDefine = src.getElementsByTag("template:define");
					if(!elementsDefine.isEmpty()) {
						List<Element> elementsInsert = src.getElementsByTag("template:insert");

						if(!elementsInsert.isEmpty()) {
							for(Element eInsert: elementsInsert) {
								for(Element eDefine: elementsDefine) {
									if(eInsert.attr("name").equals(eDefine.attr("name"))) {
										eInsert.after(eDefine.html()).remove();
										eDefine.remove();
									}
								}
							}
						}
					}
				}

				List<Element> elementsInclude = src.getElementsByTag("template:include");
				for(Element element: elementsInclude) {
					String attrSrc = element.attr("src");
					if(attrSrc != null && !attrSrc.isEmpty()) {
						File f = new File(file.getParentFile().getAbsolutePath() + "/" + attrSrc);
						try {
							FileWeb fw = loadStructure(f);

							if(!GreenCodeConfig.Server.View.bootable)
								inserted.add(fw);

							if(element.hasAttr("head"))
								src.head().append(fw.content);
							else
								element.after(fw.content);

							element.remove();
						} catch(IOException e) {
							throw new GreencodeError(LogMessage.getMessage("green-0020", attrSrc, "template:include", file.getName()));
						}
					}
				}

				List<Element> joins = src.head().getElementsByAttribute("join");
				if(GreenCodeConfig.Server.View.seekChange && !joins.isEmpty()) {
					StringBuilder links = new StringBuilder();
					for(Element e: joins) {
						String[] filesName = e.attr("join").split(",");
						for (String fileName : filesName) {
							Element newElement = e.clone();
							newElement.attr(newElement.tagName().toLowerCase().equals("link") ? "href" : "src", Core.CONTEXT_PATH+'/'+fileName.trim());
							newElement.removeAttr("join");
							newElement.removeAttr("file");
							links.append(newElement.outerHtml());
						}
						e.remove();
					}
					joins.clear();
					src.head().prepend(links.toString());
				} else {
					for(Element e: joins) {
						String filePath = e.attr("file");
						if(filePath.isEmpty())
							throw new GreencodeError(LogMessage.getMessage("green-0021", "file", e.tagName(), file.getName()));

						String[] filesName = e.attr("join").split(",");

						File[] files = new File[filesName.length];
						for(int i = -1; ++i < filesName.length;) {
							String name = filesName[i].trim();
							File f = FileUtils.getFileInWebContent(name);
							if(!f.exists()) {
								throw new GreencodeError(LogMessage.getMessage("green-0020", name, e.tagName(), file.getName()));
							}
							files[i] = f;
						}

						MergedFile mergedFile = new MergedFile(FileUtils.getFileInWebContent(e.attr("file")).toURI(), files);

						Cache.mergedFiles.put(filePath, mergedFile);

						e.removeAttr("join").removeAttr("file");
					}
				}				

				if(importCoreJS)
					src.head().prepend("<script type=\"text/javascript\" src=\"" + Core.SRC_CORE_JS_FOR_SCRIPT_HTML + "\" charset=\""+GreenCodeConfig.Server.View.charset+"\"></script>");

				content = src.html();
			} else
				content = FileUtils.getContentFile(file.toURI().toURL());

			if(GreenCodeConfig.Server.View.useMinified) {
				HtmlCompressor html = new HtmlCompressor();
				html.setRemoveIntertagSpaces(true);
				content = html.compress(content);
			}

			if(isView) {
				if(fileWeb == null) {
					(fileWeb = new FileWeb()).file = file;
					files.put(path, fileWeb);
				} else
					fileWeb.updateModifiedDate();

				fileWeb.content = content;
				fileWeb.document = src;
				if(!inserted.isEmpty())
					fileWeb.inserted = inserted;

				src = null;
				path = null;

				return fileWeb;
			} else
				FileUtils.createFile(content, file);
		}

		return null;
	}

	static void registerPage(ClassLoader classLoader, Class<? extends Window> c, Page page, File greencodeFolder) throws IOException {
		String path = page.path();
		if(files.containsKey(path) && page.URLName().isEmpty() || !(path = page.URLName()).isEmpty() && files.containsKey(path)) {
			Console.warning(LogMessage.getMessage("green-0022", path, c.getSimpleName(), files.get(path).window.getSimpleName()));
		} else {
			FileWeb pReference = new FileWeb(c, page);

			if(!page.mobile().path().isEmpty()) {
				FileWeb mobileFileWeb = new FileWeb(c, page);

				File file = FileUtils.getFileInWebContent(page.mobile().path());
				if(file.exists()) {
					mobileFileWeb.file = file;
					mobileFileWeb.isMobile = true;

					if(GreenCodeConfig.Server.View.bootable)
						loadStructure(file, mobileFileWeb, true);

					pReference.mobileFile = mobileFileWeb;
				} else
					throw new GreencodeError(LogMessage.getMessage("green-0014", page.mobile().path()));
			} else {
				
			}

			if(!page.jsModule().isEmpty()) {
				final String modulePath = StringUtils.replace(page.jsModule(), ".", "/") + ".js";

				final URL url = classLoader.getResource(modulePath);

				if(url == null)
					throw new RuntimeException(LogMessage.getMessage("green-0038", modulePath));

				try {
					final File modulesFolder = new File(greencodeFolder.getPath() + "/modules/");
					if(!modulesFolder.exists())
						modulesFolder.mkdir();

					final StringBuilder methodsJS = new StringBuilder();

					final Method[] methods = GenericReflection.getDeclaredMethods(c);
					for(Method method: methods) {
						if(method.getName().equals(Core.INIT_METHOD_NAME)) {
							FunctionHandle func = new FunctionHandle(c, method.getName());
							methodsJS.append("var ").append(method.getName()).append("=function(onComplete) {var param =").append(new Gson().toJson(func)).append(";param.viewId = __viewId;param.cid = __cid;param.url = CONTEXT_PATH+param.url;Greencode.core.callRequestMethod(principalElement, {}, {event: 'undefined', onComplete: onComplete}, param, []);};");
						}
					}

					FileUtils.createFile("Greencode.modules." + page.name() + "=function(principalElement, __viewId, __cid){" + methodsJS.toString() + FileUtils.getContentFile(url) + "}", modulesFolder.getPath() + "/" + modulePath.substring(modulePath.lastIndexOf('/')));
				} catch(IOException e) {
					e.printStackTrace();
				}
			}

			File file = FileUtils.getFileInWebContent(page.path());
			if(file.exists()) {
				pReference.file = file;
				files.put(page.URLName().isEmpty() ? page.path() : page.URLName(), pReference);

				if(GreenCodeConfig.Server.View.bootable)
					loadStructure(file, pReference, true);
			} else
				throw new GreencodeError(LogMessage.getMessage("green-0014", page.path()));
		}
	}
	
	static String getModuleName(String jsModule) {
		if(jsModule.isEmpty())
			return null;
		
		final String modulePath = StringUtils.replace(jsModule, ".", "/") + ".js";
		return modulePath.substring(modulePath.lastIndexOf('/') + 1, modulePath.length() - 3);
	}

	static FileWeb pathAnalyze(String servletPath, FileWeb fileWeb, HttpServletRequest request) {
		if(GreenCodeConfig.Server.View.bootable)
			return fileWeb;

		try {
			boolean isView = false;

			if(fileWeb != null) {
				if(fileWeb.content != null) {
					if(GreenCodeConfig.Server.View.seekChange) {
						(fileWeb.mobileFile != null && greencode.http.$HttpRequest.isMobile(request) ? fileWeb.mobileFile : fileWeb).verifyChanges();
					}

					return fileWeb;
				}

				servletPath = fileWeb.pageAnnotation.path();
				isView = true;
			} else {
				final String ext = FileUtils.getExtension(servletPath);

				final boolean isCss = ext.equals("css"), isJs = ext.equals("js");
				if((GreenCodeConfig.Server.View.seekChange) && (isCss || isJs)) {
					MergedFile mergedFile = Cache.mergedFiles.get(servletPath);
					if(mergedFile != null)
						mergedFile.verifyChanges();
				}

				isView = ext.equals("html") || ext.equals("xhtml") || ext.equals("jsp") || ext.equals("htm");

				if(!isCss && !isJs && !isView)
					return fileWeb;
			}

			if(fileWeb == null && !requestsCached.contains(servletPath) || fileWeb != null && fileWeb.document == null) {
				File file = FileUtils.getFileInWebContent(servletPath);
				if(file != null && file.exists()) {
					
					if(isView) {
						Console.log("Applying (template" + (GreenCodeConfig.Server.View.useMinified ? ", minified" : "") + ") in " + servletPath);
					} else if(GreenCodeConfig.Server.View.useMinified) {
						Console.log("Applying (minified) in " + servletPath);	
					}

					fileWeb = loadStructure(file, fileWeb, true);
					if(fileWeb != null && fileWeb.mobileFile != null)
						loadStructure(fileWeb.mobileFile.file, fileWeb.mobileFile, true);

					requestsCached.add(servletPath);
				}
			}
		} catch(IOException e) {
			throw new GreencodeError(e);
		}

		return fileWeb;
	}
}