package greencode.kernel;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.catalina.connector.Request;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.websocket.server.WsHandshakeRequest;

import greencode.util.GenericReflection;

public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {
	private final static Field
		_HandshakeRequest = GenericReflection.NoThrow.getDeclaredField(WsHandshakeRequest.class, "request");

	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		HttpServletRequest _request = (HttpServletRequest) GenericReflection.NoThrow.getValue(_HandshakeRequest, request);
		Request _requestFaced = (Request) GenericReflection.NoThrow.getValue(Core.requestField, _request);
		
		MimeHeaders mime = new MimeHeaders();
		
		Enumeration<String> enuns = _requestFaced.getHeaderNames();
		while(enuns.hasMoreElements()){
			String param = (String) enuns.nextElement();		
			mime.addValue(param).setString(_requestFaced.getHeader(param));
		}
		
		Map<String, Object> properties = config.getUserProperties();
		
		properties.put("httpRequest", _request);
		properties.put("httpResponse", _request.getAttribute("httpResponse"));
		properties.put("httpSession", _request.getSession());
		properties.put("context", _requestFaced.getContext());
		properties.put("headers", mime);
		properties.put("remoteHost", _request.getRemoteHost());
		properties.put("localPort", _request.getLocalPort());		
		properties.put("remoteAddr", _request.getRemoteAddr());
	}
}