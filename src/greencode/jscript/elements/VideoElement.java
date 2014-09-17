package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;
import greencode.jscript.elements.attribute.PreloadAttr;

/**Only supported in HTML5.*/
public class VideoElement extends Element {
		
	protected VideoElement(Window window) { super(window, "video"); }

	public void autoplay(Boolean autoplay) { DOMHandle.setProperty(this, "autoplay", autoplay); }	
	public Boolean autoplay() { return DOMHandle.getVariableValueByProperty(this, "autoplay", Boolean.class, "autoplay"); }
	
	public void controls(Boolean controls) { DOMHandle.setProperty(this, "controls", controls); }	
	public Boolean controls() { return DOMHandle.getVariableValueByProperty(this, "controls", Boolean.class, "controls"); }

	public void pixels(Integer pixels) { DOMHandle.setProperty(this, "pixels", pixels); }	
	public Integer pixels() { return DOMHandle.getVariableValueByProperty(this, "pixels", Integer.class, "pixels"); }
	
	public void loop(Boolean loop) { DOMHandle.setProperty(this, "loop", loop); }	
	public Boolean loop() { return DOMHandle.getVariableValueByProperty(this, "loop", Boolean.class, "loop"); }
	
	public void muted(Boolean muted) { DOMHandle.setProperty(this, "muted", muted); }	
	public Boolean muted() { return DOMHandle.getVariableValueByProperty(this, "muted", Boolean.class, "muted"); }
	
	public void poster(String URL) { DOMHandle.setProperty(this, "poster", URL); }	
	public String poster() { return DOMHandle.getVariableValueByProperty(this, "poster", String.class, "poster"); }
	
	public PreloadAttr preload() { return PreloadAttr.valueOf(DOMHandle.getVariableValueByProperty(this, "preload", String.class, "preload").toUpperCase()); }
	public void preload(PreloadAttr preload) { DOMHandle.setProperty(this, "preload", preload); }	
	
	public void src(String src) { DOMHandle.setProperty(this, "src", src); }	
	public String src() { return DOMHandle.getVariableValueByProperty(this, "src", String.class, "src"); }
	
	public void width(Integer pixels) { DOMHandle.setProperty(this, "width", pixels); }	
	public Integer width() { return DOMHandle.getVariableValueByProperty(this, "width", Integer.class, "width"); }
	
	public static VideoElement cast(Element e) { return ElementHandle.cast(e, VideoElement.class); }
}
