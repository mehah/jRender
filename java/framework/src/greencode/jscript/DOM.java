package greencode.jscript;

import greencode.http.ViewSession;
import greencode.kernel.Console;
import greencode.kernel.ElementsScan;
import greencode.kernel.GreenContext;
import greencode.kernel.LogMessage;

import java.io.IOException;
import java.util.HashMap;

public abstract class DOM {
	protected final ViewSession viewSession;
	HashMap<String, Object> variables = new HashMap<String, Object>();
	
	int uid = hashCode();
	
	public DOM() {
		this.viewSession = null;
	}
	
	DOM(ViewSession viewSession) {
		this.viewSession = viewSession;
	}
	
	/*
	 * Microsoft Internet Explorer
	 * somente começara a mostrar dados depois de terem recebido 256 bytes de saída,
	 * então você vai precisar enviar espaço em branco antes de descarregar para o browser para mostrar a página. 
	 */
	static final String txt2kb;
	
	static {
		int _2kb = 4000;							
		StringBuilder _txt2kb = new StringBuilder();
		for (; --_2kb > 0;)
			_txt2kb.append(",");
		
		txt2kb = _txt2kb.toString();
	}
	
	static void send2kbInternetExplorer(GreenContext context) throws IOException
	{
		if(context.getRequest().isAjax() && !greencode.kernel.$.GreenContext$flushed(context))
		{
			boolean isInternetExplorer = context.getRequest().getHeader("User-Agent").indexOf("MSIE") > -1;
			if(isInternetExplorer)
			{
				context.getResponse().getWriter().write(txt2kb);
				greencode.kernel.$.GreenContext$flushed(context, true);
			}
		}
	}
	
	public void flush()
	{
		GreenContext context = GreenContext.getInstance();
				
		try {			
			send2kbInternetExplorer(context);

			ElementsScan.sendElements(context);
			context.getResponse().flushBuffer();
		} catch (IOException e) {
			Console.warning(LogMessage.getMessage("green-0011"));
			
			//TODO: Veriricar futuramente oq fazer com isso.
			//greencode.kernel.$.GreenContext$Comet$connectionLost(context, true);
		}
	}
}
