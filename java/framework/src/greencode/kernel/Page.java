package greencode.kernel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import greencode.jscript.Window;
import greencode.util.FileUtils;
import greencode.util.StringUtils;

public final class Page {
	static final HashMap<String, Page> pages = new HashMap<String, Page>();
	private static HashSet<String> requestsCached = new HashSet<String>();	
	
	Page(){}
	
	Class<? extends Window> window;
	greencode.jscript.window.annotation.Page pageAnnotation;		
	
	File file;
	String content;
	private String selector;
	private String selectedContent;
	
	private String ajaxSelector;
	private String ajaxSelectedContent;
	
	long lastModified;
	List<Page> inserted;
	Document document;
	
	String getSelectedContent(String selector)
	{
		if(this.selector == null)
		{
			this.selector = selector;
			this.selectedContent = document.select(selector).html();
		}
		
		return selectedContent;
	}
	
	String getAjaxSelectedContent(String selector)
	{
		if(this.ajaxSelector == null)
		{
			this.ajaxSelector = selector;
			this.ajaxSelectedContent = document.select(selector).html();
		}
		
		return ajaxSelectedContent;
	}
	
	void updateModifiedDate()
	{
		lastModified = file.lastModified();
	}
	
	boolean changed()
	{
		return file != null && lastModified != file.lastModified();
	}
	
	void verifyChanges()
	{
		verifyChanges(this);
	}
	
	private boolean verifyChanges(final Page page)
	{
		boolean changed = false;
		if(page.inserted != null)
		{
			for (Page i : page.inserted) {
				if(verifyChanges(i))
					changed = true;
			}
		}
		
		changed = changed || page.changed();
		
		if(changed)
		{
			try {
				page.selector = null;
				page.ajaxSelector = null;
				page.lastModified = 0;
				Page.loadStructure(page.file);
			} catch (IOException e) {
				Console.error(e);
			}
		}
		
		return changed;
	}
	
	static void registerPage(Class<? extends Window> c, greencode.jscript.window.annotation.Page page)
	{
		String path = page.path();
		if(pages.containsKey(path) && page.URLName().isEmpty() || !(path = page.URLName()).isEmpty() && pages.containsKey(path))
		{
			Console.warning(
				LogMessage.getMessage(
					"green-0022", path,
					c.getSimpleName(),
					pages.get(path).window.getSimpleName()
				)
			);
		}else
		{
			Page pReference = new Page();
			pReference.window = c;
			pReference.pageAnnotation = page;
			
			File file = FileUtils.getFileInWebContent(page.path());
			if(file.exists())
			{
				pReference.file = file;
				if(!page.URLName().isEmpty())
					pages.put(page.URLName(), pReference);
				else
					pages.put(page.path(), pReference);
			}else
				Console.error(LogMessage.getMessage("green-0014", page.path()));
		}
	}
	
	static Page loadStructure(File file) throws IOException
	{
		return loadStructure(file, null);
	}
	
	static Page pathAnalyze(String servletPath, Page page)
	{
		if(GreenCodeConfig.View.isBootable())
		{
			return page;
		}
		
		boolean isView = false;
		
		if(page != null)
		{
			if(page.content != null)
			{
				if(GreenCodeConfig.View.seekChange())
					page.verifyChanges();
				return page;
			}else
			{
				servletPath = page.pageAnnotation.path();
				isView = true;
			}
		}
		
		if(!isView)
		{
			String ext = FileUtils.getExtension(servletPath);
			
			boolean isCss = ext.equals("css");
			boolean isJs = ext.equals("js");
			isView = ext.equals("html") || ext.equals("xhtml") || ext.equals("jsp") || ext.equals("htm");
	
			if(!isCss && !isJs && !isView)
			{
				return page;
			}
		}
			
		try {
			if(page == null && !requestsCached.contains(servletPath) || page != null && page.document == null)
			{
				File file = FileUtils.getFileInWebContent(servletPath);
				if(file != null && file.exists())
				{		
					Console.log(
						isView ? "Applying (template"+(GreenCodeConfig.View.usingMinified() ? ", minified" : "")+") in "+servletPath
					:
						"Applying (minified) in "+servletPath);
		
					page = loadStructure(file, page);
					requestsCached.add(servletPath);
				}
			}
		} catch (IOException e) {
			Console.error(e);
		}
		
		return page;
	}
	
	//TODO: Verificar futuramente para possíveis otimizações.
	static Page loadStructure(File file, Page page) throws IOException
	{
		String ext = FileUtils.getExtension(file.getName());
		
		boolean isCss = ext.equals("css");
		boolean isJs = ext.equals("js");
		boolean isView = ext.equals("html") || ext.equals("xhtml") || ext.equals("jsp") || ext.equals("htm");
		
		if(isCss || isJs || isView)
		{
			List<Page> inserted = null;
			String content = null;
			
			String path = null;
			Document src = null;
			
			if(isView)
			{				
				if(page == null)
				{
					path = file.toURI().toURL().getPath();
					path = path.substring(path.indexOf("WEB-INF/classes/../../")+22);
					
					page = pages.get(path);
				}else
				{
					path = page.pageAnnotation.path();
				}				
				
				if(page != null && !page.changed())
				{
					return page;
				}
				
				inserted = new ArrayList<Page>();				
				src = Jsoup.parse(file, GreenCodeConfig.View.getCharset());
				
				List<Element> listSelf = src.getElementsByTag("template:import");
							
				if(listSelf.size() > 0)
				{
					Element ele = listSelf.get(0);
	
					Document templateImported = null;
					
					String strTemplate = ele.attr("src");
					if(strTemplate != null && !strTemplate.isEmpty())
					{
						String caminho = file.getParentFile().getAbsolutePath()+"/"+strTemplate;
						File f = new File(caminho);
						try {
							Page template = loadStructure(f);
							
							if(!GreenCodeConfig.View.isBootable())
								inserted.add(template);
							
							templateImported = template.document;
						} catch (IOException e) {
							Console.error(LogMessage.getMessage("green-0020", strTemplate, "template:import", file.getName()));
						}
					}else
					{
		 				Page template = loadStructure(GreenContext.defaultTemplate);
						templateImported = template.document;
						
						if(!GreenCodeConfig.View.isBootable())
							inserted.add(template);
					}
					
					if(templateImported != null)
					{
						String headContent = templateImported.head().html();
						headContent = StringUtils.replace(headContent,"<script type=\"text/javascript\" src=\""+Core.CONTEXT_PATH+"/jscript/greencode/core.js\"></script>", "");
						
						src.head().append(headContent);
						src.body().append(templateImported.body().html());
					}
					ele.remove();
					
					String title = ele.attr("title");
					if(title != null && !title.isEmpty())
					{
						Elements e = src.getElementsByTag("title");
						
						if(e.size() > 0)
						{
							e.get(0).text(title);
						}
					}
					
					List<Element> elementsDefine = src.getElementsByTag("template:define");
					if(elementsDefine.size() > 0)
					{
						List<Element> elementsInsert = src.getElementsByTag("template:insert");
						
						if(elementsInsert.size() > 0)
						{
							for (Element eInsert : elementsInsert) {
								for (Element eDefine : elementsDefine) {
									if(eInsert.attr("name").equals(eDefine.attr("name")))
									{
										eInsert.after(eDefine.html());
										eInsert.remove();
										eDefine.remove();
									}
								}
							}
						}
					}
				}
				
				List<Element> elementsInclude = src.getElementsByTag("template:include");
				for (Element element : elementsInclude)
				{
					String attrSrc = element.attr("src");
					if(attrSrc != null && !attrSrc.isEmpty())
					{
						String caminho = file.getParentFile().getAbsolutePath()+"/"+attrSrc;
						File f = new File(caminho);
						try {
							Page _page = loadStructure(f);
							
							if(!GreenCodeConfig.View.isBootable())
								inserted.add(_page);
							
							String pageContent = _page.content;
							pageContent = StringUtils.replace(pageContent,"<script type=\"text/javascript\" src=\""+Core.CONTEXT_PATH+"/jscript/greencode/core.js\"></script>", "");
							
							element.after(pageContent);
							element.remove();
						} catch (IOException e) {
							Console.error(LogMessage.getMessage("green-0020", attrSrc, "template:include", file.getName()));
						}
					}
				}
				
				List<Element> joins = src.head().getElementsByAttribute("join");
				for (Element e : joins) {
					String[] filesName = e.attr("join").split(",");
					
					String joinContent = "";
					for (String name : filesName) {						
						File f = FileUtils.getFileInWebContent(name.trim());
						if(!f.exists())
						{
							Console.error(LogMessage.getMessage("green-0020", name.trim(), e.tagName(), file.getName()));
							continue;
						}
						
						joinContent += FileUtils.getContentFile(f.toURI().toURL());
					}
					
					if(e.attr("file").isEmpty())
					{
						Console.error(LogMessage.getMessage("green-0021", "file", e.tagName(), file.getName()));
						continue;
					}
						
					FileUtils.createFile(joinContent, FileUtils.getFileInWebContent(e.attr("file")));
					e.removeAttr("join");
					e.removeAttr("file");
				}
				
				src.head().prepend("<script type=\"text/javascript\" src=\""+Core.CONTEXT_PATH+"/jscript/greencode/core.js\"></script>");
				
				content = src.html();
				
				content = content.replaceAll(Pattern.quote("GREENCODE:{CONTEXT_PATH}"), Core.CONTEXT_PATH);
			}
			
			if(content == null)
			{
				content = FileUtils.getContentFile(file.toURI().toURL()).toString();
			}
			
			if(GreenCodeConfig.View.usingMinified())
			{
				HtmlCompressor html = new HtmlCompressor();
				html.setRemoveIntertagSpaces(true);
				content = html.compress(content);
			}			
			
			if(isView)
			{
				if(page == null)
				{
					page = new Page();
					page.file = file;
					pages.put(path, page);
				}else
				{
					page.updateModifiedDate();
				}
				
				page.content = content;
				page.document = src;
				if(inserted.size() > 0)
					page.inserted = inserted;
				
				src = null;
				path = null;
				
				return page;
			}else
			{				
				FileUtils.createFile(content, file);
			}
		}
		
		return null;
	}
}