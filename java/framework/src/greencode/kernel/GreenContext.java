package greencode.kernel;

import java.io.File;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import greencode.database.DatabaseConnection;
import greencode.database.implementation.DatabaseConnectionEvent;
import greencode.http.HttpRequest;
import greencode.jscript.DOM;
import greencode.jscript.Window;
import greencode.kernel.implementation.BootActionImplementation;
import greencode.kernel.serialization.DOMSerializer;

public final class GreenContext {
	static ThreadLocal<GreenContext> greenContext = new ThreadLocal<GreenContext>();
	
	static String DEFAULT_CHARSET = "UTF-8";
	static File defaultTemplate;	
	static BootActionImplementation bootAction;
	static Class<? super DatabaseConnectionEvent> classDatabaseConnectionEvent;
	
	public static BootActionImplementation getBootAction() { return bootAction; }
	public static GreenContext getInstance() { return greenContext.get(); }
	
	boolean forceSynchronization = false;
	
	Window currentWindow;
	
	final HttpRequest request;
	final HttpServletResponse response;
	
	boolean userLocaleChanged = false;	
	Locale userLocale;
	Properties currentMessagePropertie;
	
	private DatabaseConnection databaseConnection;
	private Gson gson;
	boolean executeAction = true;
	
	boolean flushed = false;
	
	GreenContext(HttpServletRequest request, HttpServletResponse response) {
		GreenContext.greenContext.set(this);
		
		boolean sessionInitialized = request.getSession(false) != null;
		
		request.getSession(true);
		
		this.request = new HttpRequest(request);
		this.response = response;
		
		if(!sessionInitialized && bootAction != null)
			bootAction.initUserContext(this);
	}
	
	public HttpServletResponse getResponse() { return response; }
	public HttpRequest getRequest() { return request; }
	
	public Window getCurrentWindow()
	{
		return this.currentWindow;
	}
	
	public Gson getGsonInstance()
	{
		if(this.gson == null)
		{
			GsonBuilder instance = new GsonBuilder();
			instance.serializeNulls();
			instance.registerTypeHierarchyAdapter(DOM.class, new DOMSerializer());
			this.gson = instance.create();
		}
		
		return this.gson;
	}
	
	public DatabaseConnection getDatabaseConnection() {
		DatabaseConnection connection = databaseConnection;		
		
		if(connection == null)
		{
			connection = new DatabaseConnection();
			databaseConnection = connection;
		}
		
		return connection;
	}
	
	public void forceSynchronization(boolean force)
	{
		this.forceSynchronization = true;
	}
}
