package com.jrender.jscript.dom;

public final class DocumentHandle {
	@SuppressWarnings("unchecked")
	public static<F extends Form> F removeForm(Document document, Class<F> formClass) { return (F) document.forms.remove(formClass); }
}
