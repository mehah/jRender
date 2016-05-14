package greencode.kernel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.RemoteEndpoint.Basic;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import greencode.http.ViewSession;
import greencode.jscript.DOM;
import greencode.jscript.JSCommand;

public class ElementsScan {
	private final List<JSCommand> comm = new ArrayList<JSCommand>(); /* commands */
	JsonObject sync;
	Integer[] args;

	private ElementsScan() {
	}

	static ElementsScan getElements(ViewSession viewSession) {
		ElementsScan elements = (ElementsScan) viewSession.getAttribute("_ELEMENTS");
		if (elements == null)
			viewSession.setAttribute("_ELEMENTS", elements = new ElementsScan());

		return elements;
	}

	public static void registerCommand(DOM e, String name, Object... args) {
		getElements(greencode.jscript.$DOMHandle.getViewSession(e)).comm.add(new JSCommand(e, name, args));
	}

	static void setSync(GreenContext context, int uid, String varName, JSCommand jsCommand) {
		JsonObject json = new JsonObject();
		json.addProperty("uid", uid);
		json.addProperty("varName", varName);
		json.add("command", context.gsonInstance.toJsonTree(jsCommand));
		getElements(context.getRequest().getViewSession()).sync = json;
	}

	public static void sendElements(GreenContext context) throws IOException {
		ElementsScan elements = ElementsScan.getElements(context.getRequest().getViewSession());

		send(context, elements);
		elements.comm.clear();
		elements.args = null;
		elements.sync = null;
	}

	static void send(GreenContext context, Object o) throws IOException {
		send(context, o, context.gsonInstance);
	}
	
	static String getMsgEventId(WebSocketData wsData) {
		return GreenCodeConfig.Browser.websocketSingleton ? "{-websocket-msg-:"+wsData.eventId+"}" : "";
	}
	
	static String getCloseEventId(WebSocketData wsData) {
		return GreenCodeConfig.Browser.websocketSingleton ? "{-websocket-close-:"+wsData.eventId+"}" : "";
	}

	private static void send(GreenContext context, Object o, Gson gson) throws IOException {
		final StringBuilder json = new StringBuilder();
		if (o != null) {
			try {
				json.append(context.gsonInstance.toJson(o));
			} catch (Exception e) { // java.util.ConcurrentModificationException || java.util.NoSuchElementException
				send(context, o, context.getGsonInstance());
				return;
			}
		}

		if (context.getRequest().isWebSocket()) {
			Basic basicRemote = context.getRequest().getWebSocketSession().getBasicRemote();
			
			String eventId = getMsgEventId(context.webSocketData);
			
			if(greencode.http.$HttpRequest.contentIsHtml(context.getRequest())) {
				basicRemote.sendText(eventId+json.insert(0, "<json style=\"display: none;\">").append("</json>").toString());
			} else {
				basicRemote.sendText(eventId+json.toString());
			}			
		} else
			context.getResponse().getWriter().write(json.insert(0, "<json style=\"display: none;\">").append("</json>").toString());
	}
}
