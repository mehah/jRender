package greencode.jscript;

public final class History {
	private final Window window;
	
	History(Window window) { this.window = window; }
	
	public int length() { return DOMHandle.getVariableValueByProperty(window, "history.length", int.class, "history.length"); }
	public void back() { DOMHandle.execCommand(window, "history.back"); }
	public void forward() { DOMHandle.execCommand(window, "history.forward"); }
	public void go(int index) { DOMHandle.execCommand(window, "history.go", index); }
	public void go(String url) { DOMHandle.execCommand(window, "history.go", url); }
}
