package com.jrender.kernel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.window.annotation.Page;
import com.jrender.jscript.dom.window.annotation.PageParameter;
import com.jrender.util.FileUtils;

public final class Router {
	public final String name;

	public final String path;

	public final Mobile mobile;

	public final String urlName;
	public final String selector;
	public final String ajaxSelector;
	public final String[] rules;

	public final String jsModule;

	public final Map<String, String[]> parameters;

	Router(String name, String path, Mobile mobile, String urlName, String selector, String ajaxSelector, String[] rules, String jsModule, Map<String, String[]> parameters) {
		this.name = name;
		this.path = path;
		this.mobile = mobile;
		this.urlName = urlName;
		this.selector = selector;
		this.ajaxSelector = ajaxSelector;
		this.rules = rules;
		this.jsModule = jsModule;
		this.parameters = parameters;
	}

	public static void register(Class<? extends Window> c, String name, String path, Mobile mobile, String urlName, String selector, String ajaxSelector, String[] rules, String jsModule, Map<String, String[]> parameters) {
		try {
			FileWeb.registerPage(Thread.currentThread().getContextClassLoader(), c, new Router(name, path, mobile, urlName, selector, ajaxSelector, rules, jsModule, parameters), new File(FileUtils.getFileInWebContent("jscript/").getPath() + "/jrender/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void unregister(String path) {
		FileWeb.unregisterPage(path);
	}

	Router(Page page) {
		this.name = page.name().isEmpty() ? null : page.name();
		this.path = page.path().isEmpty() ? null : page.path();
		this.mobile = page.mobile().path().isEmpty() ? null : new Mobile(page.mobile());
		this.urlName = page.URLName().isEmpty() ? null : page.URLName();
		this.selector = page.selector().isEmpty() ? null : page.selector();
		this.ajaxSelector = page.ajaxSelector().isEmpty() ? null : page.ajaxSelector();
		this.rules = page.rules().length == 0 ? null : page.rules();
		this.jsModule = page.jsModule().isEmpty() ? null : page.jsModule();

		if (!(page.parameters().length == 1 && page.parameters()[0].name().isEmpty())) {
			this.parameters = new HashMap<String, String[]>();
			for (PageParameter param : page.parameters()) {
				this.parameters.put(param.name(), param.value());
			}
		} else {
			this.parameters = null;
		}
	}

	public static class Mobile {
		public final String path;
		public final String selector;
		public final String ajaxSelector;
		public final Map<String, String[]> parameters;

		public Mobile(String path, String selector, String ajaxSelector, Map<String, String[]> parameters) {
			this.path = path;
			this.selector = selector;
			this.ajaxSelector = ajaxSelector;
			this.parameters = parameters;
		}

		Mobile(com.jrender.jscript.dom.window.annotation.Mobile mobileAnnotation) {
			this.path = mobileAnnotation.path().isEmpty() ? null : mobileAnnotation.path();
			this.selector = mobileAnnotation.selector().isEmpty() ? null : mobileAnnotation.selector();
			this.ajaxSelector = mobileAnnotation.ajaxSelector().isEmpty() ? null : mobileAnnotation.ajaxSelector();

			if (!(mobileAnnotation.parameters().length == 1 && mobileAnnotation.parameters()[0].name().isEmpty())) {
				this.parameters = new HashMap<String, String[]>();
				for (PageParameter param : mobileAnnotation.parameters()) {
					this.parameters.put(param.name(), param.value());
				}
			} else {
				this.parameters = null;
			}
		}

	}
}
