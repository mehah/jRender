package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class ArticleElement extends Element {		
	protected ArticleElement(Window window) { super(window, "article"); }
	
	public static ArticleElement cast(Element e) { return ElementHandle.cast(e, ArticleElement.class); }
}
