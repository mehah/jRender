package greencode.util;

import java.text.MessageFormat;
import java.util.Properties;

public final class LogMessage {
	final static Properties instance = new Properties();
	
	private LogMessage(){}
	
	public static String getMessage(String key) { return instance.getProperty(key); }
	
	public static String getMessage(String key, Object... args) { return MessageFormat.format(getMessage(key), args); }
}
