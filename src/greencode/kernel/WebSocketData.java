package greencode.kernel;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.apache.tomcat.util.http.MimeHeaders;

public class WebSocketData {
	private Map<String, String[]> params;
	
	int eventId;
	
	String url;
	HttpSession httpSession;
	Session session;
	
	String remoteHost;
	String remoteAddr;
	StringBuffer requestURL;
	String requestURI;
	int localPort;
	MimeHeaders headers;
	
	public MimeHeaders getHeaders() {
		return headers;
	}
	
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

	public Map<String, String[]> getParameters() {
		return params;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public Session getSession() {
		return session;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}
}