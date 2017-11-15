package com.jrender.kernel;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.jrender.util.LogMessage;

public final class Message {
	final static Map<String, Properties> properties = new HashMap<String, Properties>();
	
	private Message(){}
		
	public static String getMessage(String key) {
		JRenderContext context = JRenderContext.getInstance();
		Properties properties = com.jrender.kernel.$JRenderContext.getCurrentMessagePropertie(context);
		
		final String msg = properties.getProperty(key);
		if(msg == null)
			throw new NullPointerException(LogMessage.getMessage("0006", key, JRenderConfig.Server.Internationalization.getVariantPageByLocale(context.userLocale).fileName));
			
		return msg;
	}
	
	public static String getMessage(String key, Object... args) { return MessageFormat.format(getMessage(key), args); }
}