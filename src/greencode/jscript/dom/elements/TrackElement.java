package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;
import greencode.jscript.dom.elements.attribute.KindAttr;

/**Only supported in HTML5.*/
public class TrackElement extends Element {
		
	protected TrackElement(Window window) { super(window, "track"); }

	public void defaulT(Boolean defaulT) { DOMHandle.setProperty(this, "default", defaulT); }	
	public Boolean defaulT() { return DOMHandle.getVariableValueByProperty(this, "default", Boolean.class, "default"); }
	
	public KindAttr kind() { return KindAttr.valueOf(DOMHandle.getVariableValueByProperty(this, "kind", String.class, "kind").toUpperCase()); }
	public void kind(KindAttr kind) { DOMHandle.setProperty(this, "kind", kind); }	
	
	public void label(String label) { DOMHandle.setProperty(this, "label", label); }	
	public String label() { return DOMHandle.getVariableValueByProperty(this, "label", String.class, "label"); }

	public void src(String src) { DOMHandle.setProperty(this, "src", src); }	
	public String src() { return DOMHandle.getVariableValueByProperty(this, "src", String.class, "src"); }
	
	public void srclang(String srclang) { DOMHandle.setProperty(this, "srclang", srclang); }	
	public String srclang() { return DOMHandle.getVariableValueByProperty(this, "srclang", String.class, "srclang"); }
	
	public static TrackElement cast(Element e) { return ElementHandle.cast(e, TrackElement.class); }
}
