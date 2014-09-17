package greencode.jscript.function.implementation;

import greencode.jscript.event.EventObject;

public abstract class EventFunction implements Function {
	public abstract void init(EventObject event);
}
