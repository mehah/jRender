package greencode.kernel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import greencode.http.ViewSession;
import greencode.jscript.DOM;
import greencode.jscript.JSCommand;

public class ElementsScan {
	private final List<JSCommand> comm = new ArrayList<JSCommand>(); /* commands */
	JsonObject sync;
	Integer[] args;
	
	private ElementsScan() {}
	
	static ElementsScan getElements(ViewSession viewSession) {
		ElementsScan elements = (ElementsScan) viewSession.getAttribute("_ELEMENTS"); 
		if(elements == null)
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
	
	private static void send(GreenContext context, Object o, Gson gson) throws IOException {
		final StringBuilder json = new StringBuilder();
		if(o != null) {
			try {
				json.append(context.gsonInstance.toJson(o));
			}catch(Exception e) { // java.util.ConcurrentModificationException || java.util.NoSuchElementException
				send(context, o, context.getGsonInstance());
				return;
			}		
		}
		
		context.getResponse().getWriter().write(json.insert(0, "<json style=\"display: none;\">").append("</json>").toString());
	}
}
