package greencode.kernel;

import greencode.http.ViewSession;
import greencode.jscript.DOM;
import greencode.jscript.JSCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

public class ElementsScan {
	private final List<JSCommand> comm = new ArrayList<JSCommand>(); /* commands */
	private JsonObject sync;
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
		
		if(elements.comm.size() > 0 || elements.args != null || elements.sync != null) {
			send(context, elements);		
			elements.comm.clear();
			elements.args = null;
			elements.sync = null;
		}
	}
	
	static void send(GreenContext context, Object o) throws IOException {
		StringBuilder str;
		if(!context.getRequest().isIFrameHttpRequest() && !greencode.http.$HttpRequest.contentIsHtml(context.getRequest())) {
			str = new StringBuilder(",");
			if(o != null) str.append(context.gsonInstance.toJson(o));
		} else {
			str = new StringBuilder("<div class=\"JSON_CONTENT\" style=\"display: none;\">");
			if(o != null) str.append(context.gsonInstance.toJson(o));
			str.append("</div j>");
		}
		
		context.getResponse().getWriter().write(str.toString());
	}
}
