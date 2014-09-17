package greencode.jscript.elements;

import greencode.jscript.Window;

public class $Element {	
	public static BodyElement getBodyInstance(Window window) { return new BodyElement(window); }
	public static HeadElement getHeadInstance(Window window) { return new HeadElement(window); }
}
