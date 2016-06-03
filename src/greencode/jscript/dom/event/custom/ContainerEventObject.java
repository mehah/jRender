package greencode.jscript.dom.event.custom;

import greencode.jscript.DOM;
import greencode.jscript.dom.Form;
import greencode.jscript.dom.elements.custom.ContainerElement;
import greencode.kernel.GreenContext;

public final class ContainerEventObject<F extends Form, C extends ContainerElement<C>> extends DOM {
	public final F form;
	public final C container;
	
	public ContainerEventObject(GreenContext context, Integer uid) {
		super(context.currentWindow());
		this.form = (F) greencode.kernel.$GreenContext.getRequestedForm(context);
		this.container = (C) greencode.jscript.dom.$Container.getContainers(this.form).get(uid);
	}
}
