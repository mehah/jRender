package com.jrender.kernel.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.jrender.jscript.DOM;
import com.jrender.jscript.DOMHandle;

public class DOMSerializer implements com.google.gson.JsonSerializer<DOM> {
	public JsonElement serialize(DOM arg0, Type arg1, JsonSerializationContext arg2) {
		return arg2.serialize("$:"+DOMHandle.getUID(arg0));
	}
}
