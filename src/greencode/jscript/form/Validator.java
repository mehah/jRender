package greencode.jscript.form;

import greencode.jscript.Form;
import greencode.jscript.Window;

public interface Validator {
	public boolean validate(final Window window, final Form form, final String name, final Object value, final String[] labels);
}
