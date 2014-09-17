package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;
import greencode.jscript.elements.attribute.LanguageCode;

public class LinkElement extends Element {
		
	protected LinkElement(Window window) { super(window, "a"); }
	
	/**Not supported in HTML5.*/
	@Deprecated
	public String charset() { return DOMHandle.getVariableValueByProperty(this, "charset", String.class, "charset"); }
	/**Not supported in HTML5.*/
	@Deprecated
	public void charset(String charEncoding) { DOMHandle.setProperty(this, "charset", charEncoding); }
	
	public String href() { return DOMHandle.getVariableValueByProperty(this, "href", String.class, "href"); }
	public void href(String href) { DOMHandle.setProperty(this, "href", href); }
	
	public LanguageCode hreflang() { return LanguageCode.getByISOCode(DOMHandle.getVariableValueByProperty(this, "hreflang", String.class, "hreflang")); }
	public void hreflang(LanguageCode hreflang) { DOMHandle.setProperty(this, "hreflang", hreflang.isoCode); }

	public void media(String mediaQuery) { DOMHandle.setProperty(this, "media", mediaQuery); }
	public String media() { return DOMHandle.getVariableValueByProperty(this, "media", String.class, "media"); }
	
	public String rel() { return DOMHandle.getVariableValueByProperty(this, "rel", String.class, "rel"); }
	public void rel(String rel) { DOMHandle.setProperty(this, "rel", rel); }
	
	/**Not supported in HTML5.*/
	@Deprecated
	public String rev() { return DOMHandle.getVariableValueByProperty(this, "rev", String.class, "rev"); }
	/**Not supported in HTML5.*/
	@Deprecated
	public void rev(String rev) { DOMHandle.setProperty(this, "rev", rev); }
	
	/**Only supported in HTML5.*/
	public String sizes() { return DOMHandle.getVariableValueByProperty(this, "sizes", String.class, "sizes"); }
	/**Only supported in HTML5.*/
	public void sizes(String sizes) { DOMHandle.setProperty(this, "sizes", sizes); }
		
	/**Not supported in HTML5.*/
	@Deprecated
	public String target() { return DOMHandle.getVariableValueByProperty(this, "target", String.class, "target"); }
	/**Not supported in HTML5.*/
	@Deprecated
	public void target(String target) { DOMHandle.setProperty(this, "target", target); }

	public void type(String MIMEType) { DOMHandle.setProperty(this, "type", MIMEType); }
	public String type() { return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type"); }
	
	public static LinkElement cast(Element e) { return ElementHandle.cast(e, LinkElement.class); }
}
