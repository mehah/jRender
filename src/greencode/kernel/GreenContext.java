package greencode.kernel;

import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import greencode.database.DatabaseConnection;
import greencode.exception.OperationNotAllowedException;
import greencode.http.HttpRequest;
import greencode.jscript.DOM;
import greencode.jscript.Form;
import greencode.jscript.Window;
import greencode.kernel.implementation.BootActionImplementation;
import greencode.kernel.serialization.DOMDeserializer;
import greencode.kernel.serialization.DOMSerializer;

public final class GreenContext {
	final static ThreadLocal<GreenContext> greenContext = new ThreadLocal<GreenContext>();
		
	public static BootActionImplementation getBootAction() { return Cache.bootAction; }
	public static GreenContext getInstance() { return greenContext.get(); }
	
	private boolean destroyed;
	
	final JsonArray errors = new JsonArray();
	final HttpRequest request;
	final HttpServletResponse response;
	final public Gson gsonInstance;
	final greencode.jscript.window.annotation.Page currentPageAnnotation;
	
	boolean forceSynchronization = false;
	
	boolean userLocaleChanged = false;	
	Locale userLocale;
	Properties currentMessagePropertie;

	Window currentWindow;	
	Form requestedForm;
	
	private DatabaseConnection databaseConnection;
	boolean executeAction = true;
	
	boolean flushed = false;
	
	String[] listAttrSync;
	HashSet<String> listAttrSyncCache;
	
	GreenContext(HttpServletRequest request, HttpServletResponse response, Page currentPage) {
		GreenContext.greenContext.set(this);
		
		boolean sessionInitialized = request.getSession(false) != null;
				
		this.response = response;
		this.request = new HttpRequest(request);
		this.currentPageAnnotation = currentPage == null ? null : currentPage.pageAnnotation;
		
		if(!sessionInitialized && getBootAction() != null)
			getBootAction().initUserContext(this);
		
		Locale locale = (Locale) request.getSession().getAttribute("USER_LOCALE");
		
		if(locale != null)
		{
			this.userLocale = locale;
			this.currentMessagePropertie = (Properties) request.getSession().getAttribute("CURRENT_MESSAGE_PROPERTIE");
			if(!getRequest().isAjax())
				userLocaleChanged = true;	
		}else
		{
			locale = Locale.getDefault();
			if(Message.properties.containsKey(locale.toString()))
				setUserLocale(locale);
		}
		
		this.gsonInstance = getGsonInstance();
	}
	
	public HttpServletResponse getResponse() { exceptionCheck(); return response; }
	public HttpRequest getRequest() { exceptionCheck(); return request; }
	
	public Window currentWindow() {
		exceptionCheck();
		
		return this.currentWindow;
	}
	
	public greencode.jscript.window.annotation.Page currentPageAnnotation() {
		exceptionCheck();
		
		return this.currentPageAnnotation;
	}
	
	private Gson getGsonInstance() {
		GsonBuilder instance = new GsonBuilder();
		instance.serializeNulls();
		instance.registerTypeHierarchyAdapter(DOM.class, new DOMSerializer());
		instance.registerTypeHierarchyAdapter(DOM.class, new DOMDeserializer(this));		
		return instance.create();
	}
	
	public DatabaseConnection getDatabaseConnection() {
		exceptionCheck();	
		
		if(this.databaseConnection == null)
			this.databaseConnection = new DatabaseConnection();
		
		return this.databaseConnection;
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
	
	void destroy() {
		if(this.databaseConnection != null)
			this.databaseConnection.close();
		
		this.currentMessagePropertie = null;
		this.currentWindow = null;
		this.databaseConnection = null;
		this.requestedForm = null;
		this.userLocale = null;
		this.listAttrSync = null;
		this.listAttrSyncCache = null;
		
		greenContext.remove();
		
		destroyed = true;
	}
}
