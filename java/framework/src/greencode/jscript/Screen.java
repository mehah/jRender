package greencode.jscript;

public final class Screen {
	private final Window window;
	
	Screen(Window window) {
		this.window = window;
	}
	
	public int availHeight()
	{
		return DOMHandle.getVariableValueByProperty(window, "screen.availHeight", int.class, "screen.availHeight");
	}
	
	public int availWidth()
	{
		return DOMHandle.getVariableValueByProperty(window, "screen.availWidth", int.class, "screen.availWidth");
	}
	
	public int colorDepth()
	{
		return DOMHandle.getVariableValueByProperty(window, "screen.colorDepth", int.class, "screen.colorDepth");
	}
	
	public int height()
	{
		return DOMHandle.getVariableValueByProperty(window, "screen.height", int.class, "screen.height");
	}
	
	public int pixelDepth()
	{
		return DOMHandle.getVariableValueByProperty(window, "screen.pixelDepth", int.class, "screen.pixelDepth");
	}
	
	public int width()
	{
		return DOMHandle.getVariableValueByProperty(window, "screen.width", int.class, "screen.width");
	}
}
