package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;
import greencode.jscript.dom.elements.attribute.ScopeAttr;
import greencode.jscript.dom.elements.attribute.SortedAttr;

public class THElement extends Element {
		
	protected THElement(Window window) { super(window, "th"); }

	public void abbr(String abbr) { DOMHandle.setProperty(this, "abbr", abbr); }	
	public String abbr() { return DOMHandle.getVariableValueByProperty(this, "abbr", String.class, "abbr"); }
	
	public void colspan(int colspan) { DOMHandle.setProperty(this, "colspan", colspan); }	
	public Integer colspan() { return DOMHandle.getVariableValueByProperty(this, "colspan", Integer.class, "colspan"); }
	
	public void headers(String headerId) { DOMHandle.setProperty(this, "headers", headerId); }	
	public String headers() { return DOMHandle.getVariableValueByProperty(this, "headers", String.class, "headers"); }

	public void rowspan(int rowspan) { DOMHandle.setProperty(this, "rowspan", rowspan); }	
	public Integer rowspan() { return DOMHandle.getVariableValueByProperty(this, "rowspan", Integer.class, "rowspan"); }
	
	public ScopeAttr scope() { return ScopeAttr.valueOf(DOMHandle.getVariableValueByProperty(this, "scope", String.class, "scope").toUpperCase()); }
	public void scope(ScopeAttr scope) { DOMHandle.setProperty(this, "scope", scope); }	
	
	public SortedAttr sorted() { return SortedAttr.valueOf(DOMHandle.getVariableValueByProperty(this, "sorted", String.class, "sorted").toUpperCase()); }
	public void sorted(SortedAttr sorted) { DOMHandle.setProperty(this, "sorted", sorted); }
	
	public static THElement cast(Element e) { return ElementHandle.cast(e, THElement.class); }
}
