package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.Form;
import greencode.jscript.WindowHandle;

public abstract class ContainerElement extends Element {
	
	private final Form form;
	
	public ContainerElement() {
		super(null, "container");
		form = null;
	}
	
	public ContainerElement(Form form, String name) {
		super(WindowHandle.getInstance(form), "container");
		this.form = form;
		DOMHandle.setVariableValue(this, "name", name);
	}
		
	public String name() { return DOMHandle.getVariableValue(this, "name", String.class); }
	
	public Form getForm() { return this.form; }
}
