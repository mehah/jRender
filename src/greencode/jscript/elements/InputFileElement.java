package greencode.jscript.elements;

import javax.servlet.http.Part;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputFileElement extends InputElementDisabling {
	protected InputFileElement(Window window) { super("file", window); }
	
	public String accept() { return DOMHandle.getVariableValueByProperty(this, "accept", String.class, "propName"); }
	public void accept(String accept) { DOMHandle.setProperty(this, "accept", "accept"); }
	
	public Part partFile() {return DOMHandle.getVariableValueByProperty(this, "$$_file_"+DOMHandle.getUID(this), Part.class, "__partFile");}
	
	public static InputFileElement cast(Element e) { return ElementHandle.cast(e, InputFileElement.class); }
}
