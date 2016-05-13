package greencode.kernel;

import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

public class WebSocketData {
	private HashMap<String, String[]> params;
	
	String url;
	HttpSession httpSession;
	Session session;
	
	String remoteHost;
	StringBuffer requestURL;
	String requestURI;
	int localPort;
	
	public String getRemoteHost() {
		return remoteHost;
	}
	
	public StringBuffer getRequestURL() {
		return requestURL;
	}
	
	public String getRequestURI() {
		return requestURI;
	}
	
	public int getLocalPort() {
		return localPort;
	}
	
	public String getUrl() {
		return url;
	}

	public HashMap<String, String[]> getParameters() {
		return params;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public Session getSession() {
		return session;
	}
}