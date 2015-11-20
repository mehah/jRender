package greencode.kernel;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

public final class Message {
	final static HashMap<String, Properties> properties = new HashMap<String, Properties>();
	
	private Message(){}
		
	public static String getMessage(String key) {
		GreenContext context = GreenContext.getInstance();
		Properties properties = context.currentMessagePropertie;
		
		final String msg = properties.getProperty(key);
		if(msg == null)
			throw new NullPointerException(LogMessage.getMessage("green-0006", key, GreenCodeConfig.Server.Internationalization.getVariantPageByLocale(context.userLocale).fileName));
			
		return msg;
	}
	
	public static String getMessage(String key, Object... args) { return MessageFormat.format(getMessage(key), args); }
}