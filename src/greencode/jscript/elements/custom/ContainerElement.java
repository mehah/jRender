package greencode.jscript.elements.custom;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;
import greencode.jscript.elements.custom.implementation.ContainerElementImplementation;

import java.lang.reflect.Field;

public abstract class ContainerElement extends Element implements ContainerElementImplementation {
	final Field[] elementFields = greencode.jscript.$Container.processFields(getClass());
	
	public ContainerElement(Window window) { super(window, "container"); }

	public void name(String name) { DOMHandle.setProperty(this, "name", name); }
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
		
	public static ContainerElement cast(Element e) { return ElementHandle.cast(e, ContainerElement.class); }
}
