package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;
import greencode.jscript.dom.elements.attribute.LanguageCode;
import greencode.jscript.dom.elements.attribute.ShapeArea;

public class AreaElement extends Element {
		
	protected AreaElement(Window window) { super(window, "area"); }
	
	public String alt() { return DOMHandle.getVariableValueByProperty(this, "alt", String.class, "alt"); }
	public void alt(String text) { DOMHandle.setProperty(this, "alt", text); }

	public String coords() { return DOMHandle.getVariableValueByProperty(this, "coords", String.class, "coords"); }
	public void coords(String coordinates) { DOMHandle.setProperty(this, "coords", coordinates); }

	/**Only supported in HTML5.*/
	public void download(String download) { DOMHandle.setProperty(this, "download", download); }
	/**Only supported in HTML5.*/
	public String download() { return DOMHandle.getVariableValueByProperty(this, "download", String.class, "download"); }

	public String href() { return DOMHandle.getVariableValueByProperty(this, "href", String.class, "href"); }
	public void href(String href) { DOMHandle.setProperty(this, "href", href); }

	/**Only supported in HTML5.*/
	public LanguageCode hreflang() { return LanguageCode.getByISOCode(DOMHandle.getVariableValueByProperty(this, "hreflang", String.class, "hreflang")); }
	public void hreflang(LanguageCode hreflang) { DOMHandle.setProperty(this, "hreflang", hreflang.isoCode); }
	
	/**Only supported in HTML5.*/
	public void media(String mediaQuery) { DOMHandle.setProperty(this, "media", mediaQuery); }
	/**Only supported in HTML5.*/
	public String media() { return DOMHandle.getVariableValueByProperty(this, "media", String.class, "media"); }	
	
	/**Only supported in HTML5.*/
	public String rel() { return DOMHandle.getVariableValueByProperty(this, "rel", String.class, "rel"); }
	/**Only supported in HTML5.*/
	public void rel(String rel) { DOMHandle.setProperty(this, "rel", rel); }
	
	public ShapeArea shape() { return ShapeArea.valueOf(DOMHandle.getVariableValueByProperty(this, "shape", String.class, "shape").toUpperCase()); }
	public void shape(ShapeArea shape) { DOMHandle.setProperty(this, "shape", shape); }
	
	public String target() { return DOMHandle.getVariableValueByProperty(this, "target", String.class, "target"); }
	public void target(String target) { DOMHandle.setProperty(this, "target", target); }
	
	/**Only supported in HTML5.*/
	public void type(String MIMEType) { DOMHandle.setProperty(this, "type", MIMEType); }
	/**Only supported in HTML5.*/
	public String type() { return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type"); }
	
	public static AreaElement cast(Element e) { return ElementHandle.cast(e, AreaElement.class); }
}
