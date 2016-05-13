package greencode.kernel;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.catalina.connector.Request;

import greencode.util.GenericReflection;

public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {

	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		HttpServletRequest _request = (HttpServletRequest)GenericReflection.NoThrow.getValue(Core._HandshakeRequest, request);
		Request _requestFaced = (Request) GenericReflection.NoThrow.getValue(Core.requestField, _request);
						
		config.getUserProperties().put("httpRequest", _request);
		config.getUserProperties().put("httpResponse", _request.getAttribute("httpResponse"));
		config.getUserProperties().put("httpSession", _request.getSession());
		config.getUserProperties().put("context", GenericReflection.NoThrow.getValue(Core.requestContextField, _requestFaced));

		config.getUserProperties().put("remoteHost", _request.getRemoteHost());
		config.getUserProperties().put("localPort", _request.getLocalPort());
	}
}