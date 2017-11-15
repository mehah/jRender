package com.jrender.kernel;

import java.io.IOException;

import com.jrender.http.ViewSession;
import com.jrender.jscript.JSExecutor;

public final class $DOMScanner {
	private $DOMScanner() {
	}

	public static DOMScanner getInstance() {
		return new DOMScanner();
	}

	public static DOMScanner getElements(ViewSession viewSession) {
		return DOMScanner.getElements(viewSession);
	}

	public static void send(JRenderContext context, Object o) throws IOException {
		DOMScanner.send(context, o);
	}

	public static boolean hasRegisteredCommand(ViewSession viewSession) {
		return !DOMScanner.getElements(viewSession).comm.isEmpty();
	}

	public static void setSync(JRenderContext context, int uid, String varName, JSExecutor jsCommand) throws IOException {
		DOMScanner.setSync(context, uid, varName, jsCommand);
	}
}
