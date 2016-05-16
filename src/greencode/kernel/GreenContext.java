package greencode.kernel;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import greencode.database.DatabaseConnection;
import greencode.exception.OperationNotAllowedException;
import greencode.http.HttpRequest;
import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;
import greencode.jscript.Form;
import greencode.jscript.Window;
import greencode.kernel.implementation.BootActionImplementation;
import greencode.kernel.serialization.DOMDeserializer;
import greencode.kernel.serialization.DOMSerializer;

public final class GreenContext {
	private final static ThreadLocal<WeakReference<GreenContext>> greenContext = new ThreadLocal<WeakReference<GreenContext>>();
		
	public static BootActionImplementation getBootAction() { return Cache.bootAction; }
	public static GreenContext getInstance() { return greenContext.get().get(); }
	
	private boolean destroyed;
	
	final HttpRequest request;
	final HttpServletResponse response;
	final WebSocketData webSocketData;
	final public Gson gsonInstance = getGsonInstance();
	
	boolean userLocaleChanged = false;	
	Locale userLocale;
	Properties currentMessagePropertie;

	Window currentWindow;
	greencode.jscript.window.annotation.Page currentPageAnnotation;
	Form requestedForm;
	Method requestedMethod;
	
	private DatabaseConnection databaseConnection;
	boolean executeAction = true;
	
	boolean flushed = false;
	
	boolean forceSynchronization = false;
	String[] listAttrSync;
	HashMap<Integer, HashSet<String>> listAttrSyncCache;
	
	GreenContext(HttpServletRequest request, ServletResponse response, FileWeb currentPage, WebSocketData wsData) {
		GreenContext.greenContext.set(new WeakReference<GreenContext>(this)); 

		boolean sessionInitialized = wsData != null || request.getSession(false) != null;
				
		this.webSocketData = wsData;
		this.response = (HttpServletResponse) response;
		this.request = new HttpRequest(request, response, wsData);
		this.currentPageAnnotation = currentPage == null ? null : currentPage.pageAnnotation;
		
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
	
	public Method getRequestedMethod() {
		exceptionCheck();
		return this.requestedMethod;
	}
	
	public greencode.jscript.window.annotation.Page currentPageAnnotation() {
		exceptionCheck();
		
		return this.currentPageAnnotation == null ? greencode.jscript.$Window.getCurrentPageAnnotation(currentWindow()) : this.currentPageAnnotation;
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
			throw new OperationNotAllowedException(LogMessage.getMessage("green-0034"));
	}
	
	boolean isForcingSynchronization(final DOM dom, final String property) {
		boolean sync;
		if(sync = this.forceSynchronization) {			
			if(this.listAttrSync != null) {
				HashSet<String> props = this.listAttrSyncCache.get(DOMHandle.getUID(dom));
				if(props == null) {
					this.listAttrSyncCache.put(DOMHandle.getUID(dom), props = new HashSet<String>());
				}
				
				final boolean hasListAttrSyncCache = props != null;
				if(this.listAttrSync.length > 0) {
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
		}
		
		return sync;
	}
	
	void destroy() {
		if(this.webSocketData != null) {
			try {
				if(GreenCodeConfig.Browser.websocketSingleton) {
					this.webSocketData.session.getBasicRemote().sendText(ElementsScan.getCloseEventId(webSocketData));
				}else
					this.webSocketData.getSession().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(this.databaseConnection != null) {
			try {
				this.databaseConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
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
		
		greenContext.remove();
	}
}