package greencode.kernel;

import java.io.IOException;

import greencode.http.ViewSession;
import greencode.jscript.JSExecutor;

public final class $DOMScanner {
	private $DOMScanner() {
	}

	public static DOMScanner getInstance() {
		return new DOMScanner();
	}

	public static DOMScanner getElements(ViewSession viewSession) {
		return DOMScanner.getElements(viewSession);
	}

	public static void send(GreenContext context, Object o) throws IOException {
		DOMScanner.send(context, o);
	}

	public static boolean hasRegisteredCommand(ViewSession viewSession) {
		return !DOMScanner.getElements(viewSession).comm.isEmpty();
	}

	public static void setSync(GreenContext context, int uid, String varName, JSExecutor jsCommand) throws IOException {
		DOMScanner.setSync(context, uid, varName, jsCommand);
	}
}
