package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;
import greencode.jscript.elements.attribute.HttpEquivAttr;
import greencode.jscript.elements.attribute.MetaNameAttr;

public class MetaElement extends Element {
		
	protected MetaElement(Window window) { super(window, "meta"); }
	
	/**Only supported in HTML5.*/
	public String charset() { return DOMHandle.getVariableValueByProperty(this, "charset", String.class, "charset"); }
	/**Only supported in HTML5.*/
	public void charset(String charEncoding) { DOMHandle.setProperty(this, "charset", charEncoding); }
	
	public String content() { return DOMHandle.getVariableValueByProperty(this, "content", String.class, "content"); }
	public void content(String content) { DOMHandle.setProperty(this, "content", content); }
	
	public HttpEquivAttr httpEquiv() { return HttpEquivAttr.getByValue(DOMHandle.getVariableValueByProperty(this, "http-equiv", String.class, "http-equiv")); }
	public void httpEquiv(HttpEquivAttr httpEquiv) { DOMHandle.setProperty(this, "http-equiv", httpEquiv.value); }
	
	public MetaNameAttr name() { return MetaNameAttr.getByValue(DOMHandle.getVariableValueByProperty(this, "name", String.class, "name")); }
	public void name(MetaNameAttr name) { DOMHandle.setProperty(this, "name", name.value); }
	
	/**Not supported in HTML5.*/
	public void scheme(String scheme) { DOMHandle.setProperty(this, "scheme", scheme); }
	/**Not supported in HTML5.*/
	public String scheme() { return DOMHandle.getVariableValueByProperty(this, "scheme", String.class, "scheme"); }
	
	public static MetaElement cast(Element e) { return ElementHandle.cast(e, MetaElement.class); }
}
