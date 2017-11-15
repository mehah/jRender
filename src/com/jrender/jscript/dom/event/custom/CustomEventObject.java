package com.jrender.jscript.dom.event.custom;

import com.jrender.jscript.DOM;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class CustomEventObject extends DOM {
	protected CustomEventObject(Window window) { super(window); }

	transient Element mainElement;
	
	public Element getMainElement() {
		if(mainElement == null)
			DOMHandle.registerReturnByProperty(mainElement = ElementHandle.getInstance(window), this, "mainElement");
		
		return mainElement;
	}
}
