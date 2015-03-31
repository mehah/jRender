package greencode.validator;

import greencode.jscript.Form;
import greencode.jscript.Window;
import greencode.jscript.elements.custom.ContainerElement;

public interface Validator {
	public boolean validate(final Window window, final Form form, final ContainerElement<?> container, final String name, final Object value, final String[] labels, final DataValidation dataValidation);
}
