package com.jrender.validator;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.Form;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.elements.custom.ContainerElement;

public interface Validator {
	public boolean validate(final Window window, final Form form, final ContainerElement<?> container, final Element element, final String name, final Object value, final String[] labels, final DataValidation dataValidation);
}
