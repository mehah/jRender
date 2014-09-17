package greencode.jscript.form;

import greencode.jscript.Form;
import greencode.jscript.Window;

public abstract class Validator {
	public abstract boolean validate(final Window window, final Form form, final String name, final Object value, final String[] labels);
}
