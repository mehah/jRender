package greencode.jscript;

import java.util.HashMap;

import greencode.http.ViewSession;
import greencode.kernel.GreenContext;

public class $DOMHandle {
	public static HashMap<Integer, DOM> getDOMSync(ViewSession viewSession) {
		return DOMHandle.getDOMSync(viewSession);
	}

	public static ViewSession getViewSession(DOM d) {
		return d.viewSession;
	}

	public static Element getElementInstance(Window window) {
		return new Element(window);
	}

	public static void setUID(DOM dom, int uid) {
		dom.uid = uid;
	}

	public static HashMap<String, Object> getVariables(DOM dom) {
		return dom.variables;
	}

	public static void setVariables(DOM dom, HashMap<String, Object> variables) {
		dom.variables = variables;
	}
	
	public static<C> C setVariableValue(GreenContext context, final DOM owner, final String varName, Class<C> cast, Object v) {
		return DOMHandle.setVariableValue(context, owner, varName, cast, v);
	}
}
