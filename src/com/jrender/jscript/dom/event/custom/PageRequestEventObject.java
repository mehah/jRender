package com.jrender.jscript.dom.event.custom;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public abstract class PageRequestEventObject extends CustomEventObject {
	protected PageRequestEventObject(Window window) { super(window); }

	private transient Element target;
	public final Boolean empty = false, changeURL = false, keepViewId = false;
	public final String appendToSelector = null, href = null;
	
	public Element getAppendToElement() {
		return getMainElement();
	}
	
	public Element getTarget() {
		if(target == null)
			DOMHandle.registerReturnByProperty(target = ElementHandle.getInstance(window), this, "target");
		
		return target;
	}
}
