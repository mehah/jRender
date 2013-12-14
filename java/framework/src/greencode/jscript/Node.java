package greencode.jscript;

import java.util.HashMap;

import greencode.http.ViewSession;
import greencode.kernel.GreenContext;
import greencode.util.GenericReflection;

public class Node extends DOM {
	public Node() {
		super(GreenContext.getInstance().getRequest().getViewSession());
	}
	
	protected Node(ViewSession viewSession) {
		super(viewSession);
	}
	
	public Node cloneNode() {
		return cloneNode(getClass());
	}
	
	@SuppressWarnings("unchecked")
	private<N extends Node> N cloneNode(Class<N> classNode) {
		N node = null;
		try {
			node = GenericReflection.getDeclaredConstrutor(classNode, ViewSession.class).newInstance(this.viewSession);			
			node.variables = (HashMap<String, Object>) this.variables.clone();
			
			DOMHandle.registerElementByCommand(this, node, "cloneNode");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return node;
	}
}
