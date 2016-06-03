package greencode.validator;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.Form;
import greencode.jscript.dom.Window;
import greencode.jscript.dom.elements.custom.ContainerElement;

public interface Validator {
	public boolean validate(final Window window, final Form form, final ContainerElement<?> container, final Element element, final String name, final Object value, final String[] labels, final DataValidation dataValidation);
}
