package greencode.jscript;

import greencode.http.ViewSession;

public final class $ {
	private $() {}
	
	public static ViewSession DOM$getViewSession(DOM d)
	{
		return d.viewSession;
	}
	
	public static Integer DOM$getUID(DOM d)
	{
		return d.uid;
	}
	
	public static Element Element$getInstance(ViewSession viewSession)
	{
		return new Element(viewSession);
	}
}
