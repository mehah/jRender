package greencode.jscript;

import java.util.HashMap;

import greencode.util.GenericReflection;

public class Node extends EventTarget {
	
	protected Node(Window window) { super(window); }
	
	public Node appendChild(Node node) {
		DOMHandle.execCommand(this, "appendChild", node);
		return node;
	}
	
	public Node cloneNode() { return cloneNode(getClass()); }
	
	public Node nextSibling() { return nextSibling(Node.class); }
	
	public Node previousSibling() { return previousSibling(Node.class); }
	
	public Node firstChild() { return firstChild(Node.class); }
	
	public Node lastChild() { return lastChild(Node.class); }
	
	public Node parentNode() { return parentNode(Node.class); }
	
	public<C extends Node> Node nextSibling(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerElementByProperty(this, node, "nextSibling");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public<C extends Node> Node previousSibling(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerElementByProperty(this, node, "previousSibling");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public<C extends Node> Node firstChild(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerElementByProperty(this, node, "firstChild");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public<C extends Node> Node lastChild(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerElementByProperty(this, node, "lastChild");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public<C extends Node> C parentNode(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerElementByProperty(this, node, "parentNode");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private<N extends Node> N cloneNode(Class<N> classNode) {
		try {
			N node = GenericReflection.getDeclaredConstrutor(classNode, Window.class).newInstance(this.window);
			node.variables = (HashMap<String, Object>) this.variables.clone();
			
			DOMHandle.registerElementByCommand(this, node, "cloneNode");
			return node;
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}

		
	}
	
	public int compareDocumentPosition(Element node) {
		return DOMHandle.getVariableValueByCommandNoCache(this, "compareDocumentPosition", Integer.class, "compareDocumentPosition", node);		
	}
	
	public Boolean hasAttributes() {
		return DOMHandle.getVariableValueByCommand(this, "hasAttributes", Boolean.class, "hasAttributes");
	}
	
	public Boolean hasChildNodes() {
		return DOMHandle.getVariableValueByCommand(this, "hasChildNodes", Boolean.class, "hasChildNodes");
	}
	
	public Node insertBefore(Node newNode, Node existingNode) {
		DOMHandle.execCommand(this, "insertBefore", newNode, existingNode);
		return newNode;
	}
	
	public Boolean isDefaultNamespace(String namespaceURI) {
		return DOMHandle.getVariableValueByCommand(this, "isDefaultNamespace."+namespaceURI, Boolean.class, "isDefaultNamespace", namespaceURI);
	}
	
	public Boolean isEqualNode(Element node) {
		return DOMHandle.getVariableValueByCommand(this, "isEqualNode", Boolean.class, "isEqualNode", node);
	}
	
	public Boolean isSameNode(Element node) {
		return DOMHandle.getVariableValueByCommand(this, "isSameNode", Boolean.class, "isEqualNode", node);
	}
	
	public Boolean isSupported(String feature, String version) {
		return DOMHandle.getVariableValueByCommand(this, "isSupported."+feature+"."+version, Boolean.class, "isSupported", feature, version);
	}
	
	public String lookupNamespaceURI(String prefix) {
		return DOMHandle.getVariableValueByCommand(this, "lookupNamespaceURI."+prefix, String.class, "lookupNamespaceURI", prefix);
	}
	
	public String lookupPrefix(String URI) {
		return DOMHandle.getVariableValueByCommand(this, "lookupPrefix."+URI, String.class, "lookupPrefix", URI);
	}
	
	public void normalize() { DOMHandle.execCommand(this, "normalize"); }
	
	public Node removeChild(Node node) {
		DOMHandle.execCommand(this, "removeChild", node);
		return node;
	}
		
	public Node replaceChild(Node newNode, Node oldNode) {
		DOMHandle.execCommand(this, "replaceChild", newNode, oldNode);
		return oldNode;
	}
	
	public void textContent(String text) {
		DOMHandle.execCommand(this, "@crossbrowser.text", text);
	}
	
	public String textContent() {
		return DOMHandle.getVariableValueByCommand(this, "textContent", String.class, "@crossbrowser.text");
	}	
}
