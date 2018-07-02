package com.jrender.validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jrender.jscript.dom.window.annotation.Validate;
import com.jrender.kernel.JRenderContext;

public final class DataValidation {
	private final JRenderContext context;
	private final Validate requester;
	private final Map<String, Object> sharedData = new HashMap<String, Object>();
	final Set<Class<? extends Validator>> errors = new HashSet<Class<? extends Validator>>();

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

	public boolean hasError() {
		return !errors.isEmpty();
	}
	
	public boolean hasError(Class<Validator> clazz) {
		return errors.contains(clazz);
	}
}
