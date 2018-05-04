package com.jrender.kernel;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jrender.database.DatabaseConnection;
import com.jrender.database.implementation.DatabaseConnectionEvent;
import com.jrender.exception.ConnectionLost;
import com.jrender.exception.JRenderError;
import com.jrender.exception.StopProcess;
import com.jrender.http.HttpAction;
import com.jrender.http.HttpRequest;
import com.jrender.jscript.DOM;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.DOMHandle.UIDReference;
import com.jrender.jscript.JSExecutor;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Form;
import com.jrender.jscript.dom.FunctionHandle;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.WindowHandle;
import com.jrender.jscript.dom.elements.custom.ContainerElement;
import com.jrender.jscript.dom.event.EventObject;
import com.jrender.jscript.dom.event.custom.ContainerEventObject;
import com.jrender.jscript.dom.form.annotation.Name;
import com.jrender.jscript.dom.function.implementation.EventFunction;
import com.jrender.jscript.dom.function.implementation.Function;
import com.jrender.jscript.dom.function.implementation.SimpleFunction;
import com.jrender.jscript.dom.window.annotation.AfterAction;
import com.jrender.jscript.dom.window.annotation.BeforeAction;
import com.jrender.jscript.dom.window.annotation.Destroy;
import com.jrender.jscript.dom.window.annotation.ForceSync;
import com.jrender.kernel.implementation.BootActionImplementation;
import com.jrender.kernel.implementation.PluginImplementation;
import com.jrender.util.ArrayUtils;
import com.jrender.util.ClassUtils;
import com.jrender.util.FileUtils;
import com.jrender.util.FileUtils.FileRead;
import com.jrender.util.GenericReflection;
import com.jrender.util.LogMessage;
import com.jrender.util.ObjectUtils;
import com.jrender.util.PackageUtils;

@WebFilter(displayName = "core", urlPatterns = "/*")
public final class Core implements Filter {
	final static String INIT_METHOD_NAME = "init";
	
	private final static Boolean HAS_ERROR = true;	
	private final static String[]	
	JS_SUPPORT_FILES = {
		"json3.js",
		"sizzle.js"
	}, JS_CORE_FILES = {
		"iframeHttpRequest.js",
		"request.js",
		"jrender.js",
		"jrender.jquery.js",
		"jrender.crossbrowser.js",
		"jrender.element.function.js",
		"jrender.util.js",
		"jrender.events.js",
		"jrender.tags.js",
		"jrender.style.js",
		"jrender.core.js",
	};
	
	final static String
		CONTEXT_PATH = null,
		PROJECT_NAME = null,
		DEFAULT_LOG_MSG = null,
		PROJECT_CONTENT_PATH = null,
		SRC_CORE_JS_FOR_SCRIPT_HTML = null,
		XML_CONFIG_FILE_NAME = "jrender.config.xml";
	
	final static CoreFileJS CORE_FILE_JS_OBJECT = null;
	
	final static Field requestField = GenericReflection.NoThrow.getDeclaredField(RequestFacade.class, "request");

	public void doFilter(final ServletRequest request, ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(JRenderConfig.Server.View.charset);
		response.setCharacterEncoding(JRenderConfig.Server.View.charset);
		response.setContentType("text/html;charset=" + JRenderConfig.Server.View.charset);

		if (HAS_ERROR) {
			response.getWriter().write(LogMessage.getMessage("0000"));
			return;
		}
		
		HttpServletRequest _request = ((HttpServletRequest) request);

		final String servletPath = _request.getServletPath().substring(1);
	
		if (servletPath.equals("coreWebSocket")) {
			request.setAttribute("httpResponse", response);
		}

		coreInit(servletPath, _request, response, chain, null);
	}

	static void coreInit(final String servletPath, final HttpServletRequest httpServletRequest, ServletResponse response, final FilterChain chain, final WebSocketData webSocketData) throws IOException, ServletException {
				
		if (servletPath.equals("$synchronize")) {
			DOMScanner.synchronize(servletPath, httpServletRequest, response, webSocketData);			
			return;
		}

		final boolean hasBootaction = Cache.bootAction != null;

		if (hasBootaction && !Cache.bootAction.onRequest(httpServletRequest, (HttpServletResponse) response)) {
			return;
		}

		final String controllerName, methodName;
		final FileWeb page;
		final Integer hashcodeRequestMethod;

		Class<? extends HttpAction> requestClass;
		if (servletPath.indexOf('$') > -1) {
			page = null;
			String[] r = servletPath.split("\\$", 2);
			controllerName = r[0];
			hashcodeRequestMethod = Integer.parseInt(r[1]);

			requestClass = Cache.registeredWindows.get(controllerName);
			methodName = INIT_METHOD_NAME;
		} else {
			hashcodeRequestMethod = null;

			if (servletPath.indexOf('@') > -1) {
				page = null;
				String[] r = servletPath.split("\\@", 2);
				controllerName = r[0];
				methodName = r[1];

				requestClass = Cache.registeredWindows.get(controllerName);
			} else {
				page = FileWeb.pathAnalyze(servletPath, FileWeb.files.get(servletPath), webSocketData == null ? httpServletRequest : new HttpRequest(httpServletRequest, response, webSocketData));

				if (page == null || page.window == null) {
					if (page == null)
						chain.doFilter(httpServletRequest, response);
					else
						response.getWriter().write(page.getContent(null));
					return;
				}

				requestClass = page.window;
				controllerName = page.window.getSimpleName();
				methodName = INIT_METHOD_NAME;
			}
		}

		final JRenderContext context = new JRenderContext(httpServletRequest, (HttpServletResponse) response, page, webSocketData);
		long processTime = 0;
		DatabaseConnectionEvent databaseConnectionEvent = null;
		try {			
			Console.log(":: REQUEST PROCESSING ::");
			if (JRenderConfig.Server.log)
				processTime = System.currentTimeMillis();
			
			final boolean hasAccess, isFirstRequest = context.request.isFirst();
			
			Class<?>[] listArgsClass = null;
			if (page != null) {
				hasAccess = Rule.forClass(context, page);
				
				if(hasAccess) {
					listArgsClass = new Class<?>[] { JRenderContext.class };
					String content;
					if (!isFirstRequest)
						content = page.router.ajaxSelector == null ? page.getContent(context) : page.getAjaxSelectedContent(page.router.ajaxSelector, context);
					else if (page.router.selector != null)
						content = page.getSelectedContent(page.router.selector, context);
					else
						content = page.getContent(context);
					
					Console.log("Requested Page: "+ servletPath);
					
					if (context.request.isWebSocket()) {
						webSocketData.session.getBasicRemote().sendText(DOMScanner.getMsgEventId(context.webSocketData)+content);
					} else {
						if (!isFirstRequest && com.jrender.http.$HttpRequest.contentIsHtml(context.request)) {
							content = "<ajaxcontent>" + content + "</ajaxcontent>";
						}
						context.response.getWriter().write(content);
					}

					String moduleName = FileWeb.getModuleName(page.router.jsModule);
					if (moduleName != null) {
						DOMScanner.registerExecution(new JSExecutor(context, "JRender.util.loadScript", JSExecutor.TYPE.METHOD, Core.CONTEXT_PATH + "/jscript/jrender/modules/" + moduleName + ".js", false, JRenderConfig.Server.View.charset));
					}
					
					if(isFirstRequest) {						
						if (context.userLocaleChanged) {
							DOMScanner.registerExecution(new JSExecutor(context, "JRender.util.loadScript", JSExecutor.TYPE.METHOD, Core.CONTEXT_PATH + "/jscript/jrender/msg_" + context.userLocale.toString() + ".js", false, JRenderConfig.Server.View.charset));
						}
						
						FunctionHandle fh = new FunctionHandle((Class<Window>) requestClass, INIT_METHOD_NAME);
						fh.registerRequestParameter("servletPath", servletPath);
						
						DOMScanner.registerExecution(new JSExecutor(context, "JRender.exec", JSExecutor.TYPE.METHOD, fh));
						DOMScanner.registerExecution(new JSExecutor(context, "JRender.currentViewId", JSExecutor.TYPE.PROPERTY, context.request.getViewSession().getId()));
						
						throw new StopProcess();
					}
				} else {
					Console.log(LogMessage.getMessage("0043", servletPath));
				}
			} else {
				hasAccess = true;
			}
			
			Method requestedMethod = null;
			Object[] listArgs = null;
			
			if (hashcodeRequestMethod == null && hasAccess && methodName.equals(INIT_METHOD_NAME)) {
				context.requestedMethod = requestedMethod = GenericReflection.getMethod(requestClass, methodName, new Class<?>[]{JRenderContext.class});
				if (requestedMethod.isAnnotationPresent(Destroy.class)) {
					WindowHandle.removeInstance((Class<? extends Window>) requestClass, context.request.getConversation());
				}
			}
			
			HttpAction requestController = WindowHandle.getInstance((Class<Window>) requestClass, context.request.getConversation());
			if (context.currentWindow == null)
				context.currentWindow = (Window) requestController;
			
			if(!hasAccess) {
				Rule.runAuthorizationMethod(context);
				throw new StopProcess();
			}
			
			// Multipart System
			{
				MultipartConfig multipartConfig = null;
				if (((JRenderConfig.Server.Request.Multipart.autodectetion) || ((multipartConfig = requestClass.getAnnotation(MultipartConfig.class)) != null)) && httpServletRequest.getContentType() != null && httpServletRequest.getContentType().indexOf("multipart/form-data") > -1) {

					Request _request = (Request) GenericReflection.NoThrow.getValue(requestField, httpServletRequest);
					_request.getContext().setAllowCasualMultipartParsing(true);
					_request.getConnector().setMaxPostSize((int) (multipartConfig != null ? multipartConfig.maxRequestSize() : JRenderConfig.Server.Request.Multipart.maxRequestSize));
				}
			}

			final Map<Integer, Function> registeredFunctions;
			if (hashcodeRequestMethod != null) {
				registeredFunctions = com.jrender.jscript.dom.$Window.getRegisteredFunctions((Window) requestController);
				HttpAction _requestController = (HttpAction) registeredFunctions.get(hashcodeRequestMethod);
				if (_requestController == null) {
					if(Cache.bootAction != null) {
						Cache.bootAction.onRegisteredEventLost(context, (Window)requestController);
					}
					throw new StopProcess();
				} else {
					requestController = _requestController;
					_requestController = null;
				}

				requestClass = requestController.getClass();
			} else
				registeredFunctions = null;

			com.jrender.kernel.Form.processRequestedForm(context);

			try {
				if(requestedMethod == null) {
					if (!isFirstRequest && listArgsClass == null) {
						String[] _args = context.request.getParameterValues("_args[]");
						if (_args != null) {
							final int _argsSize = _args.length;
	
							DOMScanner eArg = DOMScanner.getElements(context.request.getViewSession());
	
							eArg.args = new Integer[_argsSize];
							listArgs = new Object[_argsSize];
							listArgsClass = new Class<?>[_argsSize];
	
							int cntSkip = 0;
							for (int i = -1; ++i < _argsSize;) {
								final Map<String, String> j = context.gsonInstance.fromJson(_args[i], (new HashMap<String, String>()).getClass());
	
								final Class<?> _class = Class.forName(j.get("className"));
								listArgsClass[i] = _class;
	
								if (_class.equals(JRenderContext.class)) {
									++cntSkip;
									listArgs[i] = context;
								} else {
									final DOM dom;
									if (_class.equals(ContainerEventObject.class)) {
										dom = new ContainerEventObject(context, Integer.parseInt(j.get("uid")));
										++cntSkip;
									} else if (_class.equals(ContainerElement.class)) {
										dom = com.jrender.jscript.dom.$Container.getContainers(context.requestedForm).get(Integer.parseInt(j.get("uid")));
										listArgsClass[i] = dom.getClass();
										++cntSkip;
									} else if (_class.equals(Element.class)) {
										Class<? extends Element> castTo = (Class<? extends Element>) Class.forName(j.get("castTo"));
										dom = ElementHandle.getInstance(castTo, context.currentWindow);
										com.jrender.jscript.$DOMHandle.setUID(dom, Integer.parseInt(j.get("uid")));
										listArgsClass[i] = castTo;
										++cntSkip;
									} else {
										dom = (DOM) context.gsonInstance.fromJson(j.get("fields"), _class);
										eArg.args[i - cntSkip] = DOMHandle.getUID(dom);
									}
									listArgs[i] = dom;
								}
							}
						}
					}
	
					context.requestedMethod = requestedMethod = GenericReflection.getMethod(requestClass, methodName, listArgsClass);
				}

				Rule.forMethod(context, requestedMethod);
			} catch (NoSuchMethodException e1) {
				throw new NoSuchMethodException(LogMessage.getMessage("0007", methodName, controllerName));
			}

			String classNameBootAction = null;

			ForceSync fs = requestedMethod.getAnnotation(ForceSync.class);
			if (fs != null) {
				context.forceSynchronization = true;
				context.listAttrSync = fs.value();
				if (fs.onlyOnce())
					context.listAttrSyncCache = new HashMap<Integer, HashSet<String>>();
			}

			if (hasBootaction) {
				classNameBootAction = Cache.bootAction.getClass().getSimpleName();

				BeforeAction a = requestedMethod.getAnnotation(BeforeAction.class);
				if (a == null || !a.disable()) {
					Console.log("Calling BeforeAction: [" + classNameBootAction + "]");
					context.executeAction = Cache.bootAction.beforeAction(context, requestedMethod);
				}
			}

			if (context.executeAction) {
				final String eventType = httpServletRequest.getParameter("eventType");
				Console.log("Calling Action: [" + controllerName + ":" + methodName + "]" + (eventType == null ? "" : "[EventType: " + eventType + "]"));

				try {
					databaseConnectionEvent = ActionLoader.connection(context, requestedMethod);

					ActionLoader.process(context, requestController, requestedMethod);

					if (context.executeAction) {
						if (requestController instanceof Window && methodName.equals(Core.INIT_METHOD_NAME)) {
							((Window) requestController).init(context);

							String moduleName = FileWeb.getModuleName(context.currentRouter().jsModule);
							if (moduleName != null) {
								DOMHandle.execCommand(context.currentWindow, "JRender.modules." + moduleName + ".call", context.currentWindow.principalElement(), context.currentWindow.principalElement(), context.request.getViewSession().getId(), context.request.getConversationId());
							}
						} else if (requestController instanceof EventFunction)
							((EventFunction) requestController).init((EventObject) listArgs[0]);
						else if (requestController instanceof SimpleFunction)
							((SimpleFunction) requestController).init(context);
						else
							requestedMethod.invoke(requestController, listArgs);
					}
				} catch (ConnectionLost e) {
					Console.warning(e.getMessage());
				}

				if (databaseConnectionEvent != null)
					databaseConnectionEvent.afterRequest(context);

				if (hasBootaction) {
					AfterAction a = requestedMethod.getAnnotation(AfterAction.class);

					if (a == null || !a.disable()) {
						Console.log("Calling AfterAction: [" + classNameBootAction + "]");
						Cache.bootAction.afterAction(context, requestedMethod);
					}
				}
			} else
				Console.warning(Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1) + " method will not run.");

			if (context.userLocaleChanged) {
				DOMHandle.execCommand(context.currentWindow, "JRender.util.loadScript", Core.CONTEXT_PATH + "/jscript/jrender/msg_" + context.userLocale.toString() + ".js", false, JRenderConfig.Server.View.charset);
			}
			if (databaseConnectionEvent != null)
				databaseConnectionEvent.onSuccess(context);

			if (requestedMethod.isAnnotationPresent(Destroy.class)) {
				if (registeredFunctions != null)
					registeredFunctions.remove(hashcodeRequestMethod);
				else if (!methodName.equals(INIT_METHOD_NAME))
					WindowHandle.removeInstance((Class<? extends Window>) requestClass, context.request.getConversation());
			}

		} catch (StopProcess e) {
		} catch (Exception e) {
			System.err.println(Console.msgError);
			e.printStackTrace();
			
			if (Cache.bootAction != null) {
				Cache.bootAction.onException(context, e);
			}

			if (databaseConnectionEvent != null) {
				databaseConnectionEvent.onError(context, e);			
			}
			
			if(JRenderConfig.Client.printExceptionServer) {
				Throwable thr = e.getCause() == null ? e : e.getCause();
	
				JsonArray stackTrace = new JsonArray();
				JsonObject error = new JsonObject();
				error.add("stackTrace", stackTrace);
	
				error.addProperty("className", thr.getClass().getName());
				error.addProperty("message", thr.getMessage());
	
				for (StackTraceElement trace : thr.getStackTrace()) {
					if (trace == null)
						continue;
	
					JsonObject o = new JsonObject();
					o.addProperty("className", trace.getClassName());
					o.addProperty("methodName", trace.getMethodName());
					o.addProperty("lineNumber", trace.getLineNumber());
					o.addProperty("fileName", trace.getFileName());
					try {
						ClassLoader cl = Class.forName(trace.getClassName()).getClassLoader();
						if (cl == null)
							throw new ClassNotFoundException();
	
						o.addProperty("possibleError", cl.getResource("/") != null);
					} catch (ClassNotFoundException e1) {
						o.addProperty("possibleError", false);
					}
					stackTrace.add(o);
				}
				
				JsonObject json = new JsonObject();
				json.add("error", error);
				DOMScanner.send(context, json);
			}
		} finally {
			DOMScanner.sendElements(context);
			context.destroy();
		}

		if (JRenderConfig.Server.log) {
			processTime = System.currentTimeMillis() - processTime;
			int ms = (int) ((processTime) % 1000);
			int seconds = (int) ((processTime / 1000) % 60);
			long minutes = ((processTime - seconds) / 1000) / 60;

			StringBuilder pt = new StringBuilder("::  PROCESSING TIME   :: ");
			if (minutes > 0)
				pt.append(minutes + "m ");

			if (seconds > 0)
				pt.append(seconds + "s ");

			if (ms > 0 || minutes == 0 && seconds == 0)
				pt.append(ms + "ms");

			Console.print(pt.toString());
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		GenericReflection.NoThrow.setFinalStaticValue(Core.class, "PROJECT_NAME", new File(fConfig.getServletContext().getRealPath("/")).getName());
		GenericReflection.NoThrow.setFinalStaticValue(Core.class, "DEFAULT_LOG_MSG", "[" + Core.PROJECT_NAME + "] ");
		GenericReflection.NoThrow.setFinalStaticValue(Core.class, "CONTEXT_PATH", fConfig.getServletContext().getContextPath());
		GenericReflection.NoThrow.setFinalStaticValue(Core.class, "SRC_CORE_JS_FOR_SCRIPT_HTML", Core.CONTEXT_PATH + "/jscript/jrender/core.js");
		GenericReflection.NoThrow.setFinalStaticValue(Core.class, "PROJECT_CONTENT_PATH", fConfig.getServletContext().getRealPath("").replaceAll("\\\\", "/"));
		
		System.out.println("\nLoading Project: [" + PROJECT_NAME + "]");		
		
		try {
			try {
				JRenderConfig.load();
			} catch (IOException e1) {
				throw new IOException("Could not find file: src/jrender.config.xml");
			}

			JRenderConfig.Server.Internationalization.Variant variant = JRenderConfig.Server.Internationalization.getVariantLogByLocale(Locale.getDefault());

			if (variant == null)
				variant = JRenderConfig.Server.Internationalization.getVariantLogByLocale(new Locale("pt", "BR"));

			if (variant != null) {
				com.jrender.util.$LogMessage.getInstace().load(new InputStreamReader(variant.resource.openStream(), variant.charsetName));

				for (JRenderConfig.Server.Internationalization.Variant v : JRenderConfig.Server.Internationalization.pagesLocale) {
					try {
						Properties p = new Properties();
						p.load(new InputStreamReader(v.resource.openStream(), v.charsetName));
						Message.properties.put(v.locale.toString(), p);
					} catch (Exception e) {
						throw new IOException(LogMessage.getMessage("0002", v.fileName));
					}
				}
			}

			if (JRenderConfig.Server.DataBase.defaultConfigFile != null)
				JRenderConfig.Server.DataBase.getConfig(JRenderConfig.Server.DataBase.defaultConfigFile);

			try {
				Class.forName("com.google.gson.Gson");
			} catch (ClassNotFoundException e) {
				throw new ClassNotFoundException(LogMessage.getMessage("0001"));
			}

			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			System.out.print(DEFAULT_LOG_MSG + "Copying JavaScript Tools...");

			File jScriptFolter = FileUtils.getFileInWebContent("jscript/");
			if (!jScriptFolter.exists())
				jScriptFolter.mkdir();

			final File jrenderFolder = new File(jScriptFolter.getPath() + "/jrender/");
			if (!jrenderFolder.exists())
				jrenderFolder.mkdir();

			final String jrenderPath = jrenderFolder.getPath();

			final CoreFileJS coreFileJS = new CoreFileJS(jrenderPath);
			GenericReflection.NoThrow.setFinalStaticValue(Core.class, "CORE_FILE_JS_OBJECT", coreFileJS);

			for (String fileName : JS_CORE_FILES) {
				coreFileJS.append(classLoader.getResource("com/jrender/jscript/files/" + fileName));
			}
			
			{			
				JsonObject json = new JsonObject();
				
				JsonObject className = new JsonObject();
				className.addProperty("context", JRenderContext.class.getName());
				className.addProperty("containerElement", ContainerElement.class.getName());
				className.addProperty("containerEventObject", ContainerEventObject.class.getName());
				className.addProperty("element", Element.class.getName());
				
				json.add("className", className);
				
				json.addProperty("CONTEXT_PATH", Core.CONTEXT_PATH);
				json.addProperty("DEBUG_LOG", JRenderConfig.Client.debugLog);
				json.addProperty("EVENT_REQUEST_TYPE", JRenderConfig.Server.Request.type);
				json.addProperty("REQUEST_SINGLETON", JRenderConfig.Client.websocketSingleton);
				json.addProperty("WEBSOCKET_PORT", JRenderConfig.Server.Request.Websocket.port);
				
				for (UIDReference uid : UIDReference.values()) {
					json.addProperty(uid.name(), uid.ordinal());
				}
				
				JsonObject executorType = new JsonObject();
				
				for (JSExecutor.TYPE type : JSExecutor.TYPE.values()) {
					executorType.addProperty(type.name(), type.ordinal());
				}
				
				if(JRenderConfig.Client.parameters != null) {
					JsonObject parameters = new JsonObject();
					for(Entry<String, String> entry : JRenderConfig.Client.parameters.entrySet()){
						String value = entry.getValue();
						if(ObjectUtils.isBoolean(value)) {
							parameters.addProperty(entry.getKey(), Boolean.parseBoolean(value));
						}else if(ObjectUtils.isBoolean(value)) {
							parameters.addProperty(entry.getKey(), NumberFormat.getInstance().parse(value));
						} else {
							parameters.addProperty(entry.getKey(), value);
						}
						
					}
					json.add("parameters", parameters);
				}
				
				json.add("executorType", executorType);

				coreFileJS.append("JRender.jQuery.extend(JRender,").append(json).append(");");

				coreFileJS.save();
			}

			for (String fileName : JS_SUPPORT_FILES) {
				FileUtils.createFile(FileUtils.getContentFile(classLoader.getResource("com/jrender/jscript/files/" + fileName)), jrenderPath + "/" + fileName);
			}
			
			final Charset charset = Charset.forName(JRenderConfig.Server.View.charset);
			final long currentTime = new Date().getTime();
			for (JRenderConfig.Server.Internationalization.Variant v : JRenderConfig.Server.Internationalization.pagesLocale) {
				FileWeb p = new FileWeb();
				p.lastModified = currentTime;

				p.setContent(FileUtils.getContentFile(v.resource, charset, new FileRead() {
					public String reading(String line) {
						final int indexOf = line.indexOf('=');
						if (indexOf != -1) {
							final String key = line.substring(0, indexOf).trim();
							if (!(key.indexOf('#') != -1 || key.indexOf('!') != -1)) // Comment
								return "JRender.internationalProperty['" + key + "'] = '" + line.substring(indexOf + 1, line.length()).trim() + "';";
						}

						return null;
					}
				}));

				FileWeb.files.put("jscript/jrender/msg_" + v.locale.toString() + ".js", p);
			}

			System.out.println(" [done]");
			
			if (JRenderConfig.Server.View.templatePaths != null) {
				System.out.println(DEFAULT_LOG_MSG + "Caching Template(s)...");

				for (Entry<String, String> entry : JRenderConfig.Server.View.templatePaths.entrySet()) {
					File f = FileUtils.getFileInWebContent(entry.getValue());

					if (!f.exists())
						throw new IOException(LogMessage.getMessage("0002", entry.getValue()));

					Cache.templates.put(entry.getKey(), f);

					StringBuilder str = new StringBuilder(DEFAULT_LOG_MSG + "Template: " + entry.getValue());
					if (Cache.defaultTemplate == null && entry.getValue().equals(JRenderConfig.Server.View.defaultTemplatePath)) {
						GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "defaultTemplate", f);
						str.append(" (Default)");
					}

					System.out.println(str.toString());
				}
			}

			System.out.print(DEFAULT_LOG_MSG + "Caching Classes...");
			Class<BootActionImplementation> classBootAction = null;
			Class<DatabaseConnectionEvent> classDatabaseConnection = null;
			List<Class<?>> classesTeste = PackageUtils.getClasses("/", true);
			for (Class<?> clazz : classesTeste) {
				if (classBootAction == null && ArrayUtils.contains(clazz.getInterfaces(), BootActionImplementation.class)) {
					classBootAction = (java.lang.Class<BootActionImplementation>) clazz;
				} else if (classDatabaseConnection == null && ArrayUtils.contains(clazz.getInterfaces(), DatabaseConnectionEvent.class)) {
					classDatabaseConnection = (java.lang.Class<DatabaseConnectionEvent>) clazz;
				} else if (ClassUtils.isParent(clazz, Form.class) && !Modifier.isAbstract(clazz.getModifiers())) {
					if (!clazz.isAnnotationPresent(Name.class))
						throw new IllegalAccessException(LogMessage.getMessage("0027", clazz));

					final String name = clazz.getAnnotation(Name.class).value();

					if (Cache.forms.containsKey(name))
						throw new Exception(LogMessage.getMessage("0031", name, clazz.getSimpleName()));

					Cache.forms.put(name, (java.lang.Class<? extends Form>) clazz);
				} else if (ClassUtils.isParent(clazz, Window.class) && !Modifier.isAbstract(clazz.getModifiers())) {
					Annotation.processWindowAnnotation((java.lang.Class<? extends Window>) clazz, classLoader, jrenderFolder);
					Cache.registeredWindows.put(clazz.getSimpleName(), (java.lang.Class<? extends Window>) clazz);
				}
			}
			classesTeste.clear();
			System.out.print(" [done]\n");

			if (classDatabaseConnection != null) {
				System.out.print(DEFAULT_LOG_MSG + "Initializing Database Connection Event ...");
				GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "classDatabaseConnectionEvent", classDatabaseConnection);
				System.out.println(" [done]");
			} else {
				System.out.print(DEFAULT_LOG_MSG + "Testing Database Connection ...");
				DatabaseConnection db = new DatabaseConnection();
				if (db.getConfig() != null) {
					db.start();
					db.close();
				}
				System.out.println(" [done]");
			}

			if (JRenderConfig.Server.Plugins.list != null) {
				System.out.print(DEFAULT_LOG_MSG + "Initializing Plugins ...");

				PluginImplementation[] list = new PluginImplementation[JRenderConfig.Server.Plugins.list.length];
				for (int i = -1; ++i < list.length;) {
					PluginImplementation plugin = JRenderConfig.Server.Plugins.list[i].newInstance();
					plugin.init(jrenderPath, classLoader, fConfig.getServletContext(), coreFileJS);
					list[i] = plugin;
				}

				GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "plugins", list);

				System.out.println(" [done]");
			}
			
			if (classBootAction != null) {
				System.out.print(DEFAULT_LOG_MSG + "Initializing Boot Action ...");

				GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "bootAction", (BootActionImplementation) classBootAction.newInstance());

				Cache.bootAction.init(jrenderPath, classLoader, fConfig.getServletContext(), coreFileJS);
				System.out.println(" [done]");
			}			

			GenericReflection.NoThrow.setFinalStaticValue(Core.class, "HAS_ERROR", false);
		} catch (Exception e) {
			throw new JRenderError(e);
		}
	}

	public void destroy() {
		if (Cache.bootAction != null) {
			System.out.print(DEFAULT_LOG_MSG + "Destroying BootAction...");
			Cache.bootAction.destroy();
			System.out.print(" [done]\n");
		}

		if (Cache.plugins != null) {
			System.out.print(DEFAULT_LOG_MSG + "Destroying Plugins...");
			for (PluginImplementation plugin : Cache.plugins)
				plugin.destroy();
			System.out.print(" [done]\n");
		}

		if (!com.jrender.http.$HttpRequest.getGlobalViewList().isEmpty()) {
			System.out.print(DEFAULT_LOG_MSG + "Destroying " + com.jrender.http.$HttpRequest.getGlobalViewList().size() + " View(s)...");
			while (!com.jrender.http.$HttpRequest.getGlobalViewList().isEmpty())
				com.jrender.http.$HttpRequest.getGlobalViewList().get(0).invalidate();
			System.out.print(" [done]\n");
		}

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				System.out.print(DEFAULT_LOG_MSG + String.format("Deregistering jdbc driver: %s\n", driver));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static BootActionImplementation getBoot() {
		return Cache.bootAction;
	}
}
