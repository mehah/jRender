package greencode.kernel;

import greencode.exception.GreencodeError;

public abstract class Console {

	public static void error(Throwable e) {
		System.err.print("\n[" + Core.projectName + ":Error]: ");
		throw new GreencodeError(e);
	}
	
	public static void error(String msg) {
		throw new GreencodeError("\n[" + Core.projectName + ":Error]: " + msg);
	}

	public static void warning(String msg) {
		System.err.println("[" + Core.projectName + ":Warning]: " + msg);
	}

	public static void log(String msg) {
		if(GreenCodeConfig.Server.writeLog)
			System.out.println("[" + Core.projectName + "] " + msg);
	}
}
