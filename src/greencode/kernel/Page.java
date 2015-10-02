package greencode.kernel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import greencode.jscript.FunctionHandle;
import greencode.jscript.Window;
import greencode.util.FileUtils;
import greencode.util.GenericReflection;
import greencode.util.MergedFile;
import greencode.util.StringUtils;

public final class Page {
	final static HashMap<String, Page> pages = new HashMap<String, Page>();
	private final static HashSet<String> requestsCached = new HashSet<String>();

	Page(Class<? extends Window> window, greencode.jscript.window.annotation.Page pageAnnotation) {
		this.window = window;
		this.pageAnnotation = pageAnnotation;
	}

	Page() {
		this(null, null);
	}

	final Class<? extends Window> window;
	final greencode.jscript.window.annotation.Page pageAnnotation;

	String moduleName;

	private File file;
	private String content, selector, selectedContent, ajaxSelector, ajaxSelectedContent;

	long lastModified;
	List<Page> inserted;
	Document document;

	private Page mobilePage;
	// private Mobile pageMobileAnnotation;

	private Page getCurrentPage(GreenContext context) {
		return mobilePage != null && context.getRequest().isMobile() ? mobilePage : this;
	}

	String getSelectedContent(String selector, GreenContext context) {
		Page currentPage = getCurrentPage(context);

		if(currentPage.selector == null) {
			currentPage.selector = selector;
			currentPage.selectedContent = currentPage.document.select(selector).html();
		}

		return currentPage.selectedContent;
	}

	String getAjaxSelectedContent(String selector, GreenContext context) {
		Page currentPage = getCurrentPage(context);

		if(currentPage.ajaxSelector == null) {
			currentPage.ajaxSelector = selector;
			currentPage.ajaxSelectedContent = currentPage.document.select(selector).html();
		}

		return currentPage.ajaxSelectedContent;
	}

	String getContent(GreenContext context) {
		return getCurrentPage(context).content;
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

	private boolean verifyChanges(final Page page, boolean isPrincipal) {
		boolean changed = false;
		if(page.inserted != null) {
			for(Page i: page.inserted) {
				if(verifyChanges(i, false))
					changed = true;
			}
		}

		changed = changed || page.changed();

		if(changed) {
			try {
				page.selector = null;
				page.ajaxSelector = null;
				page.lastModified = 0;
				Page.loadStructure(page.file, null, isPrincipal);
			} catch(IOException e) {
				Console.error(e);
			}
		}

		return changed;
	}

	private static Page loadStructure(File file) throws IOException {
		return loadStructure(file, null, false);
	}

	// TODO: Verificar futuramente para possíveis otimizações.
	private static Page loadStructure(File file, Page page, boolean importCoreJS) throws IOException {
		final String ext = FileUtils.getExtension(file.getName());

		final boolean
			isCss = ext.equals("css"),
			isJs = ext.equals("js"),
			isView = ext.equals("html") || ext.equals("xhtml") || ext.equals("jsp") || ext.equals("htm");

		if(isCss || isJs || isView) {
			List<Page> inserted = null;
			String content = null, path = null;
			Document src = null;

			if(isView) {
				if(page == null) {
					path = file.toURI().toURL().getPath();
					path = path.substring(path.indexOf("WEB-INF/classes/../../") + 22);

					page = pages.get(path);
				} else
					path = page.pageAnnotation.path();

				if(page != null && !page.changed())
					return page;

				inserted = new ArrayList<Page>();
				content = FileUtils.getContentFile(file.toURI().toURL(), GreenCodeConfig.View.charset).replaceAll(Pattern.quote("GREENCODE:{CONTEXT_PATH}"), Core.CONTEXT_PATH);

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
				
				src = Jsoup.parse(content, GreenCodeConfig.View.charset);

				List<Element> listSelf = src.getElementsByTag("template:import");

				if(!listSelf.isEmpty()) {
					Element ele = listSelf.get(0);

					Document templateImported = null;

					String templateName = ele.attr("name");
					if(templateName != null && !templateName.isEmpty()) {
						File f = Cache.templates.get(templateName);
						if(f != null) {
							Page template = loadStructure(f);

							if(!GreenCodeConfig.View.bootable)
								inserted.add(template);

							templateImported = template.document;
						} else
							Console.error(LogMessage.getMessage("green-0042", templateName));
					} else {
						Page template = loadStructure(Cache.defaultTemplate);
						templateImported = template.document;

						if(!GreenCodeConfig.View.bootable)
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
							Page _page = loadStructure(f);

							if(!GreenCodeConfig.View.bootable)
								inserted.add(_page);

							if(element.hasAttr("head"))
								src.head().append(_page.content);
							else
								element.after(_page.content);

							element.remove();
						} catch(IOException e) {
							Console.error(LogMessage.getMessage("green-0020", attrSrc, "template:include", file.getName()));
						}
					}
				}

				List<Element> joins = src.head().getElementsByAttribute("join");
				for(Element e: joins) {
					String filePath = e.attr("file");
					if(filePath.isEmpty()) {
						Console.error(LogMessage.getMessage("green-0021", "file", e.tagName(), file.getName()));
						continue;
					}

					String[] filesName = e.attr("join").split(",");

					File[] files = new File[filesName.length];
					for(int i = -1; ++i < filesName.length;) {
						String name = filesName[i].trim();
						File f = FileUtils.getFileInWebContent(name);
						if(!f.exists()) {
							Console.error(LogMessage.getMessage("green-0020", name, e.tagName(), file.getName()));
							continue;
						}
						files[i] = f;
					}

					MergedFile mergedFile = new MergedFile(FileUtils.getFileInWebContent(e.attr("file")).toURI(), files);

					Cache.mergedFiles.put(filePath, mergedFile);

					e.removeAttr("join").removeAttr("file");
				}

				if(importCoreJS)
					src.head().prepend("<script type=\"text/javascript\" src=\"" + Core.SRC_CORE_JS_FOR_SCRIPT_HTML + "\"></script>");

				content = src.html();
			} else
				content = FileUtils.getContentFile(file.toURI().toURL());

			if(GreenCodeConfig.View.useMinified) {
				HtmlCompressor html = new HtmlCompressor();
				html.setRemoveIntertagSpaces(true);
				content = html.compress(content);
			}

			if(isView) {
				if(page == null) {
					(page = new Page()).file = file;
					pages.put(path, page);
				} else
					page.updateModifiedDate();

				page.content = content;
				page.document = src;
				if(!inserted.isEmpty())
					page.inserted = inserted;

				src = null;
				path = null;

				return page;
			} else
				FileUtils.createFile(content, file);
		}

		return null;
	}

	static void registerPage(ClassLoader classLoader, Class<? extends Window> c, greencode.jscript.window.annotation.Page page, File greencodeFolder) throws IOException {
		String path = page.path();
		if(pages.containsKey(path) && page.URLName().isEmpty() || !(path = page.URLName()).isEmpty() && pages.containsKey(path)) {
			Console.warning(LogMessage.getMessage("green-0022", path, c.getSimpleName(), pages.get(path).window.getSimpleName()));
		} else {
			Page pReference = new Page(c, page);

			if(!page.mobile().path().isEmpty()) {
				Page mobilePage = new Page(c, page);

				File file = FileUtils.getFileInWebContent(page.mobile().path());
				if(file.exists()) {
					mobilePage.file = file;

					if(GreenCodeConfig.View.bootable)
						loadStructure(file, mobilePage, true);

					pReference.mobilePage = mobilePage;
				} else
					Console.error(LogMessage.getMessage("green-0014", page.mobile().path()));
			}

			if(!page.jsModule().isEmpty()) {
				final String modulePath = StringUtils.replace(page.jsModule(), ".", "/") + ".js";
				pReference.moduleName = modulePath.substring(modulePath.lastIndexOf('/') + 1, modulePath.length() - 3);

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
						if(!method.getName().equals("init") && method.getParameterTypes().length == 0) {
							FunctionHandle func = new FunctionHandle(c, method.getName());
							methodsJS.append("var ").append(method.getName()).append("=function(onComplete) {var param =").append(new Gson().toJson(func)).append(";param.viewId = __viewId;param.cid = __cid;param.url = CONTEXT_PATH+param.url;Bootstrap.callRequestMethod(principalElement, {}, {event: 'undefined', onComplete: onComplete}, param, []);};");
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
				pages.put(page.URLName().isEmpty() ? page.path() : page.URLName(), pReference);

				if(GreenCodeConfig.View.bootable)
					loadStructure(file, pReference, true);
			} else
				Console.error(LogMessage.getMessage("green-0014", page.path()));
		}
	}

	static Page pathAnalyze(String servletPath, Page page, HttpServletRequest request) {
		if(GreenCodeConfig.View.bootable)
			return page;

		try {
			boolean isView = false;

			if(page != null) {
				if(page.content != null) {
					if(GreenCodeConfig.View.seekChange) {
						(page.mobilePage != null && greencode.http.$HttpRequest.isMobile(request.getHeader("user-agent")) ? page.mobilePage : page).verifyChanges();
					}

					return page;
				}

				servletPath = page.pageAnnotation.path();
				isView = true;
			} else {
				final String ext = FileUtils.getExtension(servletPath);

				final boolean isCss = ext.equals("css"), isJs = ext.equals("js");
				if((GreenCodeConfig.View.seekChange) && (isCss || isJs)) {
					MergedFile mergedFile = Cache.mergedFiles.get(servletPath);
					if(mergedFile != null)
						mergedFile.verifyChanges();
				}

				isView = ext.equals("html") || ext.equals("xhtml") || ext.equals("jsp") || ext.equals("htm");

				if(!isCss && !isJs && !isView)
					return page;
			}

			if(page == null && !requestsCached.contains(servletPath) || page != null && page.document == null) {
				File file = FileUtils.getFileInWebContent(servletPath);
				if(file != null && file.exists()) {
					Console.log(isView ? "Applying (template" + (GreenCodeConfig.View.useMinified ? ", minified" : "") + ") in " + servletPath : "Applying (minified) in " + servletPath);

					page = loadStructure(file, page, true);
					if(page != null && page.mobilePage != null)
						loadStructure(page.mobilePage.file, page.mobilePage, true);

					requestsCached.add(servletPath);
				}
			}
		} catch(IOException e) {
			Console.error(e);
		}

		return page;
	}
}