package com.jrender.kernel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.websocket.RemoteEndpoint.Basic;

import org.apache.catalina.connector.Request;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jrender.http.HttpRequest;
import com.jrender.http.ViewSession;
import com.jrender.jscript.DOM;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.JSExecutor;
import com.jrender.util.GenericReflection;

public class DOMScanner {
	final List<JSExecutor> comm = new ArrayList<JSExecutor>();
	JsonObject sync;
	Integer[] args;

	DOMScanner() {
	}
	
	private boolean isEmpty() {
		return comm.isEmpty() && sync == null && args == null;
	}

	static DOMScanner getElements(ViewSession viewSession) {
		return com.jrender.http.$ViewSession.getDOMScanner(viewSession);
	}

	public static void registerExecution(JSExecutor execution) {
		getElements(execution.view).comm.add(execution);
	}

	static void setSync(JRenderContext context, int uid, String varName, JSExecutor jsCommand) {
		DOMScanner scan = getElements(context.getRequest().getViewSession());

		JsonArray list;
		if (scan.sync == null) {
			scan.sync = new JsonObject();
			scan.sync.add("list", list = new JsonArray());
			scan.sync.addProperty("set", context.immediateSync);
			scan.sync.addProperty("viewId", context.request.getViewSession().getId());
			scan.sync.addProperty("cid", context.request.getConversationId());
			if (!context.immediateSync) {
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

	public static void sendElements(JRenderContext context) throws IOException {
		DOMScanner elements = DOMScanner.getElements(context.getRequest().getViewSession());

		if(!elements.isEmpty()) {
			send(context, elements);
			elements.comm.clear();
			elements.args = null;
			elements.sync = null;
		}
	}

	static void send(JRenderContext context, Object o) throws IOException {
		send(context, o, context.gsonInstance);
	}

	static String getMsgEventId(WebSocketData wsData) {
		return JRenderConfig.Client.websocketSingleton ? "{-websocket-msg-:" + wsData.eventId + "}" : "";
	}

	static String getCloseEventId(WebSocketData wsData) {
		return JRenderConfig.Client.websocketSingleton ? "{-websocket-close-:" + wsData.eventId + "}" : "";
	}

	private static void send(JRenderContext context, Object o, Gson gson) throws IOException {
		final boolean isWebSocket = context.request.isWebSocket();

		if (isWebSocket && !context.request.getWebSocketSession().isOpen()) {
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
			if (com.jrender.http.$HttpRequest.contentIsHtml(context.request)) {
				json.insert(0, "<json style=\"display: none;\">").append("</json>");
			}

			String msgText = getMsgEventId(context.webSocketData) + json.toString();
			// Correção temporaria.
			while (true) {
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

	static void synchronize(final String servletPath, final HttpServletRequest httpServletRequest, ServletResponse response, final WebSocketData webSocketData) throws IOException, ServletException {
		final JRenderContext context = new JRenderContext(httpServletRequest, response, null, webSocketData);

		final HttpRequest __request = context.request;

		if (httpServletRequest.getContentType() != null && httpServletRequest.getContentType().indexOf("multipart/form-data") > -1) {
			Request _request = (Request) GenericReflection.NoThrow.getValue(Core.requestField, httpServletRequest);
			_request.getContext().setAllowCasualMultipartParsing(true);
			_request.getConnector().setMaxPostSize(JRenderConfig.Server.Request.Multipart.maxRequestSize);
		}

		final ViewSession viewSession = __request.getViewSession();
		final boolean set = Boolean.parseBoolean(__request.getParameter("set"));
		final Map<Integer, DOM> DOMList = com.jrender.jscript.$DOMHandle.getDOMSync(viewSession);

		final Integer fileUID = __request.getParameter("fileUID") == null ? null : Integer.parseInt(__request.getParameter("fileUID"));
		final Map<String, List<Map<String, String>>> list = fileUID != null ? null : context.gsonInstance.fromJson(__request.getParameter("list"), new HashMap<String, List<Map<String, String>>>().getClass());

		Map<Integer, Thread> threadList = null;
		Thread th = null;
		Integer accessCode = null;
		if (!set) {
			threadList = JRenderContext.getThreadList(__request.getConversation());
			accessCode = Integer.parseInt(__request.getParameter("accessCode"));
			th = threadList.get(accessCode);
		}

		synchronized (set ? Object.class : th) {
			if (list == null) {
				DOM dom = DOMList.get(fileUID);

				synchronized (dom) {
					DOMList.remove(fileUID);
					String varName = __request.getParameter("varName");
					Part value = __request.getPart(varName);
					DOMHandle.setVariableValue(dom, varName, value);

					Console.log("[Synchronized] {uid=" + fileUID + ", varName=" + varName + ", value=" + value + "}");
					dom.notify();
				}
			} else {
				for (Entry<String, List<Map<String, String>>> o : list.entrySet()) {
					final Integer uid = Integer.parseInt(o.getKey());

					DOM dom = DOMList.get(uid);

					synchronized (dom) {
						DOMList.remove(uid);

						List<Map<String, String>> attrs = o.getValue();

						StringBuilder strInforme = new StringBuilder("[Synchronized] {uid=" + uid);

						if (attrs.size() == 0) {
							strInforme.append(":Not Found}");
						} else {
							strInforme.append(", attrs = [");
							boolean first = true;
							for (Map<String, String> attr : attrs) {
								final String varName = attr.get("name");
								final String value = attr.get("var");

								if (!first) {
									strInforme.append(", ");
								} else {
									first = false;
								}

								if (set) {
									DOMHandle.setVariableValue(dom, varName, value);
								} else {
									try {
										Class<?> cast = attr.get("cast") != null ? Class.forName(attr.get("cast")) : null;
										DOMHandle.setVariableValue(dom, varName, com.jrender.jscript.$DOMHandle.setVariableValue(context, dom, varName, cast, value));
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
								}

								strInforme.append("{varName=" + varName + ", value=" + value + "}");
							}
							strInforme.append("]}");
						}

						Console.log(strInforme.toString());

						if (set) {
							dom.notify();
						}
					}
				}
			}

			if (!set) {
				th.notify();
				threadList.remove(accessCode);
			}
		}

		context.destroy();
	}
}
