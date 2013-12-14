package greencode.jscript;

import greencode.http.ViewSession;

public class Element extends Node {
	protected Element() {
		super();
	}
	
	protected Element(ViewSession viewSession) {
		super(viewSession);
	}
	
	public Node appendChild(Node node) {
		DOMHandle.execCommand(this, "appendChild", node);
		return node;
	}	
	
	public Node removeChild(Node node) {
		DOMHandle.execCommand(this, "removeChild", node);
		return node;
	}
		
	public Node replaceChild(Node newNode, Node oldNode) {
		DOMHandle.execCommand(this, "replaceChild", newNode, oldNode);
		return oldNode;
	}
	
	public Element cloneNode() {		
		return (Element) super.cloneNode();
	}
	
	public Boolean hasAttribute(String name) {
		return DOMHandle.getVariableValueByCommand(this, "hasAttribute", Boolean.class, "hasAttribute", name);
	}
	
	public Boolean hasAttributes() {
		return DOMHandle.getVariableValueByCommand(this, "hasAttributes", Boolean.class, "hasAttributes");
	}
	
	public Boolean hasChildNodes() {
		return DOMHandle.getVariableValueByCommand(this, "hasChildNodes", Boolean.class, "hasChildNodes");
	}
	
	public int compareDocumentPosition(Element node) {
		int value = DOMHandle.getVariableValueByCommand(this, "compareDocumentPosition", Integer.class, "compareDocumentPosition", node);		
		DOMHandle.removeVariable(this, "compareDocumentPosition");
		
		return value;
	}
	
	public Element insertBefore(Element newNode, Element existingNode) {
		DOMHandle.execCommand(this, "insertBefore", newNode, existingNode);
		return newNode;
	}
	
	public String getAttribute(String name) {
		return DOMHandle.getVariableValueByCommand(this, "attr."+name, String.class, "getAttribute", name);
	}
	
	public void setAttribute(String name, String value)
	{
		DOMHandle.setVariableValue(this, "attr."+name, value);
		DOMHandle.execCommand(this, "setAttribute", name, value);
	}
	
	public void removeAttribute(String name) {
		DOMHandle.removeVariable(this, "attr."+name);
		DOMHandle.execCommand(this, "removeAttribute", name);
	}
	
	public void normalize() {
		DOMHandle.execCommand(this, "normalize");
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
	
	public Element item(int index)
	{
		Element e = new Element(this.viewSession);
		
		DOMHandle.registerElementByCommand(this, e, "item", index);
		
		return e;
	}
	
	/**
	 * No internet explorer irá usar attachEvent
	 */
	public void addEventListener(String eventName, FunctionHandle handle)
	{
		DOMHandle.execCommand(this, "crossbrowser.registerEvent", eventName, handle);
	}
	
	/**
	 * No internet explorer irá usar fireEvent
	 */
	public void dispatchEvent(String eventName)
	{
		DOMHandle.execCommand(this, "crossbrowser.shootEvent", eventName);
	}
	
	/**
	 * No internet explorer irá usar detachEvent
	 */
	public void removeEventListener(String eventName, FunctionHandle handle)
	{
		DOMHandle.execCommand(this, "crossbrowser.removeEvent", eventName, handle);
	}
}
