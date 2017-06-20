package greencode.jscript.dom;

import greencode.jscript.DOMHandle;
import greencode.util.GenericReflection;

public class Node extends NodeCustom {
	
	protected Node(Window window) { super(window); }
	
	public<N extends Node> N appendChild(N node) {
		DOMHandle.execCommand(this, "appendChild", node);
		return node;
	}
	
	public Node cloneNode() { return cloneNode(getClass(), false); }
	
	public Node cloneNode(boolean deep) { return cloneNode(getClass(), deep); }
	
	public Node nextSibling() { return nextSibling(Node.class); }
	
	public Node previousSibling() { return previousSibling(Node.class); }
	
	public Node firstChild() { return firstChild(Node.class); }
	
	public Node lastChild() { return lastChild(Node.class); }
	
	public Node parentNode() { return parentNode(Node.class); }
	
	public<C extends Node> Node nextSibling(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerReturnByProperty(node, this, "nextSibling");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public<C extends Node> Node previousSibling(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerReturnByProperty(node, this, "previousSibling");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public<C extends Node> Node firstChild(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerReturnByProperty(node, this, "firstChild");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public<C extends Node> Node lastChild(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerReturnByProperty(node, this, "lastChild");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public<C extends Node> C parentNode(Class<C> cast) {
		try {
			C node = GenericReflection.getDeclaredConstrutor(cast, Window.class).newInstance(this.window);
			DOMHandle.registerReturnByProperty(node, this, "parentNode");
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private<N extends Node> N cloneNode(Class<N> classNode, boolean deep) {
		try {
			N node = GenericReflection.getDeclaredConstrutor(classNode, Window.class).newInstance(this.window);
			
			if(deep)
				greencode.jscript.$DOMHandle.cloneVariables(this, node);
			
			DOMHandle.registerReturnByCommand(node, this, "cloneNode", deep);
			
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
		
	public <N extends Node> N replaceChild(Node newNode, N oldNode) {
		DOMHandle.execCommand(this, "replaceChild", newNode, oldNode);
		return oldNode;
	}
	
	public void textContent(String text) {
		DOMHandle.execCommand(this, "childTextConent", text);
	}
	
	public String textContent() {
		return DOMHandle.getVariableValueByCommand(this, "textContent", String.class, "childTextConent");
	}	
}
