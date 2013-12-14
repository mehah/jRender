package greencode.jscript;

import greencode.http.HttpRequest;

public final class Location {
	private final Window window;
	
	public final String host;
	public final String hostName;
	public final String href;
	public final String pathName;
	public final int port;
	public final String protocol;
	public final String search;
	
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
	
	public String hash()
	{
		return DOMHandle.getVariableValueByProperty(window, "location.hash", String.class, "location.hash");
	}
	
	public void href(String href)
	{
		DOMHandle.setProperty(window, "location.href", href);
	}
	
	public void reload(boolean forceGet)
	{
		DOMHandle.execCommand(window, "location.reload", forceGet);
	}
	
	public void replace(String url)
	{
		DOMHandle.execCommand(window, "location.replace", url);
	}
	
	public void assign(String url)
	{
		DOMHandle.execCommand(window, "location.assign", url);
	}
}
