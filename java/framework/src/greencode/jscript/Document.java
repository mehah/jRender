package greencode.jscript;

import greencode.http.ViewSession;

public class Document extends DOM {
	public final Element body;
	public final Element head;
	
	Document(ViewSession viewSession) {
		super(viewSession);
		
		this.uid = 3;
		
		body = new Element(viewSession);
		head = new Element(viewSession);
		head.uid = 4;
		body.uid = 5;
	}
	
	public Element createElement(String tagName)
	{
		Element e = new Element(this.viewSession);
		
		DOMHandle.registerElementByCommand(this, e, "createElement", tagName);
		
		return e;
	}
	
	public Node createTextNode(String text)
	{
		Node e = new Node(this.viewSession);
		
		DOMHandle.registerElementByCommand(this, e, "createTextNode", text);
		
		return e;
	}
	
	public Node createComment(String text)
	{
		Node e = new Node(this.viewSession);
		
		DOMHandle.registerElementByCommand(this, e, "createComment", text);
		
		return e;
	}
	
	public Element getElementById(String id)
	{
		Element e = new Element(this.viewSession);
		
		DOMHandle.registerElementByCommand(this, e, "getElementById", id);
		
		return e;
	}
	
	public void write(String txt)
	{
		DOMHandle.execCommand(this, "write", txt);
	}
	
	public void writeln(String txt)
	{
		DOMHandle.execCommand(this, "writeln", txt);
	}
}
