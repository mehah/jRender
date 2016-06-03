package greencode.jscript;

import java.io.IOException;
import java.util.HashMap;

import greencode.exception.ConnectionLost;
import greencode.http.ViewSession;
import greencode.jscript.dom.Window;
import greencode.kernel.DOMScanner;
import greencode.kernel.GreenContext;
import greencode.util.LogMessage;

public abstract class DOM {
	
	protected final Window window;
	protected final ViewSession viewSession;
	HashMap<String, Object> variables = new HashMap<String, Object>();	
	int uid = hashCode();
	
	protected DOM(Window window) {
		this.window = window;
		this.viewSession = ((DOM)window).viewSession;
	}
	
	protected DOM(ViewSession viewSession) {
		this.window = (Window) this;
		this.viewSession = viewSession;
	}
	
	public void destroy() {
		this.variables = null;
		this.uid = 0;
		DOMHandle.deleteReference(this);
	}
	
	/*
	 * Microsoft Internet Explorer
	 * somente começara a mostrar dados depois de terem recebido 256 bytes de saída,
	 * então irá precisar enviar virgulas antes de descarregar para o browser. 
	 */
	static final String txt2kb;
	
	static {
		int _2kb = 4000;							
		StringBuilder _txt2kb = new StringBuilder();
		for (; --_2kb > 0;)
			_txt2kb.append(",");
		
		txt2kb = _txt2kb.toString();
	}
	
	static void sendByte(GreenContext context) throws IOException {
		if(!context.getRequest().isWebSocket() && (!greencode.http.$HttpRequest.contentIsHtml(context.getRequest()) || context.getRequest().isIFrameHttpRequest())) {
			if(!greencode.kernel.$GreenContext.flushed(context))  {
				context.getResponse().getWriter().write(txt2kb);
			}
		}
	}
	
	final void flush(boolean buffer) {
		GreenContext context = GreenContext.getInstance();				
		try {			
			sendByte(context);

			DOMScanner.sendElements(context);
			
			if(buffer && !context.getRequest().isWebSocket()) context.getResponse().flushBuffer();
			
			greencode.kernel.$GreenContext.flushed(context, true);
		} catch (Exception e) {
			throw new ConnectionLost(LogMessage.getMessage("green-0011"));
		}
	}
	
	public final void flush() { flush(true); }
}
