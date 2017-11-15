package com.jrender.validator;

import java.util.HashMap;
import java.util.Map;

import com.jrender.jscript.dom.window.annotation.Validate;
import com.jrender.kernel.JRenderContext;

public final class DataValidation {
	private final JRenderContext context;
	private final Validate requester;
	private final Map<String, Object> sharedData = new HashMap<String, Object>();

	public DataValidation(JRenderContext context, Validate requester) {
		this.context = context;
		this.requester = requester;
	}

	public Validate getRequester() {
		return requester;
	}

	public Map<String, Object> getSharedData() {
		return sharedData;
	}

	public JRenderContext getContext() {
		return context;
	}
}
