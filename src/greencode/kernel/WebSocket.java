package greencode.kernel;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Request;
import org.apache.tomcat.util.http.MimeHeaders;

import com.google.gson.Gson;

import greencode.util.GenericReflection;

@ServerEndpoint(value = "/coreWebSocket", configurator = WebSocketConfigurator.class)
public class WebSocket {
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
			_request.setContext((Context) config.getUserProperties().get("context"));
			
			session.setMaxBinaryMessageBufferSize(GreenCodeConfig.Server.Request.Websocket.maxBinaryMessageSize);
			session.setMaxTextMessageBufferSize(GreenCodeConfig.Server.Request.Websocket.maxTextMessageSize);
			session.setMaxIdleTimeout(GreenCodeConfig.Server.Request.Websocket.maxIdleTimeout);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) throws IOException, ServletException {
		final WebSocketData wsData = new Gson().fromJson(message, WebSocketData.class);
		
		wsData.httpSession = this.session;
		wsData.session = session;
		wsData.headers = (MimeHeaders) session.getUserProperties().get("headers");
		wsData.localPort = (Integer) session.getUserProperties().get("localPort");
		wsData.remoteHost = (String) session.getUserProperties().get("remoteHost");
		wsData.remoteAddr = (String) session.getUserProperties().get("remoteAddr");		
		wsData.requestURI = wsData.url;
		wsData.requestURL = new StringBuffer("http://").append(wsData.remoteHost).append(":").append(wsData.localPort).append("/").append(wsData.url);

		try {
			final String servletPath = wsData.url.indexOf(Core.CONTEXT_PATH) == 0 ? wsData.url.substring(Core.CONTEXT_PATH.length() + 1) : wsData.url;
			if (servletPath.equals("$synchronize")) {
				DOMScanner.synchronize(servletPath, request, response, wsData);
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
}
