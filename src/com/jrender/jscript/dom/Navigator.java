package com.jrender.jscript.dom;

import com.jrender.http.HttpRequest;
import com.jrender.jscript.DOMHandle;

public final class Navigator {
	private final Window window;
	private final String userAgent;
	Navigator(HttpRequest request, Window window) {
		this.userAgent = request.getHeader("User-Agent");
		this.window = window;
	}
	
	public String appCodeName() {
		return DOMHandle.getVariableValueByProperty(window, "navigator.appCodeName", String.class, "navigator.appCodeName");
	}
	
	public String appName() {
		return DOMHandle.getVariableValueByProperty(window, "navigator.appName", String.class, "navigator.appName");
	}
	
	public String appVersion() {
		return DOMHandle.getVariableValueByProperty(window, "navigator.appVersion", String.class, "navigator.appVersion");
	}
	
	public Boolean cookieEnabled() {
		return DOMHandle.getVariableValueByProperty(window, "navigator.cookieEnabled", Boolean.class, "navigator.cookieEnabled");
	}
	
	public String language() {
		return DOMHandle.getVariableValueByProperty(window, "navigator.language", String.class, "navigator.language");
	}
	
	public Boolean onLine() {
		return DOMHandle.getVariableValueByPropertyNoCache(window, "navigator.onLine", Boolean.class, "navigator.onLine");
	}
	
	public String platform() {
		return DOMHandle.getVariableValueByProperty(window, "navigator.platform", String.class, "navigator.platform");
	}
	
	public String product() {
		return DOMHandle.getVariableValueByProperty(window, "navigator.product", String.class, "navigator.product");
	}
	
	public String userAgent() { return userAgent; }
	
	public Boolean javaEnabled() {
		return DOMHandle.getVariableValueByCommand(window, "navigator.javaEnabled", Boolean.class, "navigator.javaEnabled");
	}
}
