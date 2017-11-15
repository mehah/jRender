package com.jrender.jscript.dom.event.custom;

import com.jrender.jscript.DOM;
import com.jrender.jscript.dom.Form;
import com.jrender.jscript.dom.elements.custom.ContainerElement;
import com.jrender.kernel.JRenderContext;

public final class ContainerEventObject<F extends Form, C extends ContainerElement<C>> extends DOM {
	public final F form;
	public final C container;
	
	public ContainerEventObject(JRenderContext context, Integer uid) {
		super(context.currentWindow());
		this.form = (F) com.jrender.kernel.$JRenderContext.getRequestedForm(context);
		this.container = (C) com.jrender.jscript.dom.$Container.getContainers(this.form).get(uid);
	}
}
