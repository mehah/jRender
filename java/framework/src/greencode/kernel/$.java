package greencode.kernel;

import greencode.http.ViewSession;

public final class $ {
	private $() {}
	
	public static ElementsScan getElementsScan(ViewSession viewSession)
	{
		return ElementsScan.getElements(viewSession);
	}
	
	public static boolean GreenContext$flushed(GreenContext context)
	{
		return context.flushed;
	}
	
	public static void GreenContext$flushed(GreenContext context, boolean flushed)
	{
		context.flushed = flushed;
	}
	
	public static boolean GreenContext$forceSynchronization(GreenContext context)
	{
		return context.forceSynchronization;
	}
}
