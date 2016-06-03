package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;
import greencode.jscript.dom.elements.attribute.CrossoriginAttr;

/**Only supported in HTML5.*/
public class ImgElement extends Element {
		
	protected ImgElement(Window window) { super(window, "img"); }

	public void alt(String text) { DOMHandle.setProperty(this, "alt", text); }	
	public String alt() { return DOMHandle.getVariableValueByProperty(this, "alt", String.class, "alt"); }
	
	/**Only supported in HTML5.*/
	public void crossorigin(CrossoriginAttr crossorigin) { DOMHandle.setProperty(this, "crossorigin", crossorigin.value); }
	/**Only supported in HTML5.*/
	public CrossoriginAttr crossorigin() { return CrossoriginAttr.getByValue(DOMHandle.getVariableValueByProperty(this, "crossorigin", String.class, "crossorigin")); }
	
	public void height(Integer pixels) { DOMHandle.setProperty(this, "height", pixels); }	
	public Integer height() { return DOMHandle.getVariableValueByProperty(this, "height", Integer.class, "height"); }
	
	public void isMap(boolean ismap) { DOMHandle.setProperty(this, "ismap", ismap); }
	public Boolean isMap() { return DOMHandle.getVariableValueByProperty(this, "ismap", Boolean.class, "ismap"); }
	
	public void src(String URL) { DOMHandle.setProperty(this, "src", URL); }	
	public String src() { return DOMHandle.getVariableValueByProperty(this, "src", String.class, "src"); }
	
	public void useMap(String map) { DOMHandle.setProperty(this, "usemap", map); }	
	public String useMap() { return DOMHandle.getVariableValueByProperty(this, "usemap", String.class, "usemap"); }
	
	public void width(int width) { DOMHandle.setProperty(this, "width", width); }	
	public Integer width() { return DOMHandle.getVariableValueByProperty(this, "width", Integer.class, "width"); }
	
	public static ImgElement cast(Element e) { return ElementHandle.cast(e, ImgElement.class); }
}
