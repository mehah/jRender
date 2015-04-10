package greencode.jscript;

import greencode.http.HttpRequest;
import greencode.jscript.window.annotation.Page;

public final class Location {
	private final Window window;
	
	public final String host, hostName, href, pathName, protocol, search;
	public final int port;
	
	Location(HttpRequest request, Window window) {
		this.window = window;
		
		this.host = request.getRemoteHost()+":"+request.getLocalPort();
		this.hostName = request.getRemoteHost();
		this.href = request.getRequestURL().toString();
		this.pathName = request.getRequestURI();
		this.port = request.getLocalPort();
		this.protocol = href.substring(0, href.indexOf('/'));
		this.search = "?"+request.getQueryString();
	}
	
	public String hash() {
		return DOMHandle.getVariableValueByProperty(window, "location.hash", String.class, "location.hash");
	}
	
	public void href(String href) { DOMHandle.setProperty(window, "location.href", href); }
	
	public void href(Class<? extends Window> loc) {	
		href(loc, null);
	}
	
	public void href(Class<? extends Window> loc, String name) {	
		Page page = WindowHandle.getPageByName(loc, name);
		DOMHandle.setProperty(window, "location.href", greencode.kernel.$GreenContext.getContextPath()+"/"+(page.URLName().isEmpty() ? page.path() : page.URLName()));
	}
	
	public void reload(boolean forceGet) { DOMHandle.execCommand(window, "location.reload", forceGet); }
	
	public void replace(String url) { DOMHandle.execCommand(window, "location.replace", url); }
	
	public void assign(String url) { DOMHandle.execCommand(window, "location.assign", url); }
}
