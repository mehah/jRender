package greencode.validator;

import java.util.HashMap;
import java.util.Map;

import greencode.jscript.dom.window.annotation.Validate;
import greencode.kernel.GreenContext;

public final class DataValidation {
	private final GreenContext context;
	private final Validate requester;
	private final Map<String, Object> sharedData = new HashMap<String, Object>();

	public DataValidation(GreenContext context, Validate requester) {
		this.context = context;
		this.requester = requester;
	}

	public Validate getRequester() {
		return requester;
	}

	public Map<String, Object> getSharedData() {
		return sharedData;
	}

	public GreenContext getContext() {
		return context;
	}
}
