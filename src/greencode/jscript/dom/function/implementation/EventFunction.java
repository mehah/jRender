package greencode.jscript.dom.function.implementation;

import greencode.jscript.dom.event.EventObject;

public abstract class EventFunction implements Function {
	public abstract void init(EventObject event);
}
