package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class MeterElement extends Element {
		
	protected MeterElement(Window window) { super(window, "meter"); }
		
	public void form(String formId) { DOMHandle.setProperty(this, "form", formId); }
	public String form() { return DOMHandle.getVariableValueByProperty(this, "form", String.class, "form"); }
	
	public void high(int high) { DOMHandle.setProperty(this, "high", high); }
	public Integer high() { return DOMHandle.getVariableValueByProperty(this, "high", Integer.class, "high"); }
	
	public void low(int low) { DOMHandle.setProperty(this, "low", low); }
	public Integer low() { return DOMHandle.getVariableValueByProperty(this, "low", Integer.class, "low"); }
	
	public void max(int max) { DOMHandle.setProperty(this, "max", max); }
	public Integer max() { return DOMHandle.getVariableValueByProperty(this, "max", Integer.class, "max"); }
	
	public void min(int min) { DOMHandle.setProperty(this, "min", min); }
	public Integer min() { return DOMHandle.getVariableValueByProperty(this, "min", Integer.class, "min"); }
	
	public void optimum(int optimum) { DOMHandle.setProperty(this, "optimum", optimum); }
	public Integer optimum() { return DOMHandle.getVariableValueByProperty(this, "optimum", Integer.class, "optimum"); }
	
	public void value(int value) { DOMHandle.setProperty(this, "value", value); }
	public Integer value() { return DOMHandle.getVariableValueByProperty(this, "value", Integer.class, "value"); }
	
	public static MeterElement cast(Element e) { return ElementHandle.cast(e, MeterElement.class); }
}
