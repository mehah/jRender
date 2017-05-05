package greencode.kernel;

import greencode.exception.GreencodeError;

public abstract class Console {
	static final String msgError = "\n[" + Core.PROJECT_NAME + ":Error]: ";
	
	public static void error(Throwable e) {
		System.err.print(msgError);
		throw new GreencodeError(e);
	}
	
	public static void error(String msg) {
		throw new GreencodeError("\n[" + Core.PROJECT_NAME + ":Error]: " + msg);
	}

	public static void warning(String msg) {
		System.err.println("[" + Core.PROJECT_NAME + ":Warning]: " + msg);
	}

	public static void log(String msg) {
		if(GreenCodeConfig.Server.log)
			print(msg);
	}
	
	public static void print(String msg) {
		System.out.println("[" + Core.PROJECT_NAME + "] " + msg);
	}
}
