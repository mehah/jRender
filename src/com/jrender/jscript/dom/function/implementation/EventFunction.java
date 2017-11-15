package com.jrender.jscript.dom.function.implementation;

import com.jrender.jscript.dom.event.EventObject;

public abstract class EventFunction implements Function {
	public abstract void init(EventObject event);
}
