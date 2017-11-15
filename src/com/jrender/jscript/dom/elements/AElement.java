package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.elements.attribute.LanguageCode;
import com.jrender.jscript.dom.elements.attribute.ShapeArea;

public class AElement extends Element {
		
	protected AElement(Window window) { super(window, "a"); }
	
	/**Not supported in HTML5.*/
	@Deprecated
	public String charset() { return DOMHandle.getVariableValueByProperty(this, "charset", String.class, "charset"); }
	/**Not supported in HTML5.*/
	@Deprecated
	public void charset(String charEncoding) { DOMHandle.setProperty(this, "charset", charEncoding); }
	
	/**Not supported in HTML5.*/
	@Deprecated
	public String coords() { return DOMHandle.getVariableValueByProperty(this, "coords", String.class, "coords"); }
	/**Not supported in HTML5.*/
	@Deprecated
	public void coords(String coordinates) { DOMHandle.setProperty(this, "coords", coordinates); }
	
	/**Only supported in HTML5.*/
	public void download(String download) { DOMHandle.setProperty(this, "download", download); }
	/**Only supported in HTML5.*/
	public String download() { return DOMHandle.getVariableValueByProperty(this, "download", String.class, "download"); }
	
	public String href() { return DOMHandle.getVariableValueByProperty(this, "href", String.class, "href"); }
	public void href(String href) { DOMHandle.setProperty(this, "href", href); }
	
	public LanguageCode hreflang() { return LanguageCode.getByISOCode(DOMHandle.getVariableValueByProperty(this, "hreflang", String.class, "hreflang")); }
	public void hreflang(LanguageCode hreflang) { DOMHandle.setProperty(this, "hreflang", hreflang.isoCode); }
	
	/**Only supported in HTML5.*/
	public void media(String mediaQuery) { DOMHandle.setProperty(this, "media", mediaQuery); }
	/**Only supported in HTML5.*/
	public String media() { return DOMHandle.getVariableValueByProperty(this, "media", String.class, "media"); }
	
	/**Not supported in HTML5.*/
	@Deprecated
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	/**Not supported in HTML5.*/
	@Deprecated
	public void name(String sectionName) { DOMHandle.setProperty(this, "name", sectionName); }
	
	public String rel() { return DOMHandle.getVariableValueByProperty(this, "rel", String.class, "rel"); }
	public void rel(String rel) { DOMHandle.setProperty(this, "rel", rel); }

	/**Not supported in HTML5.*/
	@Deprecated
	public String rev() { return DOMHandle.getVariableValueByProperty(this, "rev", String.class, "rev"); }
	/**Not supported in HTML5.*/
	@Deprecated
	public void rev(String rev) { DOMHandle.setProperty(this, "rev", rev); }
	
	/**Not supported in HTML5.*/
	@Deprecated
	public ShapeArea shape() { return ShapeArea.valueOf(DOMHandle.getVariableValueByProperty(this, "shape", String.class, "shape").toUpperCase()); }
	/**Not supported in HTML5.*/
	@Deprecated
	public void shape(ShapeArea shape) { DOMHandle.setProperty(this, "shape", shape); }
	
	public String target() { return DOMHandle.getVariableValueByProperty(this, "target", String.class, "target"); }
	public void target(String target) { DOMHandle.setProperty(this, "target", target); }
	
	/**Only supported in HTML5.*/
	public void type(String MIMEType) { DOMHandle.setProperty(this, "type", MIMEType); }
	/**Only supported in HTML5.*/
	public String type() { return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type"); }
	
	public static AElement cast(Element e) { return ElementHandle.cast(e, AElement.class); }
}
