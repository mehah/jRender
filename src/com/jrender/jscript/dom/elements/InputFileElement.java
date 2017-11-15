package com.jrender.jscript.dom.elements;

import javax.servlet.http.Part;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class InputFileElement extends InputElementDisabling<Part> {
	protected InputFileElement(Window window) {
		super("file", window);
	}

	public String accept() {
		return DOMHandle.getVariableValueByProperty(this, "accept", String.class, "propName");
	}

	public void accept(String accept) {
		DOMHandle.setProperty(this, "accept", "accept");
	}

	public Part partFile() {
		return DOMHandle.getVariableValueByProperty(this, "$$_file_" + DOMHandle.getUID(this), Part.class,
				"__partFile");
	}

	public static InputFileElement cast(Element e) {
		return ElementHandle.cast(e, InputFileElement.class);
	}
}
