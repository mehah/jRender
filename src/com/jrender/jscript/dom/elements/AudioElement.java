package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.elements.attribute.PreloadAttr;

/**Only supported in HTML5.*/
public class AudioElement extends Element {
		
	protected AudioElement(Window window) { super(window, "audio"); }
	
	public Boolean autoplay() { return DOMHandle.getVariableValueByProperty(this, "autoplay", Boolean.class, "autoplay"); }
	public void autoplay(Boolean autoplay) { DOMHandle.setProperty(this, "autoplay", autoplay); }

	public Boolean controls() { return DOMHandle.getVariableValueByProperty(this, "controls", Boolean.class, "controls"); }
	public void controls(Boolean controls) { DOMHandle.setProperty(this, "controls", controls); }
	
	public Boolean loop() { return DOMHandle.getVariableValueByProperty(this, "loop", Boolean.class, "loop"); }
	public void loop(Boolean loop) { DOMHandle.setProperty(this, "loop", loop); }
	
	public Boolean muted() { return DOMHandle.getVariableValueByProperty(this, "muted", Boolean.class, "muted"); }
	public void muted(Boolean muted) { DOMHandle.setProperty(this, "muted", muted); }
	
	public PreloadAttr preload() { return PreloadAttr.valueOf(DOMHandle.getVariableValueByProperty(this, "preload", String.class, "preload").toUpperCase()); }
	public void preload(PreloadAttr preload) { DOMHandle.setProperty(this, "preload", preload); }
	
	public void src(String src) { DOMHandle.setProperty(this, "src", src); }	
	public String src() { return DOMHandle.getVariableValueByProperty(this, "src", String.class, "src"); }
	
	public static AudioElement cast(Element e) { return ElementHandle.cast(e, AudioElement.class); }
}
