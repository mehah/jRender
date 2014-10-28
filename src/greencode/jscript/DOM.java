package greencode.jscript;

import greencode.exception.ConnectionLost;
import greencode.http.ViewSession;
import greencode.kernel.ElementsScan;
import greencode.kernel.GreenContext;
import greencode.kernel.LogMessage;

import java.io.IOException;
import java.util.HashMap;

public abstract class DOM {
	
	protected final Window window;
	protected final ViewSession viewSession;
	HashMap<String, Object> variables = new HashMap<String, Object>();
	
	int uid = hashCode();
	
	protected DOM(Window window) {
		this.window = window;
		this.viewSession = window.viewSession;
	}
	
	DOM(ViewSession viewSession) {
		this.window = (Window) this;
		this.viewSession = viewSession;
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
		if(!greencode.http.$HttpRequest.contentIsHtml(context.getRequest()) || context.getRequest().isIFrameHttpRequest()) {
			if(!greencode.kernel.$GreenContext.flushed(context))  {
				//TODO: fazer tb para Safari
				//boolean isInternetExplorer = context.getRequest().getHeader("User-Agent").indexOf("MSIE") > -1;
				//if(isInternetExplorer)
					context.getResponse().getWriter().write(txt2kb);
			}
		}
	}
	
	void flush(boolean buffer) {
		GreenContext context = GreenContext.getInstance();
				
		try {			
			sendByte(context);

			ElementsScan.sendElements(context);
			
			if(buffer) context.getResponse().flushBuffer();
			
			greencode.kernel.$GreenContext.flushed(context, true);
		} catch (IOException e) {
			throw new ConnectionLost(LogMessage.getMessage("green-0011"));
		}
	}
	
	public void flush() { flush(true); }
}
