package greencode.jscript.elements.custom;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;
import greencode.jscript.elements.custom.implementation.ContainerElementImplementation;
import greencode.util.GenericReflection;

import java.lang.reflect.Field;

public abstract class ContainerElement<E extends ContainerElement<E>> extends Element implements ContainerElementImplementation {
	final Field[] elementFields = greencode.jscript.$Container.processFields(getClass());
	private E original;
	
	public ContainerElement(Window window) { super(window, "container"); }

	public void name(String name) { DOMHandle.setProperty(this, "name", name); }
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }

	public E getOriginal() {
		if(this.original == null)
			DOMHandle.registerElementByProperty(this, this.original = (E) GenericReflection.NoThrow.newInstance(getClass(), new Class<?>[]{Window.class}, this.window), "original");

		return this.original;
	}

	public E repeat() {
		return repeat(true);
	}
	
	public E repeat(boolean useOriginal) {
		Element e = GenericReflection.NoThrow.newInstance(getClass(), new Class[]{Window.class}, window);		
		DOMHandle.registerElementByCommand(this, e, "repeat", useOriginal);
		
		return (E) e;
	}

	public static ContainerElement<?> cast(Element e) {
		return ElementHandle.cast(e, ContainerElement.class);
	}
	
	public void fill() {
		greencode.jscript.$Container.fill(this, elementFields);
	}
}
