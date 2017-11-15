package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class ArticleElement extends Element {		
	protected ArticleElement(Window window) { super(window, "article"); }
	
	public static ArticleElement cast(Element e) { return ElementHandle.cast(e, ArticleElement.class); }
}
