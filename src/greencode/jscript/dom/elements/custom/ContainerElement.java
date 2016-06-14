package greencode.jscript.dom.elements.custom;

import java.lang.reflect.Field;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;
import greencode.jscript.dom.elements.custom.implementation.ContainerElementImplementation;
import greencode.util.GenericReflection;

public abstract class ContainerElement<E extends ContainerElement<E>> extends Element implements ContainerElementImplementation {
	final Field[] elementFields = greencode.jscript.dom.$Container.processFields(getClass());
	private E original;
	
	public ContainerElement(Window window) { super(window, "container"); }

	public void name(String name) { DOMHandle.setProperty(this, "name", name); }
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }

	public E getOriginal() {
		if(this.original == null)
			DOMHandle.registerReturnByProperty(this.original = (E) GenericReflection.NoThrow.newInstance(getClass(), new Class<?>[]{Window.class}, this.window), this, "original");

		return this.original;
	}

	public E repeat() {
		return repeat(true);
	}
	
	public E repeat(boolean useOriginal) {
		Element e = GenericReflection.NoThrow.newInstance(getClass(), new Class[]{Window.class}, window);		
		DOMHandle.registerReturnByCommand(e, this, "repeat", useOriginal);
		
		return (E) e;
	}

	public static ContainerElement<?> cast(Element e) {
		return ElementHandle.cast(e, ContainerElement.class);
	}
	
	public void fill() {
		greencode.jscript.dom.$Container.fill(this, elementFields);
	}
}
