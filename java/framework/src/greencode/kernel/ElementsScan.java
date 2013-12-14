package greencode.kernel;

import greencode.http.ViewSession;
import greencode.jscript.DOM;
import greencode.jscript.JSCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElementsScan {
	public final List<JSCommand> pComm = new ArrayList<JSCommand>(); /* prioritizedCommands */
	private final List<JSCommand> comm = new ArrayList<JSCommand>(); /* commands */
	
	private ElementsScan() {}
	
	static ElementsScan getElements(ViewSession viewSession)
	{
		ElementsScan elements = (ElementsScan) viewSession.getAttribute("_ELEMENTS"); 
		if(elements == null)
		{
			elements = new ElementsScan();
			viewSession.setAttribute("_ELEMENTS", elements);
		}
		
		return elements;
	}
	
	public static void registerCommand(DOM e, boolean prioritize, String name, Object... args)
	{
		ElementsScan scan = getElements(greencode.jscript.$.DOM$getViewSession(e));
		
		(prioritize ? scan.pComm : scan.comm).add(new JSCommand(e, name, args));
	}
	
	public static void sendElements(GreenContext context) throws IOException
	{
		ElementsScan elements = ElementsScan.getElements(context.getRequest().getViewSession());
		
		send(context, elements);
		
		elements.pComm.clear();
		elements.comm.clear();
	}
	
	static void send(GreenContext context, Object o) throws IOException
	{
		if(context.getRequest().isAjax())
		{
			context.getResponse().getWriter().write(','+context.getGsonInstance().toJson(o));
		}else
		{
			context.getResponse().getWriter().write(
				new StringBuilder("<div id=\"JSON_CONTENT\" style=\"display: none;\">")
					.append(context.getGsonInstance().toJson(o))
					.append("</div>").toString()
			);
		}
	}
}
