package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class EmbedElement extends Element {
		
	protected EmbedElement(Window window) { super(window, "embed"); }
	
	public void height(Integer pixels) { DOMHandle.setProperty(this, "height", pixels); }	
	public Integer height() { return DOMHandle.getVariableValueByProperty(this, "height", Integer.class, "height"); }
	
	public void width(Integer width) { DOMHandle.setProperty(this, "width", width); }	
	public Integer width() { return DOMHandle.getVariableValueByProperty(this, "width", Integer.class, "width"); }
	
	public void type(String MIMEType) { DOMHandle.setProperty(this, "type", MIMEType); }
	public String type() { return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type"); }
	
	public void src(String url) { DOMHandle.setProperty(this, "src", url); }	
	public String src() { return DOMHandle.getVariableValueByProperty(this, "src", String.class, "src"); }
	
	public static EmbedElement cast(Element e) { return ElementHandle.cast(e, EmbedElement.class); }
}
