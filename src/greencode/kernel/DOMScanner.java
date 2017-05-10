package greencode.kernel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.RemoteEndpoint.Basic;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import greencode.http.HttpRequest;
import greencode.http.ViewSession;
import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;
import greencode.jscript.JSExecutor;

public class DOMScanner {
	final List<JSExecutor> comm = new ArrayList<JSExecutor>();
	JsonObject sync;
	Integer[] args;

	private DOMScanner() {
	}

	static DOMScanner getElements(ViewSession viewSession) {
		DOMScanner elements = (DOMScanner) viewSession.getAttribute("_ELEMENTS");
		if (elements == null)
			viewSession.setAttribute("_ELEMENTS", elements = new DOMScanner());

		return elements;
	}

	public static void registerExecution(JSExecutor execution) {
		getElements(execution.view).comm.add(execution);
	}

	static void setSync(GreenContext context, int uid, String varName, JSExecutor jsCommand) {
		DOMScanner scan = getElements(context.getRequest().getViewSession());
		
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
		DOMScanner elements = DOMScanner.getElements(context.getRequest().getViewSession());

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
		final boolean isWebSocket = context.request.isWebSocket();
		
		if(isWebSocket && !context.request.getWebSocketSession().isOpen()) {
			return;
		}
		
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

		if (isWebSocket) {
			Basic basicRemote = context.request.getWebSocketSession().getBasicRemote();
			if (greencode.http.$HttpRequest.contentIsHtml(context.request)) {
				json.insert(0, "<json style=\"display: none;\">").append("</json>");
			}
			
			String msgText = getMsgEventId(context.webSocketData) + json.toString();
			// Correção temporaria.
			while(true) {
				try {
					basicRemote.sendText(msgText);
					break;
				} catch (Exception e) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e1) {
					}
				}
			}
		} else {
			context.response.getWriter().write(json.insert(0, "<json style=\"display: none;\">").append("</json>").toString());	
		}
	}
	
	static void synchronize(final String servletPath, final HttpServletRequest httpServletRequest, ServletResponse response, final WebSocketData webSocketData) throws IOException {
		final GreenContext context = new GreenContext(httpServletRequest, response, null, webSocketData);
		
		final HttpRequest __request = context.request;
					
		final ViewSession viewSession = __request.getViewSession();
		final boolean set = Boolean.parseBoolean(__request.getParameter("set"));
		final Map<Integer, DOM> DOMList = greencode.jscript.$DOMHandle.getDOMSync(viewSession);			
		final Map<String, List<Map<String, String>>> list = context.gsonInstance.fromJson(__request.getParameter("list"), new HashMap<String, List<Map<String, String>>>().getClass());			
		
		Map<Integer, Thread> threadList = null;
		Thread th = null;
		Integer accessCode = null; 
		if(!set) {
			threadList = GreenContext.getThreadList(__request.getConversation());
			accessCode = Integer.parseInt(__request.getParameter("accessCode"));
			th = threadList.get(accessCode);
		}
		
		synchronized(set ? Object.class : th) {
			for (Entry<String, List<Map<String, String>>> o : list.entrySet()) {
				final Integer uid = Integer.parseInt(o.getKey());
				
				DOM dom = DOMList.get(uid);

				synchronized (dom) {
					DOMList.remove(uid);

					List<Map<String, String>> attrs = o.getValue();	
					
					StringBuilder strInforme = new StringBuilder("[Synchronized] {uid=" + uid);
					
					if(attrs.size() == 0) {
						strInforme.append(":Not Found}");
					} else {
						strInforme.append(", attrs = [");
						boolean first = true;
						for (Map<String, String> attr : attrs) {
							final String varName = attr.get("name");
							final String value = attr.get("var");
							
							
							if(!first) {
								strInforme.append(", ");
							} else {
								first = false;
							}

							if(set) {
								DOMHandle.setVariableValue(dom, varName, value);	
							} else {
								try {
									Class<?> cast = attr.get("cast") != null ? Class.forName(attr.get("cast")) : null;
									DOMHandle.setVariableValue(dom, varName, greencode.jscript.$DOMHandle.setVariableValue(context, dom, varName, cast, value));
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}								
							}

							strInforme.append("{varName=" + varName + ", value=" + value+"}");
						}
						strInforme.append("]}");
					}
					
					Console.log(strInforme.toString());

					if(set) {
						dom.notify();
					}
				}
			}
			
			if(!set) {
				th.notify();
				threadList.remove(accessCode);
			}
		}

		context.destroy();
	}
}
