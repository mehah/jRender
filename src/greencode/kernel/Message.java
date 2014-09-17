package greencode.kernel;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

public final class Message {
	final static HashMap<String, Properties> properties = new HashMap<String, Properties>();
	
	private Message(){}
	
	private static Properties getPropertie() { return GreenContext.getInstance().currentMessagePropertie; }
	
	public static String getMessage(String key) {
		final String msg = getPropertie().getProperty(key);
		if(msg == null)
			throw new NullPointerException(LogMessage.getMessage("green-0006", key, GreenCodeConfig.Internationalization.getVariantPageByLocale(GreenContext.getInstance().userLocale).fileName));
			
		return msg;
	}
	
	public static String getMessage(String key, Object... args) { return MessageFormat.format(getMessage(key), args); }
}