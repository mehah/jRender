package greencode.jscript.function.implementation;

import greencode.jscript.event.custom.CustomEventObject;

public abstract class CustomEventFunction implements Function {
	public abstract void init(CustomEventObject event);
}
