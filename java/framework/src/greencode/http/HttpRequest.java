package greencode.http;

import java.util.Date;

import greencode.kernel.GreenContext;
import greencode.util.FileUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public final class HttpRequest extends HttpServletRequestWrapper {	
	private final boolean isAjax;
	private final int cid;
	private final int viewId;
	private Extension extension;
	
	private final long createTime = new Date().getTime();
	
	public HttpRequest(HttpServletRequest request) {
		super(request);
								
		isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) || request.getParameterMap().containsKey("isAjax");
		
		if(request.getParameterMap().containsKey("cid"))
		{
			cid = Integer.parseInt(request.getParameter("cid"));
		}else
			cid = 1;
		
		if(request.getParameterMap().containsKey("viewId"))
		{
			viewId = Integer.parseInt(request.getParameter("viewId"));
		}else
		{
			Integer lastViewID = (Integer) request.getSession().getAttribute("LAST_VIEW_ID");
			if(lastViewID == null)
			{
				lastViewID = 1;				
			}else
				++lastViewID;
			
			viewId = lastViewID;
		}
	}
	
	public boolean isAjax() { return isAjax; }
	
	public int getConversationId() { return cid; }
	
	public Extension getExtension() {
		if(extension == null)
			extension = new Extension(this.getServletPath());
		
		return extension;
	}
	
	public ViewSession getViewSession()
	{
		GreenContext context = GreenContext.getInstance();
		
		ViewSessionContext viewContext = new ViewSessionContext(context.getRequest().getSession());
		
		ViewSession view = viewContext.getViewSession(viewId);
		
		if(view == null)
		{
			view = new ViewSession();
			view.id = viewId;
			view.setContext(viewContext);
			viewContext.views.put(viewId, view);
		}else
		{
			view.isNew = false;
		}
		
		view.lastAccessedTime = createTime;
		
		return view;
	}
		
	public final class Extension
	{		
		String ext;
		
		boolean isCss;
		boolean isJS;
		boolean isView;

		private Extension(String path) {
			String ext = FileUtils.getExtension(path);
			
			isCss = ext.equals("css");
			isJS = ext.equals("js");
			isView = ext.equals("html") || ext.equals("xhtml") || ext.equals("jsp") || ext.equals("htm");
		}
		
		public String get() { return this.ext; }
		
		public boolean isCss() { return this.isCss; }
		public boolean isJS() { return this.isJS; }
		public boolean isView() { return this.isView; }		
	}
}
