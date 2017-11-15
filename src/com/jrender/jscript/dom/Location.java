package com.jrender.jscript.dom;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import com.jrender.http.HttpRequest;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.window.annotation.Page;
import com.jrender.kernel.JRenderConfig;

public final class Location {
	private final Window window;

	public final String host, hostName, href, pathName, protocol, search;
	public final int port;

	Location(HttpRequest request, Window window) {
		this.window = window;

		this.host = request.getRemoteHost() + ":" + request.getLocalPort();
		this.hostName = request.getRemoteHost();
		this.href = request.getRequestURL().toString();
		this.pathName = request.getRequestURI();
		this.port = request.getLocalPort();
		this.protocol = href.substring(0, href.indexOf('/'));
		this.search = "?" + request.getQueryString();
	}

	public String hash() {
		return DOMHandle.getVariableValueByProperty(window, "location.hash", String.class, "location.hash");
	}

	public void href(String href) {
		DOMHandle.setProperty(window, "location.href", href);
	}

	public void href(Class<? extends Window> loc) {
		href(loc, null, null);
	}

	public void href(Class<? extends Window> loc, String name) {
		href(loc, name, null);
	}
	
	public void href(Class<? extends Window> loc, Map<String, String> parameters) {
		href(loc, null, parameters);
	}
	
	public void href(Class<? extends Window> loc, String name, Map<String, String> parameters) {
		Page page = WindowHandle.getPageByName(loc, name);

		String _parameters = null;
		if (parameters != null) {
			try {
				StringBuilder sb = new StringBuilder();
				for (Entry<String, String> e : parameters.entrySet()) {
					if (sb.length() > 0) {
						sb.append('&');
					}

					sb.append(URLEncoder.encode(e.getKey(), JRenderConfig.Server.View.charset)).append('=').append(URLEncoder.encode(e.getValue(), JRenderConfig.Server.View.charset));
				}
				_parameters = sb.toString();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}

		String url = com.jrender.kernel.$JRenderContext.getContextPath() + "/" + (page.URLName().isEmpty() ? page.path() : page.URLName());
		if(_parameters != null) {
			url += "?"+_parameters;
		}
		
		DOMHandle.setProperty(window, "location.href", url);
	}

	public void reload(boolean forceGet) {
		DOMHandle.execCommand(window, "location.reload", forceGet);
	}

	public void replace(String url) {
		DOMHandle.execCommand(window, "location.replace", url);
	}

	public void assign(String url) {
		DOMHandle.execCommand(window, "location.assign", url);
	}
}
