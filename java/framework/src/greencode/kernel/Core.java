package greencode.kernel;

import greencode.database.DatabaseConfig;
import greencode.database.DatabaseConnection;
import greencode.database.annotation.Connection;
import greencode.database.implementation.DatabaseConnectionEvent;
import greencode.exception.OperationNotAllowedException;
import greencode.http.HttpAction;
import greencode.http.HttpRequest;
import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;
import greencode.jscript.Window;
import greencode.jscript.event.EventObject;
import greencode.jscript.function.implementation.EventFunction;
import greencode.jscript.function.implementation.SimpleFunction;
import greencode.jscript.window.annotation.AfterAction;
import greencode.jscript.window.annotation.BeforeAction;
import greencode.kernel.GreenCodeConfig.Internationalization;
import greencode.kernel.GreenCodeConfig.Internationalization.Variant;
import greencode.kernel.implementation.BootActionImplementation;
import greencode.util.ArrayUtils;
import greencode.util.FileUtils;
import greencode.util.GenericReflection;
import greencode.util.PackageUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;


@WebFilter(displayName="core", urlPatterns="/*")
public final class Core implements Filter {
	private static HashMap<String, Class<? extends Window>> registedWindows = new HashMap<String, Class<? extends Window>>();
	
	static String CONTEXT_PATH;
	static String projectName = null;
	
	boolean hasError;
	
	static String[] scriptJSFiles = {
		"json2.js",
		"greencode.js",
		"greencodeFunction.js",
		/*"crossbrowser.js",*/
		"iframeHttpRequest.js",
		"comet.js",
		"greenCodeStyle.js",
		"bootstrap.js",
		"init.js",
		/*,
		"Math.js",
		"String.js",
		"StringBuilder.js"*/
	};
	
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,	FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(GreenCodeConfig.View.getCharset());
		response.setCharacterEncoding(GreenCodeConfig.View.getCharset());
		response.setContentType("text/html;charset="+GreenCodeConfig.View.getCharset());
		
		if(hasError)
		{
			response.getWriter().write(LogMessage.getMessage("green-0000"));
			return;
		}
		
		final String servletPath = ((HttpServletRequest) request).getServletPath().substring(1);

		if(servletPath.equals("$synchronize"))
		{
			GreenContext context = new GreenContext(new HttpRequest((HttpServletRequest) request), (HttpServletResponse) response);
			
			Integer uid = Integer.parseInt(request.getParameter("uid"));
			
			HashMap<Integer, DOM> DOMList = DOMHandle.getDOMSync(context.getRequest().getViewSession());
			
			DOM dom = DOMList.get(uid);
			
			synchronized (dom) {
				DOMList.remove(uid);
				
				if(request.getParameterMap().containsKey("varName"))
				{
					String varName = request.getParameter("varName");
					String value = request.getParameter("var");
					
					DOMHandle.setVariableValue(dom, varName, value);
					
					Console.log("Synchronized:  [uid="+uid+", varName="+varName+", value="+value+"]");
				}else				
					Console.log("Synchronized:  [uid="+uid+":Not Found]");
				
				dom.notify();					
			}
			
			response.getWriter().close();
			return;
		}
		
		String controllerName = null;
		String methodName = null;
		Page page = null;
		
		Class<? extends HttpAction> requestClass = null;
		HttpAction requestController = null;
		
		Integer hashcodeRequestMethod = null;
		
		if(servletPath.indexOf('$') > -1)
		{
			String[] r = servletPath.split("\\$");
			controllerName = r[0];
			methodName = "init";
			hashcodeRequestMethod = new Integer(r[1]);
			
			requestClass = registedWindows.get(controllerName);
		} else if(servletPath.indexOf('@') > -1)
		{
			String[] r = servletPath.split("\\@");
			controllerName = r[0];
			methodName = r[1];
			
			if(methodName.equals("onLoad"))
			{
				throw new OperationNotAllowedException();
			}
			
			requestClass = registedWindows.get(controllerName);
		}else
		{
			page = Page.pages.get(servletPath);
			
			page = Page.pathAnalyze(servletPath, page);
			if(page != null)
			{
				if(page.window != null)
				{
					requestClass = page.window;
					controllerName = page.window.getSimpleName();
					methodName = "onLoad";
				}else
				{	
					response.getWriter().write(page.content);
					return;
				}
			}else
			{
				chain.doFilter(request, response);
				return;
			}
		}
		
		long startProcessTime = 0;
		
		Console.log(":: Request Processing ::");
		GreenContext context = new GreenContext((HttpServletRequest) request, (HttpServletResponse) response);
		try {
			requestController = Window.Context.getInstance((Class<Window>)requestClass, context);		
			context.currentWindow = (Window) requestController;
			
			if(hashcodeRequestMethod != null)
			{
				requestController = (HttpAction) Window.Context.getRegisteredFunctions((Window) requestController).get(hashcodeRequestMethod);
				requestClass = requestController.getClass();
			}
			
			Object[] listArgs = null;
			Method requestMethod = null;
			
			try {
				Class<?>[] listArgsClass = null;
				String[] _args = context.request.getParameterValues("_args[]");
				if((_args != null))
				{
					listArgs = new Object[_args.length];
					listArgsClass = new Class<?>[_args.length];
					for (int i = -1; ++i < _args.length;) {
						HashMap<String, String> j = context.getGsonInstance().fromJson(_args[i], (new HashMap<String, String>()).getClass());
						
						Class<?> _class = Class.forName(j.get("className"));
						j.remove("className");
						listArgsClass[i] = _class;								
						listArgs[i] = context.getGsonInstance().fromJson(j.get("fields"), _class);
					}

				}
				
				requestMethod = GenericReflection.getMethod(requestClass, methodName, listArgsClass);
				
				/*if(isPageAddress && requestMethod.isAnnotationPresent(End.class))
				{	
					End end = requestMethod.getAnnotation(End.class);

					HttpAction.Context.removeInstance((HttpAction) controller);
					
					if(end.conversation())
					{
						context.conversations.remove(context.getConversation().getId());
						context.conversation = new Conversation(context.request.getConversationId());
					}
					
					context.currentHttpAction = null;
					controller = HttpAction.Context.getInstance((Class<? extends HttpAction>)requestClass, context.getConversation());
					context.currentHttpAction = (HttpAction) controller;
					mainController = controller;
				}*/
			} catch (NoSuchMethodException e1) {
				throw new NoSuchMethodException(LogMessage.getMessage("green-0007", methodName, controllerName));
			}
			
			if(GreenCodeConfig.Console.writeLog())
				startProcessTime = System.currentTimeMillis();
			
			String classNameBootAction = null;
			boolean executeAfterAction = false;
			boolean executeBeforeAction = false;
			
			DatabaseConnectionEvent databaseConnectionEvent = null;
			
			if(GreenContext.bootAction != null)
			{
				classNameBootAction = GreenContext.bootAction.getClass().getSimpleName();
				executeBeforeAction = true;
				executeAfterAction = true;
			}
			
			if(requestMethod.isAnnotationPresent(BeforeAction.class))
			{
				BeforeAction a = requestMethod.getAnnotation(BeforeAction.class);
				executeBeforeAction = !a.disable();
			}
			
			if(requestMethod.isAnnotationPresent(AfterAction.class))
			{
				AfterAction a = requestMethod.getAnnotation(AfterAction.class);
				
				executeAfterAction = !a.disable();
			}
			
			if(requestMethod.isAnnotationPresent(Connection.class))
			{
				Connection cA = requestMethod.getAnnotation(Connection.class);
				if(GreenContext.classDatabaseConnectionEvent != null)
				{
					try {
						databaseConnectionEvent = (DatabaseConnectionEvent) GreenContext.classDatabaseConnectionEvent.newInstance();
					} catch (Exception e) {
						// Tratamento desnecessário
					}
					databaseConnectionEvent.beforeRequest(cA);
				} else
					startConnection(context, cA);
			}
			
			if(executeBeforeAction)
			{
				Console.log("Calling BeforeAction: ["+classNameBootAction+"]");
				context.executeAction = GreenContext.bootAction.beforeAction(context, requestMethod);
			}
			
			if(context.executeAction)
			{
				Console.log("Calling Action: ["+controllerName+":"+methodName+"]");
											
				if(requestController instanceof EventFunction)
					((EventFunction) requestController).init((EventObject) listArgs[0]);
				else if(requestController instanceof SimpleFunction)
					((SimpleFunction) requestController).init();
				else if(page != null)
				{
					String content = null;
					
					if(context.request.isAjax() && !page.pageAnnotation.ajaxSelector().isEmpty())
					{
						content = page.getAjaxSelectedContent(page.pageAnnotation.ajaxSelector());
					}else if(!page.pageAnnotation.selector().isEmpty())
					{
						content = page.getSelectedContent(page.pageAnnotation.selector());
					}else
						content = page.content;
										
					context.getResponse().getWriter().write(content);
					context.getResponse().getWriter().write("<script>viewId = "+context.getRequest().getViewSession().getId()+";</script>");
					
					((Window)requestController).onLoad();
				}
				else
					requestMethod.invoke(requestController, listArgs);
				
				ElementsScan.sendElements(context);
				
				if(databaseConnectionEvent != null)
					databaseConnectionEvent.afterRequest();
			}
			
			if(executeAfterAction)
			{
				Console.log("Calling AfterAction: ["+classNameBootAction+"]");
				GreenContext.bootAction.afterAction(context, requestMethod);
			}
			
			if(GreenCodeConfig.Console.writeLog())
				Console.log("Processing time: "+(System.currentTimeMillis()-startProcessTime)+ " ms");
		} catch (Exception e) {
			Console.error(e.getCause() == null ? e : e.getCause());
			
			JsonObject json = new JsonObject();
			json.add("errors", context.getGsonInstance().toJsonTree(Console.errors.get()));
			ElementsScan.send(context, json);
		} finally {
			Console.errors.set(null);
			GreenContext.greenContext.set(null);
		}
	}
	
	private void startConnection(GreenContext context, Connection cA) throws SQLException
	{
		DatabaseConfig config;
		
		if(!cA.value().isEmpty())
		{
			config = GreenCodeConfig.DataBase.getConfig(cA.value());
			context.getDatabaseConnection().setConfig(config);
		}else
		{
			DatabaseConfig defaultConfig = GreenCodeConfig.DataBase.getConfigs().get(GreenCodeConfig.DataBase.getDefaultConfigFile());
			config = new DatabaseConfig();
			
			if(cA.database().isEmpty())
			{
				config.setDatabase(defaultConfig.getDatabase());
			}else
			{
				config.setDatabase(cA.database());
			}
			
			if(cA.password().isEmpty())
			{
				config.setPassword(defaultConfig.getPassword());
			}else
			{
				config.setPassword(cA.password());
			}
			
			if(cA.schema().isEmpty())
			{
				config.setSchema(defaultConfig.getSchema());
			}else
			{
				config.setSchema(cA.schema());
			}
			
			if(cA.serverName().isEmpty())
			{
				config.setServerName(defaultConfig.getServerName());
			}else
			{
				config.setServerName(cA.serverName());
			}
			
			if(cA.userName().isEmpty())
			{
				config.setUserName(defaultConfig.getUserName());
			}else
			{
				config.setUserName(cA.userName());
			}
		}
		
		context.getDatabaseConnection().setConfig(config);		
		context.getDatabaseConnection().start();
	}

	public void init(FilterConfig fConfig) throws ServletException {
		projectName = new File(fConfig.getServletContext().getRealPath("/")).getName();
		CONTEXT_PATH = fConfig.getServletContext().getContextPath();
		
		System.out.println("\nLoading Project: ["+projectName+"]");
		
		try {
			GreenCodeConfig.load();
		} catch (IOException e1) {
			Console.error("Could not find file: src/greencode.config.xml");
			hasError = true;
			return;
		}
		
		Variant variant = Internationalization.getVariantLogByLocale(Locale.getDefault());
		
		if(variant == null)
			variant = Internationalization.getVariantLogByLocale(new Locale("pt", "BR"));
		
		if(variant != null)
		{
			try {
				LogMessage.instance.load(new InputStreamReader(variant.resource.openStream(), variant.charsetName));
			} catch (Exception e) {
				Console.error(e);
				hasError = true;
			}
			
			for (Variant v : Internationalization.pagesLocale) {				
				Properties p = new Properties();
				
				try {
					p.load(new InputStreamReader(v.resource.openStream(), v.charsetName));
					Message.properties.put(v.locale.toString(), p);
				} catch (Exception e) {
					Console.error(LogMessage.getMessage("green-0002", v.fileName));
					hasError = true;
					return;
				}
			}
		}
		
		if(GreenCodeConfig.DataBase.getDefaultConfigFile() != null)
			GreenCodeConfig.DataBase.getConfig(GreenCodeConfig.DataBase.getDefaultConfigFile());
				
		try {
			Class.forName("com.google.gson.Gson");
		} catch (ClassNotFoundException e) {
			Console.error(LogMessage.getMessage("green-0001"));
			hasError = true;
		}
		
		String defaultLogMsg = "["+Core.projectName+"] ";
		
		Class<BootActionImplementation> classBootAction = null;
		Class<DatabaseConnectionEvent> classDatabaseConnection = null;
		
		System.out.print(defaultLogMsg+"Caching Classes...");
		try {
			
			List<Class<?>> classesTeste = PackageUtils.getClasses("/", true);
			for (Class<?> Class : classesTeste) {
				
				if(classBootAction == null && ArrayUtils.contains(Class.getInterfaces(), BootActionImplementation.class))
				{
					classBootAction = (java.lang.Class<BootActionImplementation>) Class;
					continue;
				}else if(classDatabaseConnection == null && ArrayUtils.contains(Class.getInterfaces(), DatabaseConnectionEvent.class))
				{
					classDatabaseConnection = (java.lang.Class<DatabaseConnectionEvent>) Class;
					continue;
				}
				
				Class<?> parent = Class;
				while((parent = parent.getSuperclass()) != Object.class && parent != null)
				{
					if(parent.equals(Window.class))
					{
						//TODO: Vai ser utilizado modo bootable(Adaptar)
						Annotation.processWindowAnnotation((java.lang.Class<? extends Window>) Class);
						registedWindows.put(Class.getSimpleName(), (java.lang.Class<? extends Window>) Class);
					}
				}
			}
		} catch (IOException e) {
			Console.error(e);
			hasError = true;
		}
		System.out.print(" [done]");
		
		
		System.out.print(defaultLogMsg+"Copying JavaScript Tools...");
		try {
			File jScriptFolter = FileUtils.getFileInWebContent("jscript/");
			if(!jScriptFolter.exists())
				jScriptFolter.mkdir();
			
			File greencodeFolder = new File(jScriptFolter.getPath()+"/greencode/");
			if(!greencodeFolder.exists())
				greencodeFolder.mkdir();
			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			StringBuilder greencodeCore = new StringBuilder();
			greencodeCore.append("var CONTEXT_PATH = '"+fConfig.getServletContext().getContextPath()+"';");
			for (String fileName : scriptJSFiles)
			{
				greencodeCore.append(FileUtils.getContentFile(classLoader.getResource("greencode/jscript/files/"+fileName)));
			}
			FileUtils.createFile(greencodeCore.toString(), greencodeFolder.getPath()+"/core.js");
			
			for (Variant v : Internationalization.pagesLocale) {
				InputStream fstream = v.getResource().openStream();
				
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName(GreenCodeConfig.View.charset)));
									
				String strLine;
				StringBuilder str = new StringBuilder();
				
				while ((strLine = br.readLine()) != null)
				{	
					int indexOf = strLine.indexOf('=');
					if(indexOf != -1)
					{
						String key = strLine.substring(0, indexOf).trim();
						if(key.indexOf('#') != -1 || key.indexOf('!') != -1)
							continue;
						
						strLine = "internationalization_msg['"+key+"'] = '"+strLine.substring(indexOf+1, strLine.length()).trim()+"';";
						
						str.append(strLine);
					}
				}
				fstream.close();
				in.close();
				br.close();
							
				Page p = new Page();
				p.lastModified = new Date().getTime();
				p.content = str.toString();				
				
				Page.pages.put("jscript/greencode/msg_"+v.getLocale().toString()+".js", p);
				//FileUtils.createFile(str.toString(), greencodeFolder.getPath()+"/msg_"+v.getLocale().toString()+".js");
			}
		} catch (IOException e) {
			hasError = true;
			Console.error(e);
		}
		System.out.println(" [done]");
		
		if(GreenCodeConfig.View.getTemplateFile() != null)
		{
			System.out.println(defaultLogMsg+"Caching Default Template: "+GreenCodeConfig.View.getTemplateFile());
			try {
				GreenContext.defaultTemplate = FileUtils.getFileInWebContent(GreenCodeConfig.View.getTemplateFile());
			} catch (Exception e) {
				hasError = true;
				Console.error(LogMessage.getMessage("green-0002", GreenCodeConfig.View.getTemplateFile()));
			}
		}
		
		if(classDatabaseConnection != null)
		{
			try {
				System.out.print("Initializing Database Connection Event ...");
				GreenContext.classDatabaseConnectionEvent = classDatabaseConnection;
				System.out.println(" [done]");
			} catch (Exception e) {
				Console.error(e);
				hasError = true;
			}
		}else
		{
			DatabaseConnection db = null;
			
			db = new DatabaseConnection();
			
			db.setConfig(GreenCodeConfig.DataBase.getConfigs().get(GreenCodeConfig.DataBase.getDefaultConfigFile()));
			
			if(db.getConfig() != null)
			{			
				try {
					db.start();
				} catch (SQLException e) {
					Console.error(e);
					hasError = true;
					return;
				}
			}
			
			GreenCodeConfig.DataBase.showResultQuery = false;
		}
		
		if(classBootAction != null)
		{
			try {
				System.out.print(defaultLogMsg+"Initializing Boot Action ...");
				
				GreenContext.bootAction = (BootActionImplementation) classBootAction.newInstance();				
				GreenContext.bootAction.init();
				System.out.println(" [done]");
			} catch (Exception e) {
				Console.error(e);
				hasError = true;
			}
		}
	}
}
