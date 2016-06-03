package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class ArticleElement extends Element {		
	protected ArticleElement(Window window) { super(window, "article"); }
	
	public static ArticleElement cast(Element e) { return ElementHandle.cast(e, ArticleElement.class); }
}
