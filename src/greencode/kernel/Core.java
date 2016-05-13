package greencode.kernel;

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
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import greencode.database.DatabaseConnection;
import greencode.database.implementation.DatabaseConnectionEvent;
import greencode.exception.ConnectionLost;
import greencode.exception.GreencodeError;
import greencode.exception.OperationNotAllowedException;
import greencode.exception.StopProcess;
import greencode.http.HttpAction;
import greencode.http.HttpRequest;
import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Form;
import greencode.jscript.Window;
import greencode.jscript.WindowHandle;
import greencode.jscript.elements.custom.ContainerElement;
import greencode.jscript.event.EventObject;
import greencode.jscript.event.custom.ContainerEventObject;
import greencode.jscript.form.annotation.Name;
import greencode.jscript.function.implementation.EventFunction;
import greencode.jscript.function.implementation.Function;
import greencode.jscript.function.implementation.SimpleFunction;
import greencode.jscript.window.annotation.AfterAction;
import greencode.jscript.window.annotation.BeforeAction;
import greencode.jscript.window.annotation.Destroy;
import greencode.jscript.window.annotation.ForceSync;
import greencode.jscript.window.annotation.PageParameter;
import greencode.kernel.implementation.BootActionImplementation;
import greencode.kernel.implementation.PluginImplementation;
import greencode.util.ArrayUtils;
import greencode.util.ClassUtils;
import greencode.util.FileUtils;
import greencode.util.FileUtils.FileRead;
import greencode.util.GenericReflection;
import greencode.util.PackageUtils;

@ServerEndpoint(value = "/coreWebSocket", configurator = WebSocketConfigurator.class)
@WebFilter(displayName = "core", urlPatterns = "/*")
public final class Core implements Filter {
	private final static String INIT_METHOD_NAME = "init";
	private final static Boolean HAS_ERROR = true;
	private final static String[] JS_SUPPORT_FILES = {
		"json3.js",
		"sizzle.js"
	};

	private final static String[] JS_CORE_FILES = {
		"greencode.js",
		"greencodeFunction.js",
		"greencodeEvents.js",
		"greencodeTags.js",
		"greenCodeStyle.js",
		"iframeHttpRequest.js",
		"request.js",
		"bootstrap.js",
		"init.js"
	};
	final static String CONTEXT_PATH = null, projectName = null, defaultLogMsg = null, SRC_CORE_JS_FOR_SCRIPT_HTML = null;
	final static Field requestField = GenericReflection.NoThrow.getDeclaredField(RequestFacade.class, "request");

	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		try {
			this.request = (HttpServletRequest) config.getUserProperties().get("httpRequest");
			this.response = (HttpServletResponse) config.getUserProperties().get("httpResponse");
			this.session = (HttpSession) config.getUserProperties().get("httpSession");

			Request _request = (Request) GenericReflection.NoThrow.getValue(Core.requestField, this.request);

			GenericReflection.setValue(Request.class, "context", config.getUserProperties().get("context"), _request);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) throws IOException, ServletException {
		final WebSocketData wsData = new Gson().fromJson(message, WebSocketData.class);

		wsData.httpSession = this.session;
		wsData.session = session;

		wsData.localPort = (Integer) session.getUserProperties().get("localPort");
		wsData.remoteHost = (String) session.getUserProperties().get("remoteHost");
		wsData.requestURI = wsData.url;
		wsData.requestURL = new StringBuffer("http://").append(wsData.remoteHost).append(":").append(wsData.localPort).append(wsData.url);

		try {
			final String servletPath = wsData.url.substring(Core.CONTEXT_PATH.length() + 1);
			if (servletPath.equals("$synchronize")) {
				Core.coreInit(servletPath, request, response, null, wsData);
			} else {
				new Thread(new Runnable() {
					public void run() {
						try {
							Core.coreInit(servletPath, request, response, null, wsData);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
	}

	@OnError
	public void onError(Session session, Throwable thr) {
	}

	@Override
	public void doFilter(final ServletRequest request, ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(GreenCodeConfig.Server.View.charset);
		response.setCharacterEncoding(GreenCodeConfig.Server.View.charset);
		response.setContentType("text/html;charset=" + GreenCodeConfig.Server.View.charset);

		if (HAS_ERROR) {
			response.getWriter().write(LogMessage.getMessage("green-0000"));
			return;
		}

		final String servletPath = ((HttpServletRequest) request).getServletPath().substring(1);

		if (servletPath.equals("coreWebSocket")) {
			request.setAttribute("httpResponse", response);
		}

		coreInit(servletPath, request, response, chain, null);
	}

	static void coreInit(final String servletPath, final ServletRequest request, ServletResponse response, final FilterChain chain, final WebSocketData webSocketData) throws IOException, ServletException {
		if (servletPath.equals("$synchronize")) {
			GreenContext context = new GreenContext((HttpServletRequest) request, (HttpServletResponse) response, null, webSocketData);

			final Integer uid = Integer.parseInt(context.request.getParameter("uid"));

			HttpRequest __request = context.getRequest();

			HashMap<Integer, DOM> DOMList = greencode.jscript.$DOMHandle.getDOMSync(__request.getViewSession());

			DOM dom = DOMList.get(uid);

			synchronized (dom) {
				DOMList.remove(uid);

				final String varName = __request.getParameter("varName");
				if (varName != null) {
					Object value = varName.indexOf("$$_file_") == 0 ? __request.getPart(varName) : __request.getParameter("var");

					DOMHandle.setVariableValue(dom, varName, value);

					Console.log("Synchronized:  [uid=" + uid + ", varName=" + varName + ", value=" + value + "]");
				} else
					Console.log("Synchronized:  [uid=" + uid + ":Not Found]");

				dom.notify();
			}

			if (webSocketData == null)
				response.getWriter().close();
			return;
		}

		final boolean hasBootaction = Cache.bootAction != null;

		if (hasBootaction)
			Cache.bootAction.onRequest((HttpServletRequest) request, (HttpServletResponse) response);

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

				if (methodName.equals(INIT_METHOD_NAME))
					throw new OperationNotAllowedException();

				requestClass = Cache.registeredWindows.get(controllerName);
			} else {
				page = FileWeb.pathAnalyze(servletPath, FileWeb.files.get(servletPath), (HttpServletRequest) request);

				if (page == null || page.window == null) {
					if (page == null)
						chain.doFilter(request, response);
					else
						response.getWriter().write(page.getContent(null));
					return;
				}

				requestClass = page.window;
				controllerName = page.window.getSimpleName();
				methodName = INIT_METHOD_NAME;
			}
		}

		long processTime = 0;

		DatabaseConnectionEvent databaseConnectionEvent = null;

		// Multipart System
		{
			MultipartConfig multipartConfig = null;
			if (((GreenCodeConfig.Server.Request.Multipart.autodectetion) || ((multipartConfig = requestClass.getAnnotation(MultipartConfig.class)) != null)) && request.getContentType() != null && request.getContentType().indexOf("multipart/form-data") > -1) {

				Request _request = (Request) GenericReflection.NoThrow.getValue(requestField, request);
				_request.getContext().setAllowCasualMultipartParsing(true);
				_request.getConnector().setMaxPostSize((int) (multipartConfig != null ? multipartConfig.maxRequestSize() : GreenCodeConfig.Server.Request.Multipart.maxRequestSize));
			}
		}

		Console.log(":: Request Processing ::");
		final GreenContext context = new GreenContext((HttpServletRequest) request, (HttpServletResponse) response, page, webSocketData);

		final Basic basicRemote = webSocketData != null ? webSocketData.getSession().getBasicRemote() : null;

		try {
			Class<?>[] listArgsClass = null;
			if (page != null) {
				listArgsClass = new Class<?>[] { GreenContext.class };

				Rule.forClass(context, page);

				if (!(page.pageAnnotation.parameters().length == 1 && page.pageAnnotation.parameters()[0].name().isEmpty())) {
					for (PageParameter p : page.pageAnnotation.parameters())
						greencode.http.$HttpRequest.getParameters(context.request).put(p.name(), new String[] { p.value() });
				}

				String content;
				if (context.request.isAjax())
					content = page.pageAnnotation.ajaxSelector().isEmpty() ? page.getContent(context) : page.getAjaxSelectedContent(page.pageAnnotation.ajaxSelector(), context);
				else if (!page.pageAnnotation.selector().isEmpty())
					content = page.getSelectedContent(page.pageAnnotation.selector(), context);
				else
					content = page.getContent(context);
				if (basicRemote != null) {
					basicRemote.sendText(content);
				} else {
					if (greencode.http.$HttpRequest.__contentIsHtml(context.request)) {
						content = "<ajaxcontent>" + content + "</ajaxcontent>";
					}

					context.getResponse().getWriter().write(content);
				}
			}

			HttpAction requestController = WindowHandle.getInstance((Class<Window>) requestClass, context.getRequest().getConversation());
			if (context.currentWindow == null)
				context.currentWindow = (Window) requestController;

			final Map<Integer, Function> registeredFunctions;
			if (hashcodeRequestMethod != null) {
				registeredFunctions = greencode.jscript.$Window.getRegisteredFunctions((Window) requestController);
				requestController = (HttpAction) registeredFunctions.get(hashcodeRequestMethod);
				if (requestController == null) {

				}

				requestClass = requestController.getClass();
			} else
				registeredFunctions = null;

			Object[] listArgs = null;
			Method requestMethod = null;

			greencode.kernel.Form.processRequestedForm(context);

			try {
				if (context.request.isAjax() && listArgsClass == null) {
					String[] _args = context.request.getParameterValues("_args[]");
					if (_args != null) {
						final int _argsSize = _args.length;

						ElementsScan eArg = ElementsScan.getElements(context.getRequest().getViewSession());

						eArg.args = new Integer[_argsSize];
						listArgs = new Object[_argsSize];
						listArgsClass = new Class<?>[_argsSize];

						int cntSkip = 0;
						for (int i = -1; ++i < _argsSize;) {
							final HashMap<String, String> j = context.gsonInstance.fromJson(_args[i], (new HashMap<String, String>()).getClass());

							final Class<?> _class = Class.forName(j.get("className"));
							listArgsClass[i] = _class;

							if (_class.equals(GreenContext.class)) {
								++cntSkip;
								listArgs[i] = context;
							} else {
								final DOM dom;
								if (_class.equals(ContainerEventObject.class)) {
									dom = new ContainerEventObject(context, Integer.parseInt(j.get("uid")));
									++cntSkip;
								} else if (_class.equals(ContainerElement.class)) {
									dom = greencode.jscript.$Container.getContainers(context.requestedForm).get(Integer.parseInt(j.get("uid")));
									listArgsClass[i] = dom.getClass();
									++cntSkip;
								} else if (_class.equals(Element.class)) {
									Class<? extends Element> castoTo = (Class<? extends Element>) Class.forName(j.get("castTo"));
									dom = ElementHandle.getInstance(castoTo, context.currentWindow);
									greencode.jscript.$DOMHandle.setUID(dom, Integer.parseInt(j.get("uid")));
									listArgsClass[i] = castoTo;
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

				context.requestedMethod = requestMethod = GenericReflection.getMethod(requestClass, methodName, listArgsClass);

				Rule.forMethod(context, requestMethod);
			} catch (NoSuchMethodException e1) {
				throw new NoSuchMethodException(LogMessage.getMessage("green-0007", methodName, controllerName));
			}

			if (GreenCodeConfig.Server.writeLog)
				processTime = System.currentTimeMillis();

			String classNameBootAction = null;

			ForceSync fs = requestMethod.getAnnotation(ForceSync.class);
			if (fs != null) {
				context.forceSynchronization = true;
				context.listAttrSync = fs.value();
				if (fs.onlyOnce())
					context.listAttrSyncCache = new HashMap<Integer, HashSet<String>>();
			}

			if (hasBootaction) {
				classNameBootAction = Cache.bootAction.getClass().getSimpleName();

				BeforeAction a = requestMethod.getAnnotation(BeforeAction.class);
				if (a == null || !a.disable()) {
					Console.log("Calling BeforeAction: [" + classNameBootAction + "]");
					context.executeAction = Cache.bootAction.beforeAction(context, requestMethod);
				}
			}

			if (context.executeAction) {
				final String eventType = request.getParameter("eventType");
				Console.log("Calling Action: [" + controllerName + ":" + methodName + "]" + (eventType == null ? "" : "[EventType: " + eventType + "]"));

				try {
					databaseConnectionEvent = ActionLoader.connection(context, requestMethod);

					ActionLoader.process(context, requestController, requestMethod);

					if (context.executeAction) {
						if (page != null && requestController instanceof Window) {
							((Window) requestController).init(context);

							if (page.moduleName != null) {
								DOMHandle.execCommand(context.currentWindow, "Greencode.util.loadScript", Core.CONTEXT_PATH + "/jscript/greencode/modules/" + page.moduleName + ".js", false, GreenCodeConfig.Server.View.charset);
								DOMHandle.execCommand(context.currentWindow, "Greencode.modules." + page.moduleName + ".call", context.currentWindow.principalElement(), context.currentWindow.principalElement(), context.getRequest().getViewSession().getId(), context.getRequest().getConversationId());
							}
						} else if (requestController instanceof EventFunction)
							((EventFunction) requestController).init((EventObject) listArgs[0]);
						else if (requestController instanceof SimpleFunction)
							((SimpleFunction) requestController).init(context);
						else
							requestMethod.invoke(requestController, listArgs);
					}
				} catch (ConnectionLost e) {
					Console.warning(e.getMessage());
				}

				if (databaseConnectionEvent != null)
					databaseConnectionEvent.afterRequest(context);

				if (hasBootaction) {
					AfterAction a = requestMethod.getAnnotation(AfterAction.class);

					if (a == null || !a.disable()) {
						Console.log("Calling AfterAction: [" + classNameBootAction + "]");
						Cache.bootAction.afterAction(context, requestMethod);
					}
				}
			} else
				Console.warning(Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1) + " method will not run.");

			if (context.userLocaleChanged) {
				DOMHandle.execCommand(context.currentWindow, "Greencode.util.loadScript", Core.CONTEXT_PATH + "/jscript/greencode/msg_" + context.userLocale.toString() + ".js", false, GreenCodeConfig.Server.View.charset);
			}
			if (databaseConnectionEvent != null)
				databaseConnectionEvent.onSuccess(context);

			if (requestMethod.isAnnotationPresent(Destroy.class)) {
				if (registeredFunctions != null)
					registeredFunctions.remove(hashcodeRequestMethod);
				else if (!methodName.equals(INIT_METHOD_NAME))
					WindowHandle.removeInstance((Class<? extends Window>) requestClass, context.getRequest().getConversation());
			}

		} catch (StopProcess e) {
		} catch (Exception e) {
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

			if (Cache.bootAction != null) {
				Cache.bootAction.onException(context, e);
			}

			if (databaseConnectionEvent != null)
				databaseConnectionEvent.onError(context, e);

			JsonObject json = new JsonObject();
			json.add("error", error);
			ElementsScan.send(context, json);

			System.err.println(Console.msgError);
			e.printStackTrace();
		} finally {
			ElementsScan.sendElements(context);
			context.destroy();
		}

		if (GreenCodeConfig.Server.writeLog) {
			processTime = System.currentTimeMillis() - processTime;
			int ms = (int) ((processTime) % 1000);
			int seconds = (int) ((processTime / 1000) % 60);
			long minutes = ((processTime - seconds) / 1000) / 60;

			StringBuilder pt = new StringBuilder("Processing time: ");
			if (minutes > 0)
				pt.append(minutes + "m ");

			if (seconds > 0)
				pt.append(seconds + "s ");

			if (ms > 0 || minutes == 0 && seconds == 0)
				pt.append(ms + "ms");

			Console.log(pt.toString());
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		GenericReflection.NoThrow.setFinalStaticValue(Core.class, "projectName", new File(fConfig.getServletContext().getRealPath("/")).getName());
		GenericReflection.NoThrow.setFinalStaticValue(Core.class, "defaultLogMsg", "[" + Core.projectName + "] ");
		GenericReflection.NoThrow.setFinalStaticValue(Core.class, "CONTEXT_PATH", fConfig.getServletContext().getContextPath());
		GenericReflection.NoThrow.setFinalStaticValue(Core.class, "SRC_CORE_JS_FOR_SCRIPT_HTML", Core.CONTEXT_PATH + "/jscript/greencode/core.js");

		System.out.println("\nLoading Project: [" + projectName + "]");

		try {
			try {
				GreenCodeConfig.load();
			} catch (IOException e1) {
				throw new IOException("Could not find file: src/greencode.config.xml");
			}

			GreenCodeConfig.Server.Internationalization.Variant variant = GreenCodeConfig.Server.Internationalization.getVariantLogByLocale(Locale.getDefault());

			if (variant == null)
				variant = GreenCodeConfig.Server.Internationalization.getVariantLogByLocale(new Locale("pt", "BR"));

			if (variant != null) {
				LogMessage.instance.load(new InputStreamReader(variant.resource.openStream(), variant.charsetName));

				for (GreenCodeConfig.Server.Internationalization.Variant v : GreenCodeConfig.Server.Internationalization.pagesLocale) {
					try {
						Properties p = new Properties();
						p.load(new InputStreamReader(v.resource.openStream(), v.charsetName));
						Message.properties.put(v.locale.toString(), p);
					} catch (Exception e) {
						throw new IOException(LogMessage.getMessage("green-0002", v.fileName));
					}
				}
			}

			if (GreenCodeConfig.Server.DataBase.defaultConfigFile != null)
				GreenCodeConfig.Server.DataBase.getConfig(GreenCodeConfig.Server.DataBase.defaultConfigFile);

			try {
				Class.forName("com.google.gson.Gson");
			} catch (ClassNotFoundException e) {
				throw new ClassNotFoundException(LogMessage.getMessage("green-0001"));
			}

			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			System.out.print(defaultLogMsg + "Copying JavaScript Tools...");

			File jScriptFolter = FileUtils.getFileInWebContent("jscript/");
			if (!jScriptFolter.exists())
				jScriptFolter.mkdir();

			final File greencodeFolder = new File(jScriptFolter.getPath() + "/greencode/");
			if (!greencodeFolder.exists())
				greencodeFolder.mkdir();

			final String greencodePath = greencodeFolder.getPath();

			final CoreFileJS coreFileJS = new CoreFileJS(greencodePath);

			for (String fileName : JS_CORE_FILES)
				coreFileJS.append(classLoader.getResource("greencode/jscript/files/" + fileName));
			
			{			
				JsonObject json = new JsonObject();
				
				JsonObject className = new JsonObject();
				className.addProperty("greenContext", GreenContext.class.getName());
				className.addProperty("containerElement", ContainerElement.class.getName());
				className.addProperty("containerEventObject", ContainerEventObject.class.getName());
				className.addProperty("element", Element.class.getName());
				
				json.add("className", className);
				
				json.addProperty("CONTEXT_PATH", Core.CONTEXT_PATH);
				json.addProperty("DEBUG_MODE", GreenCodeConfig.Browser.consoleDebug);
				json.addProperty("EVENT_REQUEST_TYPE", GreenCodeConfig.Server.Request.Event.requestType);

				coreFileJS.append("Greencode.jQuery.extend(Greencode,").append(json).append(");");

				coreFileJS.save();
			}

			for (String fileName : JS_SUPPORT_FILES)
				FileUtils.createFile(FileUtils.getContentFile(classLoader.getResource("greencode/jscript/files/" + fileName)), greencodePath + "/" + fileName);

			final Charset charset = Charset.forName(GreenCodeConfig.Server.View.charset);
			final long currentTime = new Date().getTime();
			for (GreenCodeConfig.Server.Internationalization.Variant v : GreenCodeConfig.Server.Internationalization.pagesLocale) {
				FileWeb p = new FileWeb();
				p.lastModified = currentTime;

				p.setContent(FileUtils.getContentFile(v.resource, charset, new FileRead() {
					public String reading(String line) {
						final int indexOf = line.indexOf('=');
						if (indexOf != -1) {
							final String key = line.substring(0, indexOf).trim();
							if (!(key.indexOf('#') != -1 || key.indexOf('!') != -1)) // Comment
																						// Symbol
								return "internationalization_msg['" + key + "'] = '" + line.substring(indexOf + 1, line.length()).trim() + "';";
						}

						return null;
					}

				}));

				FileWeb.files.put("jscript/greencode/msg_" + v.locale.toString() + ".js", p);
			}

			System.out.println(" [done]");

			System.out.print(defaultLogMsg + "Caching Classes...");
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
						throw new IllegalAccessException(LogMessage.getMessage("green-0027", clazz));

					final String name = clazz.getAnnotation(Name.class).value();

					if (Cache.forms.containsKey(name))
						throw new Exception(LogMessage.getMessage("green-0031", name, clazz.getSimpleName()));

					Cache.forms.put(name, (java.lang.Class<? extends Form>) clazz);
				} else if (ClassUtils.isParent(clazz, Window.class) && !Modifier.isAbstract(clazz.getModifiers())) {
					Annotation.processWindowAnnotation((java.lang.Class<? extends Window>) clazz, classLoader, greencodeFolder);
					Cache.registeredWindows.put(clazz.getSimpleName(), (java.lang.Class<? extends Window>) clazz);
				}
			}
			classesTeste.clear();
			System.out.print(" [done]\n");

			if (GreenCodeConfig.Server.View.templatePaths != null) {
				System.out.println(defaultLogMsg + "Caching Template(s)...");

				for (Entry<String, String> entry : GreenCodeConfig.Server.View.templatePaths.entrySet()) {
					File f = FileUtils.getFileInWebContent(entry.getValue());

					if (!f.exists())
						throw new IOException(LogMessage.getMessage("green-0002", entry.getValue()));

					Cache.templates.put(entry.getKey(), f);

					StringBuilder str = new StringBuilder(defaultLogMsg + "Template: " + entry.getValue());
					if (Cache.defaultTemplate == null && entry.getValue().equals(GreenCodeConfig.Server.View.defaultTemplatePath)) {
						GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "defaultTemplate", f);
						str.append(" (Default)");
					}

					System.out.println(str.toString());
				}
			}

			if (classDatabaseConnection != null) {
				System.out.print(defaultLogMsg + "Initializing Database Connection Event ...");
				GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "classDatabaseConnectionEvent", classDatabaseConnection);
				System.out.println(" [done]");
			} else {
				System.out.print(defaultLogMsg + "Testing Database Connection ...");
				DatabaseConnection db = new DatabaseConnection();
				if (db.getConfig() != null) {
					db.start();
					db.close();
				}
				System.out.println(" [done]");
			}

			if (classBootAction != null) {
				System.out.print(defaultLogMsg + "Initializing Boot Action ...");

				GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "bootAction", (BootActionImplementation) classBootAction.newInstance());

				Cache.bootAction.init(greencodePath, classLoader, fConfig.getServletContext(), coreFileJS);
				System.out.println(" [done]");
			}

			if (GreenCodeConfig.Server.Plugins.list != null) {
				System.out.print(defaultLogMsg + "Initializing Plugins ...");

				PluginImplementation[] list = new PluginImplementation[GreenCodeConfig.Server.Plugins.list.length];
				for (int i = -1; ++i < list.length;) {
					PluginImplementation plugin = GreenCodeConfig.Server.Plugins.list[i].newInstance();
					plugin.init(greencodePath, classLoader, fConfig.getServletContext(), coreFileJS);
					list[i] = plugin;
				}

				GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "plugins", list);

				System.out.println(" [done]");
			}

			GenericReflection.NoThrow.setFinalStaticValue(Core.class, "HAS_ERROR", false);
		} catch (Exception e) {
			throw new GreencodeError(e);
		}
	}

	public void destroy() {
		if (Cache.bootAction != null) {
			System.out.print(defaultLogMsg + "Destroying BootAction...");
			Cache.bootAction.destroy();
			System.out.print(" [done]\n");
		}

		if (Cache.plugins != null) {
			System.out.print(defaultLogMsg + "Destroying Plugins...");
			for (PluginImplementation plugin : Cache.plugins)
				plugin.destroy();
			System.out.print(" [done]\n");
		}

		if (!greencode.http.$HttpRequest.getGlobalViewList().isEmpty()) {
			System.out.print(defaultLogMsg + "Destroying " + greencode.http.$HttpRequest.getGlobalViewList().size() + " View(s)...");
			while (!greencode.http.$HttpRequest.getGlobalViewList().isEmpty())
				greencode.http.$HttpRequest.getGlobalViewList().get(0).invalidate();
			System.out.print(" [done]\n");
		}

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				System.out.print(defaultLogMsg + String.format("Deregistering jdbc driver: %s\n", driver));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
