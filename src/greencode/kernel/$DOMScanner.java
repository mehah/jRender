package greencode.kernel;

import java.io.IOException;

import greencode.http.ViewSession;
import greencode.jscript.JSCommand;

public final class $DOMScanner {
	private $DOMScanner() {}
	
	public static DOMScanner getElements(ViewSession viewSession) { return DOMScanner.getElements(viewSession); }
	public static void send(GreenContext context, Object o) throws IOException { DOMScanner.send(context, o); }
	public static void setSync(GreenContext context, int uid, String varName, JSCommand jsCommand) throws IOException { DOMScanner.setSync(context, uid, varName, jsCommand); }
}
