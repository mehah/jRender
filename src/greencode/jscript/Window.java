package greencode.jscript;

import greencode.http.Conversation;
import greencode.http.HttpAction;
import greencode.http.HttpRequest;
import greencode.jscript.function.implementation.Function;
import greencode.jscript.function.implementation.SimpleFunction;
import greencode.kernel.ElementsScan;
import greencode.kernel.GreenContext;

import java.io.IOException;
import java.util.HashMap;

public abstract class Window extends EventTarget implements HttpAction {
	
	HashMap<Integer, Function> functions;
	
	public final Document document;
	public final Location location;
	public final History history;
	public final Navigator navigator;
	
	private final Element principalElement; 
	
	protected Window() { this(GreenContext.getInstance()); }
	
	private Window(GreenContext context) {
		super(context.getRequest().getViewSession());
		
		if(context.currentWindow() == null)
			greencode.kernel.$GreenContext.setCurrentWindow(context, window);
		
		uid = 2; // WINDOW ID
		
		final HttpRequest request = context.getRequest();
		final Conversation currentConversation = request.getConversation();
		
		if(currentConversation.getAttribute("location") == null || !request.isAjax()) {
			this.location = new Location(context.getRequest(), this);
			this.history = new History(this);
			this.navigator = new Navigator(context.getRequest(), this);
			this.document = new Document(this);
			this.principalElement = new Element(this);
			this.principalElement.uid = 1; // MainElement UID
		
			currentConversation.setAttribute("location", location);
			currentConversation.setAttribute("history", history);
			currentConversation.setAttribute("navigator", navigator);
			currentConversation.setAttribute("document", document);
			currentConversation.setAttribute("principalElement", principalElement);
		}else {
			this.location = (Location) currentConversation.getAttribute("location");
			this.history = (History) currentConversation.getAttribute("history");
			this.navigator = (Navigator) currentConversation.getAttribute("navigator");
			this.document = (Document) currentConversation.getAttribute("document");
			this.principalElement = (Element) currentConversation.getAttribute("principalElement");
		}
		
		if(request.getViewSession().isNew())
			DOMHandle.setProperty(this, "viewId", request.getViewSession().getId());
	}
	
	public Element principalElement() { return principalElement; }
	
	public Screen screen() { return DOMHandle.getVariableValueByProperty(this, "screen", Screen.class, "screen"); }
		
	public abstract void init();
	
	public boolean connectionAborted() {
		try {
			GreenContext context = GreenContext.getInstance();			
			greencode.kernel.$ElementsScan.send(context, null);			
			context.getResponse().flushBuffer();
			
			return false;
		} catch(IOException e) {
			return true;
		}
	}
	
	public int setTimeout(SimpleFunction func, int time) { return setTimeout(new FunctionHandle(func), time); }
	
	public int setInterval(SimpleFunction func, int time) { return setInterval(new FunctionHandle(func), time); }
	
	public void eval(String expression) { DOMHandle.execCommand(this, "eval", expression); }
	
	public void alert(String text) { DOMHandle.execCommand(this, "alert", text); }
	
	public String atob(String encodedStr) {
		return DOMHandle.getVariableValueByCommand(this, "atob."+encodedStr, String.class, "atob", encodedStr);
	}
	
	public void blur() { DOMHandle.execCommand(this, "blur"); }
	
	public String btoa(String str) { return DOMHandle.getVariableValueByCommand(this, "btoa."+str, String.class, "btoa", str); }
	
	public void clearInterval(int uid) {
		DOMHandle.execCommand(this, "clearInterval", DOMHandle.getDefaultIdToRegisterReturn(uid));
		DOMHandle.removeRegisteredReturn(this, uid);
	}
	
	public void clearTimeout(int uid) {
		DOMHandle.execCommand(this, "clearTimeout", DOMHandle.getDefaultIdToRegisterReturn(uid));
		DOMHandle.removeRegisteredReturn(this, uid);
	}
	
	public void close() { DOMHandle.execCommand(this, "close"); }
	
	public Boolean confirm(String message) {
		return DOMHandle.getVariableValueByCommandNoCache(this, "confirm", Boolean.class, "confirm", message);
	}
	
	public void focus() { DOMHandle.execCommand(this, "focus"); }
	
	public void moveBy(int x, int y) { DOMHandle.execCommand(this, "moveBy", x, y); }
	
	public void moveTo(int x, int y) { DOMHandle.execCommand(this, "moveTo", x, y); }
	
	public void print() { DOMHandle.execCommand(this, "print"); }
	
	public String prompt(String text) { return prompt(text, null); }
	
	public String prompt(String text, String defaultText) {
		return DOMHandle.getVariableValueByCommandNoCache(this, "prompt", String.class, "prompt", text, defaultText);
	}
	
	public void resizeBy(int width, int height) { DOMHandle.execCommand(this, "resizeBy", width, height); }
	
	public void resizeTo(int width, int height) { DOMHandle.execCommand(this, "resizeTo", width, height); }
	
	public void scrollBy(int xnum, int ynum) { DOMHandle.execCommand(this, "scrollBy", xnum, ynum); }
	
	public void scrollTo(int xpos, int ypos) { DOMHandle.execCommand(this, "scrollTo", xpos, ypos); }
	
	public int setTimeout(FunctionHandle handle, int time) {
		int hashcode = handle.hashCode();
		DOMHandle.registerReturnByCommand(this, hashcode, "setTimeout", handle, time);
		return hashcode;
	}
	
	public int setInterval(FunctionHandle handle, int time) {
		int hashcode = handle.hashCode();
		DOMHandle.registerReturnByCommand(this, hashcode, "setInterval", handle, time);
		return hashcode;
	}
	
	public void stop() { DOMHandle.execCommand(this, "stop"); }
	
	public Boolean fullScreen() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "fullScreen", Boolean.class, "fullScreen");
	}
	
	public Integer outerHeight() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "outerHeight", Integer.class, "outerHeight");
	}
	
	public Integer outerWidth() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "outerWidth", Integer.class, "outerWidth");
	}
	
	public Integer innerHeight() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "innerHeight", Integer.class, "innerHeight");
	}
	
	public Integer innerWidth() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "innerWidth", Integer.class, "innerWidth");
	}
	
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	public void name(String name) { DOMHandle.setProperty(this, "name", name); }
	
	public String defaultStatus() { return DOMHandle.getVariableValueByProperty(this, "defaultStatus", String.class, "defaultStatus"); }
	
	public void defaultStatus(String status) { DOMHandle.setProperty(this, "defaultStatus", status); }
	
	public Integer pageXOffset() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "pageXOffset", Integer.class, "pageXOffset");
	}
	
	public Integer pageYOffset() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "pageYOffset", Integer.class, "pageYOffset");
	}
	
	public Integer screenLeft() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "screenLeft", Integer.class, "screenLeft");
	}
	
	public Integer screenTop() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "screenTop", Integer.class, "screenTop");
	}
	
	public Integer screenX() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "screenX", Integer.class, "screenX");
	}
	
	public Integer screenY() {
		return DOMHandle.getVariableValueByPropertyNoCache(this, "screenY", Integer.class, "screenY");
	}
	
	public String status()
	{return DOMHandle.getVariableValueByProperty(this, "status", String.class, "status"); }
	
	public void status(String status)
	{ DOMHandle.setProperty(this, "status", status); }
}
