package com.jrender.kernel.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jrender.jscript.DOM;
import com.jrender.jscript.dom.Window;
import com.jrender.kernel.JRenderContext;
import com.jrender.util.GenericReflection;

public class DOMDeserializer implements com.google.gson.JsonDeserializer<DOM> {
	private final JRenderContext context;
	
	public DOMDeserializer(JRenderContext context) { this.context = context; }

	public DOM deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		try {
			DOM d = (DOM) GenericReflection.NoThrow.getDeclaredConstrutor((Class<?>)arg1, Window.class).newInstance(context.currentWindow());
			
			for (Entry<String, JsonElement> entry : ((JsonObject) arg0).entrySet()) {
				Field f = GenericReflection.NoThrow.getDeclaredField(d.getClass(), entry.getKey());
				f.set(d, arg2.deserialize(entry.getValue(), f.getType()));
			}
			
			return d;
		} catch (Exception e) {
			throw new JsonParseException(e);
		}
	}

}
