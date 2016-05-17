package greencode.kernel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.RemoteEndpoint.Basic;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import greencode.http.ViewSession;
import greencode.jscript.DOM;
import greencode.jscript.JSCommand;

public class ElementsScan {
	private final List<JSCommand> comm = new ArrayList<JSCommand>();
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
		registerCommand(e, null, name, args);
	}

	public static void registerCommand(GreenContext context, String name, Object... args) {
		registerCommand(context, null, name, args);
	}

	public static void registerCommand(DOM e, Class<?> cast, String name, Object... args) {
		getElements(greencode.jscript.$DOMHandle.getViewSession(e)).comm.add(new JSCommand(e, cast, name, args));
	}

	public static void registerCommand(GreenContext context, Class<?> cast, String name, Object... args) {
		getElements(context.getRequest().getViewSession()).comm.add(new JSCommand(null, cast, name, args));
	}

	static void setSync(GreenContext context, int uid, String varName, JSCommand jsCommand) {
		ElementsScan scan = getElements(context.getRequest().getViewSession());
		
		JsonArray list;
		if(scan.sync == null) {
			scan.sync = new JsonObject();
			scan.sync.add("list", list = new JsonArray());
			scan.sync.addProperty("set", context.immediateSync);
			scan.sync.addProperty("viewId", context.request.getViewSession().getId());
			scan.sync.addProperty("cid", context.request.getConversationId());			
			if(!context.immediateSync) {
				scan.sync.addProperty("accessCode", Thread.currentThread().hashCode());	
			}
		} else {
			list = (JsonArray) scan.sync.get("list");
		}
		
		JsonObject json = new JsonObject();
		json.addProperty("uid", uid);
		json.addProperty("varName", varName);
		json.add("command", context.gsonInstance.toJsonTree(jsCommand));
		
		list.add(json);
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
		return GreenCodeConfig.Browser.websocketSingleton ? "{-websocket-msg-:" + wsData.eventId + "}" : "";
	}

	static String getCloseEventId(WebSocketData wsData) {
		return GreenCodeConfig.Browser.websocketSingleton ? "{-websocket-close-:" + wsData.eventId + "}" : "";
	}

	private static void send(GreenContext context, Object o, Gson gson) throws IOException {
		final StringBuilder json = new StringBuilder();
		if (o != null) {
			try {
				json.append(context.gsonInstance.toJson(o));
			} catch (Exception e) { // java.util.ConcurrentModificationException
									// || java.util.NoSuchElementException
				send(context, o, context.getGsonInstance());
				return;
			}
		}

		if (context.request.isWebSocket()) {
			try {
				Basic basicRemote = context.request.getWebSocketSession().getBasicRemote();

				String eventId = getMsgEventId(context.webSocketData);

				if (greencode.http.$HttpRequest.contentIsHtml(context.request)) {
					basicRemote.sendText(eventId + json.insert(0, "<json style=\"display: none;\">").append("</json>").toString());
				} else {
					basicRemote.sendText(eventId + json.toString());
				}
			} catch (Exception e) {
				// Ignore Errors
			}
		} else
			context.response.getWriter().write(json.insert(0, "<json style=\"display: none;\">").append("</json>").toString());
	}
}
