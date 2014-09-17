package greencode.kernel;

import java.io.IOException;

import greencode.http.ViewSession;

public final class $ElementsScan {
	private $ElementsScan() {}
	
	public static ElementsScan getElements(ViewSession viewSession) { return ElementsScan.getElements(viewSession); }
	public static void send(GreenContext context, Object o) throws IOException { ElementsScan.send(context, o); }
}
