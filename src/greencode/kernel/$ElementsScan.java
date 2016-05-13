package greencode.kernel;

import java.io.IOException;

import javax.websocket.RemoteEndpoint.Basic;

import greencode.http.ViewSession;
import greencode.jscript.JSCommand;

public final class $ElementsScan {
	private $ElementsScan() {}
	
	public static ElementsScan getElements(ViewSession viewSession) { return ElementsScan.getElements(viewSession); }
	public static void send(GreenContext context, Object o) throws IOException { ElementsScan.send(context, o); }
	public static void setSync(GreenContext context, int uid, String varName, JSCommand jsCommand) throws IOException { ElementsScan.setSync(context, uid, varName, jsCommand); }
}
