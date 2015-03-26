package greencode.jscript.function.implementation;

import greencode.jscript.event.custom.PageRequestEventObject;

public abstract class PageRequestEventFunction implements Function {
	public abstract void init(PageRequestEventObject event);
}
