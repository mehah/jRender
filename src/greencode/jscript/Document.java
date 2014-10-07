package greencode.jscript;

import java.util.HashMap;

import greencode.jscript.elements.BodyElement;
import greencode.jscript.elements.HeadElement;
import greencode.util.GenericReflection;

public class Document extends Node {
	final HashMap<Class<? extends Form>, Form> forms = new HashMap<Class<? extends Form>, Form>();
	public final BodyElement body;
	public final HeadElement head;
	
	Document(Window window) {
		super(window);
		
		this.uid = 3; // Document UID
		
		body = greencode.jscript.elements.$Element.getBodyInstance(window);
		head = greencode.jscript.elements.$Element.getHeadInstance(window);
		
		greencode.jscript.$DOMHandle.setUID(head, 4);
		greencode.jscript.$DOMHandle.setUID(body, 5);
	}
	
	@SuppressWarnings("unchecked")
	public<F extends Form> F forms(Class<F> formClass) {
		F form = (F) forms.get(formClass);
		if(form == null)
		{
			try {
				form = formClass.newInstance();
				form.processAnnotation();
				
				forms.put((Class<? extends Form>) formClass, form);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return form;
	}
	
	public Element createElement(String tagName) {
		Element e = new Element(this.window);
		
		DOMHandle.registerElementByCommand(this, e, "createElement", tagName);
		DOMHandle.setVariableValue(e, "tagName", tagName);
		
		return e;
	}
	
	public<E extends Element> E createElement(Class<E> element) {
		try {
			E e = GenericReflection.NoThrow.getDeclaredConstrutor(element, Window.class).newInstance(this.window);
			DOMHandle.registerElementByCommand(this, e, "createElement", DOMHandle.getVariableValue(e, "tagName", String.class));
			
			if(DOMHandle.containVariableKey(e, "type"))
				DOMHandle.setProperty(e, "type", DOMHandle.getVariableValue(e, "type", String.class));
			
			return e;
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}		
	}
	
	public Node createTextNode(String text) {
		Node e = new Node(this.window);		
		DOMHandle.registerElementByCommand(this, e, "createTextNode", text);
		
		return e;
	}
	
	public Node createComment(String text) {
		Node e = new Node(this.window);		
		DOMHandle.registerElementByCommand(this, e, "createComment", text);
		
		return e;
	}
	
	public Element getElementById(String id) {
		Element e = new Element(this.window);		
		DOMHandle.registerElementByCommand(this, e, "getElementById", id);
		
		return e;
	}
	
	public<E extends Element> E getElementById(String id, Class<E> cast) {		
		return ElementHandle.cast(getElementById(id), cast);
	}
	
	public Element[] getElementsByName(String tagName)
	{ return getElementsBy("getElementsByName.length", "getElementsByName", tagName); }
	
	public Element[] getElementsByTagName(String tagName)
	{ return getElementsBy("getElementsByTagName.length", "getElementsByTagName", tagName); }
	
	public Element[] getElementsByClassName(String tagName)
	{ return getElementsBy("getElementsByClassName.length", "crossbrowser.getElementsByClassName", tagName); }
	
	public Element querySelector(String selector) {
		Element e = new Element(this.window);		
		DOMHandle.registerElementByCommand(this, e, "crossbrowser.querySelector", selector);
		
		return e;
	}
	
	public Element[] querySelectorAll(String selector) { return getElementsBy("querySelectorAll.length", "crossbrowser.querySelectorAll", selector); }
	
	private Element[] getElementsBy(String varName, String command, String tagName) {
		final int qnt = DOMHandle.getVariableValueByPropertyNoCache(this, varName, Integer.class, command+"('"+tagName+"').length");
		
		Element[] elements = new Element[qnt];
		int[] uids = new int[qnt];
		for (int i = -1; ++i < qnt;)
			uids[i] = DOMHandle.getUID(elements[i] = new Element(this.window));
		
		DOMHandle.registerReturnByCommand(this, uids, command, tagName);
		
		return elements;
	}
	
	public void open() { DOMHandle.execCommand(this, "open"); }
	
	public void open(String MIMEtype, String replace) { DOMHandle.execCommand(this, "open", MIMEtype, replace); }
	
	public void close() { DOMHandle.execCommand(this, "close"); }
	
	public void normalizeDocument() { DOMHandle.execCommand(this, "normalizeDocument"); }
	
	public String readyState() { return "interactive"; }
	
	public String referrer() { return DOMHandle.getVariableValueByProperty(this, "referrer", String.class, "referrer"); }
	
	public Node renameNode(Node node, String namespaceURI, String nodename) {
		DOMHandle.execCommand(this, "renameNode", node, namespaceURI, nodename);		
		return node;
	}
	
	public Boolean strictErrorChecking() { return DOMHandle.getVariableValueByProperty(this, "strictErrorChecking", Boolean.class, "strictErrorChecking"); }
	
	public void strictErrorChecking(boolean arg0) { DOMHandle.setProperty(this, "strictErrorChecking", arg0); }
	
	public String title() { return DOMHandle.getVariableValueByProperty(this, "title", String.class, "title"); }
	
	public void title(String title) { DOMHandle.setProperty(this, "title", title); }
	
	public String URL() { return DOMHandle.getVariableValueByProperty(this, "title", String.class, "title"); }
	
	public void write(String txt) { DOMHandle.execCommand(this, "write", txt); }
	
	public void writeln(String txt) { DOMHandle.execCommand(this, "writeln", txt); }
}
