package com.jrender.kernel;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jrender.database.DatabaseConnection;
import com.jrender.exception.ConnectionLost;
import com.jrender.exception.OperationNotAllowedException;
import com.jrender.http.Conversation;
import com.jrender.http.HttpRequest;
import com.jrender.jscript.DOM;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Form;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.function.implementation.SimpleFunction;
import com.jrender.jscript.dom.window.annotation.PageParameter;
import com.jrender.kernel.implementation.BootActionImplementation;
import com.jrender.kernel.serialization.DOMDeserializer;
import com.jrender.kernel.serialization.DOMSerializer;
import com.jrender.util.LogMessage;

public final class JRenderContext {
	private final static ThreadLocal<WeakReference<JRenderContext>> context = new ThreadLocal<WeakReference<JRenderContext>>();
		
	public static BootActionImplementation getBootAction() { return Cache.bootAction; }
	public static JRenderContext getInstance() { return context.get().get(); }
	
	private boolean destroyed;
	
	final HttpRequest request;
	final HttpServletResponse response;
	final WebSocketData webSocketData;
	final com.jrender.jscript.dom.window.annotation.Page currentPageAnnotation;
	final public Gson gsonInstance = getGsonInstance();
	
	boolean userLocaleChanged = false;	
	Locale userLocale;
	Properties currentMessagePropertie;

	Window currentWindow;
	Form requestedForm;
	Method requestedMethod;
	
	private DatabaseConnection databaseConnection;
	boolean executeAction = true, flushed, forceSynchronization, immediateSync = true;
	String[] listAttrSync;
	Map<Integer, HashSet<String>> listAttrSyncCache;
	
	JRenderContext(HttpServletRequest request, ServletResponse response, FileWeb currentPage, WebSocketData wsData) {
		JRenderContext.context.set(new WeakReference<JRenderContext>(this)); 

		boolean sessionInitialized = wsData != null || request.getSession(false) != null;
				
		this.webSocketData = wsData;
		this.response = (HttpServletResponse) response;
		this.request = new HttpRequest(request, response, wsData);
		
		if(currentPage == null) {
			String servletPath = this.request.getParameter("servletPath");
			if(servletPath != null) {
				this.currentPageAnnotation = FileWeb.files.get(servletPath).pageAnnotation;
			} else {
				this.currentPageAnnotation = null;
			}
		} else
			this.currentPageAnnotation = currentPage.pageAnnotation;
		
		if(this.currentPageAnnotation != null) {
			if (!(this.currentPageAnnotation.parameters().length == 1 && this.currentPageAnnotation.parameters()[0].name().isEmpty())) {
				for (PageParameter p : this.currentPageAnnotation.parameters())
					com.jrender.http.$HttpRequest.getParameters(this.request).put(p.name(), p.value());
			}
		}
		
		if(!sessionInitialized && getBootAction() != null)
			getBootAction().initUserContext(this);
		
		Locale locale = (Locale) this.request.getSession().getAttribute("USER_LOCALE");
		
		if(locale != null) {
			this.userLocale = locale;
			this.currentMessagePropertie = (Properties) this.request.getSession().getAttribute("CURRENT_MESSAGE_PROPERTIE");
			if(this.request.isFirst())
				userLocaleChanged = true;	
		} else {
			locale = Locale.getDefault();
			if(Message.properties.containsKey(locale.toString()))
				setUserLocale(locale);
		}
	}
	
	public HttpServletResponse getResponse() { exceptionCheck(); return response; }
	public HttpRequest getRequest() { exceptionCheck(); return request; }
	
	public Window currentWindow() {
		exceptionCheck();
		
		return this.currentWindow;
	}
	
	static Map<Integer, Thread> getThreadList(Conversation conversation) {
		Map<Integer, Thread> list = (Map<Integer, Thread>) conversation.getAttribute("LIST_THREADS");
		if(list == null)
			conversation.setAttribute("LIST_THREADS", list = new ConcurrentHashMap<Integer, Thread>());
		
		return list;
	}
	
	public void synchronizeDOMGroup(SimpleFunction func) {
		exceptionCheck();
		
		this.immediateSync = false;
		try {
			Thread th = Thread.currentThread();
			getThreadList(request.getConversation()).put(th.hashCode(), th);
			synchronized(th) {
				func.init(this);
				this.currentWindow.flush();
				th.wait(120000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConnectionLost(LogMessage.getMessage("0011"));
		}
		this.immediateSync = true;
	}
	
	public Method getRequestedMethod() {
		exceptionCheck();
		return this.requestedMethod;
	}
	
	public com.jrender.jscript.dom.window.annotation.Page currentPageAnnotation() {
		exceptionCheck();
		
		return this.currentPageAnnotation == null ? com.jrender.jscript.dom.$Window.getCurrentPageAnnotation(currentWindow()) : this.currentPageAnnotation;
	}
	
	Gson getGsonInstance() {
		GsonBuilder instance = new GsonBuilder()
			.serializeNulls()
			.registerTypeHierarchyAdapter(DOM.class, new DOMSerializer())
			.registerTypeHierarchyAdapter(DOM.class, new DOMDeserializer(this));

		return instance.create();
	}
	
	public DatabaseConnection getDatabaseConnection() {
		exceptionCheck();		
		return this.databaseConnection == null ? this.databaseConnection = new DatabaseConnection() : this.databaseConnection;
	}
	
	public void forceSync(boolean force) {
		exceptionCheck();
		this.forceSynchronization = force;
	}
	
	public Locale getUserLocale() {
		exceptionCheck();
		return this.userLocale;
	}
	
	public void setUserLocale(Locale locale) { setUserLocale(locale, false); }
	
	public void setUserLocale(Locale locale, boolean ifEmpty) {
		exceptionCheck();
		
		final HttpSession session = request.getSession();
		
		if(ifEmpty && session.getAttribute("CURRENT_MESSAGE_PROPERTIE") != null)
			return;
		
		this.userLocaleChanged = true;
		this.userLocale = locale;
		this.currentMessagePropertie = Message.properties.get(locale.toString());
		
		session.setAttribute("USER_LOCALE", this.userLocale);
		session.setAttribute("CURRENT_MESSAGE_PROPERTIE", this.currentMessagePropertie);
	}
	
	private void exceptionCheck() {
		if(destroyed)
			throw new OperationNotAllowedException(LogMessage.getMessage("0034"));
	}
	
	boolean isForcingSynchronization(final DOM dom, final String property) {
		boolean sync;
		if(sync = this.forceSynchronization && this.listAttrSyncCache != null) {
			HashSet<String> props = this.listAttrSyncCache.get(DOMHandle.getUID(dom));
			if(props == null) {
				this.listAttrSyncCache.put(DOMHandle.getUID(dom), props = new HashSet<String>());
			}
			
			final boolean hasListAttrSyncCache = props != null;
			if(this.listAttrSync != null && this.listAttrSync.length > 0) {
				sync = false;
				
				if(!hasListAttrSyncCache || !props.contains(property)) {
					for (String attr : this.listAttrSync) {
						if(attr.equals(property)) {
							sync = true;
							if(hasListAttrSyncCache)
								props.add(property);
							break;
						}
					}
				}
			}else if(hasListAttrSyncCache) {
				if(sync = !props.contains(property))
					props.add(property);
			}
		}
		
		return sync;
	}
	
	void destroy() {
		try {
			if(this.webSocketData != null) {
				if(!JRenderConfig.Client.websocketSingleton) {
					this.webSocketData.session.close();					
				} else if(this.webSocketData.session.isOpen()) {
					this.webSocketData.session.getBasicRemote().sendText(DOMScanner.getCloseEventId(webSocketData));
				}
			}
		} catch (Exception e) { e.printStackTrace(); }
		
		if(this.databaseConnection != null) {
			try {
				this.databaseConnection.close();
			} catch (SQLException e) { e.printStackTrace(); }
			finally {
				this.databaseConnection = null;
			}
		}
		
		this.currentMessagePropertie = null;
		this.currentWindow = null;
		this.requestedForm = null;
		this.requestedMethod = null;
		this.userLocale = null;
		this.listAttrSync = null;
		this.listAttrSyncCache = null;
		this.destroyed = true;
		
		context.get().clear();
		context.remove();
	}
}