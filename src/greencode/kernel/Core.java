package greencode.kernel;

import greencode.database.DatabaseConnection;
import greencode.database.implementation.DatabaseConnectionEvent;
import greencode.exception.ConnectionLost;
import greencode.exception.OperationNotAllowedException;
import greencode.exception.StopProcess;
import greencode.http.HttpAction;
import greencode.http.HttpRequest;
import greencode.http.gzip.GZipServletResponseWrapper;
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
import greencode.kernel.GreenCodeConfig.Browser;
import greencode.kernel.GreenCodeConfig.Internationalization;
import greencode.kernel.GreenCodeConfig.Internationalization.Variant;
import greencode.kernel.implementation.BootActionImplementation;
import greencode.kernel.implementation.PluginImplementation;
import greencode.util.ArrayUtils;
import greencode.util.ClassUtils;
import greencode.util.FileUtils;
import greencode.util.FileUtils.FileRead;
import greencode.util.GenericReflection;
import greencode.util.PackageUtils;

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
import java.util.ArrayList;
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

import com.google.gson.JsonObject;

@WebFilter(displayName = "core", urlPatterns = "/*")
public final class Core implements Filter {
	final static String CONTEXT_PATH = null, projectName = null, defaultLogMsg = null;
	final static String SRC_CORE_JS_FOR_SCRIPT_HTML = null;
	private final static Boolean hasError = true;

	private final static String[] jsFiles = {
		"sizzle.js"
	};

	private final static String[] coreJSFiles = {
		"json3.js",
		"greencode.js",
		"greencodeFunction.js",
		"greencodeEvents.js",
		"greencodeTags.js",
		"greenCodeStyle.js",
		"iframeHttpRequest.js",
		"comet.js",
		"bootstrap.js",
		"init.js"
	};
	

	public void destroy() {
		if(Cache.bootAction != null) {
			System.out.print(defaultLogMsg + "Destroying BootAction...");
			Cache.bootAction.destroy();
			System.out.print(" [done]\n");
		}

		if(Cache.plugins != null) {
			System.out.print(defaultLogMsg + "Destroying Plugins...");
			for(PluginImplementation plugin: Cache.plugins)
				plugin.destroy();
			System.out.print(" [done]\n");
		}

		if(!greencode.http.$HttpRequest.getGlobalViewList().isEmpty()) {
			System.out.print(defaultLogMsg + "Destroying " + greencode.http.$HttpRequest.getGlobalViewList().size() + " View(s)...");
			while(!greencode.http.$HttpRequest.getGlobalViewList().isEmpty())
				greencode.http.$HttpRequest.getGlobalViewList().get(0).invalidate();
			System.out.print(" [done]\n");
		}

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while(drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				System.out.print(defaultLogMsg + String.format("Deregistering jdbc driver: %s\n", driver));
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
		String acceptEncoding = httpRequest.getHeader("Accept-Encoding");
		return acceptEncoding != null && acceptEncoding.indexOf("gzip") != -1;
	}

	private final static Field requestField = GenericReflection.NoThrow.getDeclaredField(RequestFacade.class, "request");

	@Override
	public void doFilter(final ServletRequest request, ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(GreenCodeConfig.View.charset);
		response.setCharacterEncoding(GreenCodeConfig.View.charset);
		response.setContentType("text/html;charset=" + GreenCodeConfig.View.charset);

		if(hasError) {
			response.getWriter().write(LogMessage.getMessage("green-0000"));
			return;
		}

		final String servletPath = ((HttpServletRequest) request).getServletPath().substring(1);

		if(servletPath.equals("$synchronize")) {
			GreenContext context = new GreenContext(new HttpRequest((HttpServletRequest) request), (HttpServletResponse) response, null);

			final Integer uid = Integer.parseInt(request.getParameter("uid"));

			HashMap<Integer, DOM> DOMList = greencode.jscript.$DOMHandle.getDOMSync(context.getRequest().getViewSession());

			DOM dom = DOMList.get(uid);

			synchronized (dom) {
				DOMList.remove(uid);

				final String varName = request.getParameter("varName");
				if(varName != null) {
					Object value = varName.indexOf("$$_file_") == 0 ? context.getRequest().getPart(varName) : request.getParameter("var");

					DOMHandle.setVariableValue(dom, varName, value);

					Console.log("Synchronized:  [uid=" + uid + ", varName=" + varName + ", value=" + value + "]");
				} else
					Console.log("Synchronized:  [uid=" + uid + ":Not Found]");

				dom.notify();
			}

			response.getWriter().close();
			return;
		}

		if(GreenCodeConfig.Response.gzipSupport) {
			if(acceptsGZipEncoding((HttpServletRequest) request)) {
				final HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.addHeader("Content-Encoding", "gzip");
				response = new GZipServletResponseWrapper(httpResponse);
			}
		}

		final boolean hasBootaction = Cache.bootAction != null;

		if(hasBootaction)
			Cache.bootAction.onRequest((HttpServletRequest) request, (HttpServletResponse) response);

		final String controllerName, methodName;
		final Page page;

		Class<? extends HttpAction> requestClass = null;
		HttpAction requestController = null;

		Integer hashcodeRequestMethod = null;

		if(servletPath.indexOf('$') > -1) {
			page = null;
			String[] r = servletPath.split("\\$", 2);
			controllerName = r[0];
			methodName = "init";
			hashcodeRequestMethod = new Integer(r[1]);

			requestClass = Cache.registeredWindows.get(controllerName);
		} else if(servletPath.indexOf('@') > -1) {
			page = null;
			String[] r = servletPath.split("\\@", 2);
			controllerName = r[0];
			methodName = r[1];

			if(methodName.equals("init"))
				throw new OperationNotAllowedException();

			requestClass = Cache.registeredWindows.get(controllerName);
		} else {
			page = Page.pathAnalyze(servletPath, Page.pages.get(servletPath), (HttpServletRequest) request);

			if(page == null || page.window == null) {
				if(page == null)
					chain.doFilter(request, response);
				else
					response.getWriter().write(page.getContent(null));

				if(response instanceof GZipServletResponseWrapper)
					((GZipServletResponseWrapper) response).close();
				return;
			} else {
				requestClass = page.window;
				controllerName = page.window.getSimpleName();
				methodName = "init";
			}
		}

		long processTime = 0;

		DatabaseConnectionEvent databaseConnectionEvent = null;

		// Multipart System
		{
			MultipartConfig multipartConfig = null;
			if(((GreenCodeConfig.Multipart.autodectetion) || ((multipartConfig = requestClass.getAnnotation(MultipartConfig.class)) != null)) && request.getContentType() != null && request.getContentType().indexOf("multipart/form-data") > -1) {

				Request _request = (Request) GenericReflection.NoThrow.getValue(requestField, request);
				_request.getContext().setAllowCasualMultipartParsing(true);
				_request.getConnector().setMaxPostSize((int) (multipartConfig != null ? multipartConfig.maxRequestSize() : GreenCodeConfig.Multipart.maxRequestSize));
			}
		}

		Console.log(":: Request Processing ::");
		GreenContext context = new GreenContext((HttpServletRequest) request, (HttpServletResponse) response, page);
		try {
			if(page != null) {
				Rule.forClass(context, page);

				if(!(page.pageAnnotation.parameters().length == 1 && page.pageAnnotation.parameters()[0].name().isEmpty())) {
					for(PageParameter p: page.pageAnnotation.parameters())
						greencode.http.$HttpRequest.getParameters(context.request).put(p.name(), p.value());
				}

				String content;
				if(context.request.isAjax())
					content = page.pageAnnotation.ajaxSelector().isEmpty() ? page.getContent(context) : page.getAjaxSelectedContent(page.pageAnnotation.ajaxSelector(), context);
				else if(!page.pageAnnotation.selector().isEmpty())
					content = page.getSelectedContent(page.pageAnnotation.selector(), context);
				else
					content = page.getContent(context);

				context.getResponse().getWriter().write(content);
			}

			requestController = WindowHandle.getInstance((Class<Window>) requestClass, context.getRequest().getConversation());
			if(context.currentWindow == null)
				context.currentWindow = (Window) requestController;

			final Map<Integer, Function> registeredFunctions;
			if(hashcodeRequestMethod != null) {
				registeredFunctions = greencode.jscript.$Window.getRegisteredFunctions((Window) requestController);
				requestController = (HttpAction) registeredFunctions.get(hashcodeRequestMethod);
				if(requestController == null) {
					
				}
				
				requestClass = requestController.getClass();
			} else
				registeredFunctions = null;

			Object[] listArgs = null;
			Method requestMethod = null;

			greencode.kernel.Form.processRequestedForm(context);

			try {
				Class<?>[] listArgsClass = null;
				String[] _args = context.request.getParameterValues("_args[]");
				if(_args != null) {
					final int _argsSize = _args.length;

					ElementsScan eArg = ElementsScan.getElements(context.getRequest().getViewSession());

					eArg.args = new Integer[_argsSize];
					listArgs = new Object[_argsSize];
					listArgsClass = new Class<?>[_argsSize];

					int cntSkip = 0;
					for(int i = -1; ++i < _argsSize;) {
						HashMap<String, String> j = context.gsonInstance.fromJson(_args[i], (new HashMap<String, String>()).getClass());

						Class<?> _class = Class.forName(j.get("className"));
						listArgsClass[i] = _class;

						if(_class.equals(GreenContext.class)) {
							++cntSkip;
							listArgs[i] = context;
						} else {
							DOM dom;
							if(_class.equals(ContainerEventObject.class)) {
								dom = new ContainerEventObject(context, Integer.parseInt(j.get("uid")));
								++cntSkip;
							} else if(_class.equals(ContainerElement.class)) {
								dom = greencode.jscript.$Container.getContainers(context.requestedForm).get(Integer.parseInt(j.get("uid")));
								listArgsClass[i] = dom.getClass();
								++cntSkip;
							} else if(_class.equals(Element.class)) {
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

				context.requestedMethod = requestMethod = GenericReflection.getMethod(requestClass, methodName, listArgsClass);

				Rule.forMethod(context, requestMethod);
			} catch(NoSuchMethodException e1) {
				throw new NoSuchMethodException(LogMessage.getMessage("green-0007", methodName, controllerName));
			}

			if(GreenCodeConfig.Console.writeLog)
				processTime = System.currentTimeMillis();

			String classNameBootAction = null;

			ForceSync fs = requestMethod.getAnnotation(ForceSync.class);
			if(fs != null) {
				context.forceSynchronization = true;
				context.listAttrSync = fs.value();
				if(fs.onlyOnce())
					context.listAttrSyncCache = new HashSet<String>();
			}

			if(hasBootaction) {
				classNameBootAction = Cache.bootAction.getClass().getSimpleName();

				BeforeAction a = requestMethod.getAnnotation(BeforeAction.class);
				if(a == null || !a.disable()) {
					Console.log("Calling BeforeAction: [" + classNameBootAction + "]");
					context.executeAction = Cache.bootAction.beforeAction(context, requestMethod);
				}
			}

			if(context.executeAction) {
				final String eventType = request.getParameter("eventType");
				Console.log("Calling Action: [" + controllerName + ":" + methodName + "]"+(eventType == null ? "" : "[EventType: "+eventType+"]"));

				try {
					databaseConnectionEvent = ActionLoader.connection(context, requestMethod);

					ActionLoader.process(context, requestController, requestMethod);

					if(context.executeAction) {
						if(page != null && requestController instanceof Window) {
							((Window) requestController).init();

							if(page.moduleName != null) {
								DOMHandle.execCommand(
									context.currentWindow,
									"Greencode.util.loadScript",
									Core.CONTEXT_PATH + "/jscript/greencode/modules/" + page.moduleName + ".js",
									false,
									GreenCodeConfig.View.charset
								);
								DOMHandle.execCommand(
									context.currentWindow,
									"Greencode.modules." + page.moduleName + ".call",
									context.currentWindow.principalElement(),
									context.currentWindow.principalElement(),
									context.getRequest().getViewSession().getId(),
									context.getRequest().getConversationId()
								);
							}
						} else if(requestController instanceof EventFunction)
							((EventFunction) requestController).init((EventObject) listArgs[0]);
						else if(requestController instanceof SimpleFunction)
							((SimpleFunction) requestController).init();
						else
							requestMethod.invoke(requestController, listArgs);
					}
				} catch(ConnectionLost e) {
					Console.warning(e.getMessage());
				}

				if(databaseConnectionEvent != null)
					databaseConnectionEvent.afterRequest();

				if(hasBootaction) {
					AfterAction a = requestMethod.getAnnotation(AfterAction.class);

					if(a == null || !a.disable()) {
						Console.log("Calling AfterAction: [" + classNameBootAction + "]");
						Cache.bootAction.afterAction(context, requestMethod);
					}
				}
			} else
				Console.warning(Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1) + " method will not run.");

			if(context.userLocaleChanged) {
				DOMHandle.execCommand(
					context.currentWindow,
					"Greencode.util.loadScript",
					Core.CONTEXT_PATH + "/jscript/greencode/msg_" + context.userLocale.toString() + ".js",
					false,
					GreenCodeConfig.View.charset
				);
			}

			ElementsScan.sendElements(context);

			if(databaseConnectionEvent != null)
				databaseConnectionEvent.onSuccess();

			if(requestMethod.isAnnotationPresent(Destroy.class)) {
				if(registeredFunctions != null)
					registeredFunctions.remove(hashcodeRequestMethod);
				else if(!methodName.equals("init"))
					WindowHandle.removeInstance((Class<? extends Window>)requestClass, context.getRequest().getConversation());
			}
				
		} catch(StopProcess e) {
		} catch(Exception e) {
			try {
				Console.error(e.getCause() == null ? e : e.getCause());
			} catch(StopProcess e2) {
			}

			if(Cache.bootAction != null) {
				if(Cache.bootAction.onException(context, e)) {
					JsonObject json = new JsonObject();
					json.add("errors", context.errors);
					ElementsScan.send(context, json);
				}else
					ElementsScan.sendElements(context);
			}

			if(databaseConnectionEvent != null)
				databaseConnectionEvent.onError(e);
		} finally {
			context.destroy();

			if(response instanceof GZipServletResponseWrapper)
				((GZipServletResponseWrapper) response).close();
		}

		if(GreenCodeConfig.Console.writeLog) {
			processTime = System.currentTimeMillis() - processTime;
			int ms = (int) ((processTime) % 1000);
			int seconds = (int) ((processTime / 1000) % 60);
			long minutes = ((processTime - seconds) / 1000) / 60;

			StringBuilder pt = new StringBuilder("Processing time: ");
			if(minutes > 0)
				pt.append(minutes + "m ");

			if(seconds > 0)
				pt.append(seconds + "s ");

			if(ms > 0 || minutes == 0 && seconds == 0)
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
			} catch(IOException e1) {
				throw new IOException("Could not find file: src/greencode.config.xml");
			}

			Variant variant = Internationalization.getVariantLogByLocale(Locale.getDefault());

			if(variant == null)
				variant = Internationalization.getVariantLogByLocale(new Locale("pt", "BR"));

			if(variant != null) {
				LogMessage.instance.load(new InputStreamReader(variant.resource.openStream(), variant.charsetName));

				for(Variant v: Internationalization.pagesLocale) {
					try {
						Properties p = new Properties();
						p.load(new InputStreamReader(v.resource.openStream(), v.charsetName));
						Message.properties.put(v.locale.toString(), p);
					} catch(Exception e) {
						throw new IOException(LogMessage.getMessage("green-0002", v.fileName));
					}
				}
			}

			if(GreenCodeConfig.DataBase.defaultConfigFile != null)
				GreenCodeConfig.DataBase.getConfig(GreenCodeConfig.DataBase.defaultConfigFile);

			try {
				Class.forName("com.google.gson.Gson");
			} catch(ClassNotFoundException e) {
				throw new ClassNotFoundException(LogMessage.getMessage("green-0001"));
			}

			Class<BootActionImplementation> classBootAction = null;
			Class<DatabaseConnectionEvent> classDatabaseConnection = null;

			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			System.out.print(defaultLogMsg + "Copying JavaScript Tools...");

			File jScriptFolter = FileUtils.getFileInWebContent("jscript/");
			if(!jScriptFolter.exists())
				jScriptFolter.mkdir();

			final File greencodeFolder = new File(jScriptFolter.getPath() + "/greencode/");
			if(!greencodeFolder.exists())
				greencodeFolder.mkdir();

			final String greencodePath = greencodeFolder.getPath();

			final CoreFileJS coreFileJS = new CoreFileJS(greencodePath);

			for(String fileName: coreJSFiles)
				coreFileJS.append(classLoader.getResource("greencode/jscript/files/" + fileName));

			coreFileJS.append("Greencode.className = {").append("greenContext: '" + GreenContext.class.getName() + "',").append("containerElement: '" + ContainerElement.class.getName() + "',").append("containerEventObject: '" + ContainerEventObject.class.getName() + "',").append("element: '" + Element.class.getName() + "'").append("};").append("Greencode.CONTEXT_PATH = '" + fConfig.getServletContext().getContextPath() + "';").append("Greencode.DEBUG_MODE = " + Browser.consoleDebug + ";");

			coreFileJS.save();

			for(String fileName: jsFiles)
				FileUtils.createFile(FileUtils.getContentFile(classLoader.getResource("greencode/jscript/files/" + fileName)), greencodePath + "/" + fileName);

			final Charset charset = Charset.forName(GreenCodeConfig.View.charset);
			final long currentTime = new Date().getTime();
			for(Variant v: Internationalization.pagesLocale) {
				Page p = new Page();
				p.lastModified = currentTime;

				p.setContent(FileUtils.getContentFile(v.resource, charset, new FileRead() {
					public String reading(String line) {
						final int indexOf = line.indexOf('=');
						if(indexOf != -1) {
							final String key = line.substring(0, indexOf).trim();
							if(!(key.indexOf('#') != -1 || key.indexOf('!') != -1)) // Comment
																					// Symbol
								return "internationalization_msg['" + key + "'] = '" + line.substring(indexOf + 1, line.length()).trim() + "';";
						}

						return null;
					}

				}));

				Page.pages.put("jscript/greencode/msg_" + v.locale.toString() + ".js", p);
			}

			System.out.println(" [done]");

			System.out.print(defaultLogMsg + "Caching Classes...");
			List<Class<?>> classesTeste = PackageUtils.getClasses("/", true);
			for(Class<?> Class: classesTeste) {
				if(classBootAction == null && ArrayUtils.contains(Class.getInterfaces(), BootActionImplementation.class))
					classBootAction = (java.lang.Class<BootActionImplementation>) Class;
				else if(classDatabaseConnection == null && ArrayUtils.contains(Class.getInterfaces(), DatabaseConnectionEvent.class))
					classDatabaseConnection = (java.lang.Class<DatabaseConnectionEvent>) Class;
				/*
				 * else if(ClassUtils.isParent(Class, Validator.class))
				 * ValidatorFactory.getValidationInstance((java.lang.Class<?
				 * extends Validator>) Class);
				 */
				else if(ClassUtils.isParent(Class, Form.class) && !Modifier.isAbstract(Class.getModifiers())) {
					if(!Class.isAnnotationPresent(Name.class))
						throw new IllegalAccessException(LogMessage.getMessage("green-0027", Class));

					final String name = Class.getAnnotation(Name.class).value();

					if(Cache.forms.containsKey(name))
						throw new Exception(LogMessage.getMessage("green-0031", name, Class.getSimpleName()));

					Cache.forms.put(name, (java.lang.Class<? extends Form>) Class);
				} else if(ClassUtils.isParent(Class, Window.class) && !Modifier.isAbstract(Class.getModifiers())) {
					Annotation.processWindowAnnotation((java.lang.Class<? extends Window>) Class, classLoader, greencodeFolder);
					Cache.registeredWindows.put(Class.getSimpleName(), (java.lang.Class<? extends Window>) Class);
				}
			}
			classesTeste.clear();
			System.out.print(" [done]\n");

			if(GreenCodeConfig.View.templatePaths != null) {
				System.out.println(defaultLogMsg + "Caching Template(s)...");

				if(GreenCodeConfig.View.templatePaths != null) {
					for(Entry<String, String> entry: GreenCodeConfig.View.templatePaths.entrySet()) {
						File f = FileUtils.getFileInWebContent(entry.getValue());
						Cache.templates.put(entry.getKey(), f);

						if(!f.exists())
							throw new IOException(LogMessage.getMessage("green-0002", entry.getValue()));

						String _default = "";
						if(Cache.defaultTemplate == null && entry.getValue().equals(GreenCodeConfig.View.defaultTemplatePath)) {
							GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "defaultTemplate", f);
							_default = "(Default)";
						}
						System.out.println(defaultLogMsg + "Template: " + entry.getValue() + " " + _default);
					}
				}
			}

			if(classDatabaseConnection != null) {
				System.out.print(defaultLogMsg + "Initializing Database Connection Event ...");
				GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "classDatabaseConnectionEvent", classDatabaseConnection);
				System.out.println(" [done]");
			} else {
				System.out.print(defaultLogMsg + "Testing Database Connection ...");
				DatabaseConnection db = new DatabaseConnection();
				db.setConfig(GreenCodeConfig.DataBase.configs.get(GreenCodeConfig.DataBase.defaultConfigFile));

				if(db.getConfig() != null) {
					db.start();
					db.close();
				}
					
				System.out.println(" [done]");
			}

			if(classBootAction != null) {
				System.out.print(defaultLogMsg + "Initializing Boot Action ...");

				GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "bootAction", (BootActionImplementation) classBootAction.newInstance());

				Cache.bootAction.init(greencodePath, classLoader, fConfig.getServletContext(), coreFileJS);
				System.out.println(" [done]");
			}

			if(GreenCodeConfig.Plugins.list != null && GreenCodeConfig.Plugins.list.length > 0) {
				System.out.print(defaultLogMsg + "Initializing Plugins ...");

				ArrayList<PluginImplementation> list = new ArrayList<PluginImplementation>();
				for(Class<PluginImplementation> c: GreenCodeConfig.Plugins.list) {
					PluginImplementation plugin = c.newInstance();
					plugin.init(greencodePath, classLoader, fConfig.getServletContext(), coreFileJS);
					list.add(plugin);
				}

				GenericReflection.NoThrow.setFinalStaticValue(Cache.class, "plugins", list.toArray(new PluginImplementation[list.size()]));

				System.out.println(" [done]");
			}

			GenericReflection.NoThrow.setFinalStaticValue(Core.class, "hasError", false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
