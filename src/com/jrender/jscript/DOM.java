package com.jrender.jscript;

import java.io.IOException;
import java.util.HashMap;

import com.jrender.exception.ConnectionLost;
import com.jrender.http.ViewSession;
import com.jrender.jscript.dom.Window;
import com.jrender.kernel.DOMScanner;
import com.jrender.kernel.JRenderContext;
import com.jrender.util.LogMessage;

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
	
	static void sendByte(JRenderContext context) throws IOException {
		if(!context.getRequest().isWebSocket() && (!com.jrender.http.$HttpRequest.contentIsHtml(context.getRequest()) || context.getRequest().isIFrameHttpRequest())) {
			if(!com.jrender.kernel.$JRenderContext.flushed(context))  {
				context.getResponse().getWriter().write(txt2kb);
			}
		}
	}
	
	final void flush(boolean buffer) {
		JRenderContext context = JRenderContext.getInstance();				
		try {			
			sendByte(context);

			DOMScanner.sendElements(context);
			
			if(buffer && !context.getRequest().isWebSocket()) context.getResponse().flushBuffer();
			
			com.jrender.kernel.$JRenderContext.flushed(context, true);
		} catch (Exception e) {
			throw new ConnectionLost(LogMessage.getMessage("0011"));
		}
	}
	
	public final void flush() { flush(true); }
}
