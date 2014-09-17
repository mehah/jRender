package greencode.http;

import java.security.Principal;
import java.util.HashMap;
import java.util.regex.Pattern;

import greencode.exception.OperationNotAllowedException;
import greencode.http.enumeration.RequestMethod;
import greencode.http.security.UserPrincipal;
import greencode.kernel.GreenContext;
import greencode.kernel.LogMessage;
import greencode.util.FileUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public final class HttpRequest extends HttpServletRequestWrapper {
	final HashMap<String, String> params = new HashMap<String, String>();	
	final boolean contentIsHtml;
	
	private final boolean isAjax, isIFrameHttpRequest;
	private final int cid, viewId;
	
	private Boolean isMobile = null;
	
	private final Conversation conversation;
	
	private Extension extension;
	private ViewSession viewSession = null;
	private UserPrincipal userPrincipal;
	
	public HttpRequest(HttpServletRequest request) {
		super(request);
						
		isIFrameHttpRequest = request.getParameterMap().containsKey("isAjax");
		isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) || isIFrameHttpRequest;
		
		contentIsHtml = !isAjax || Boolean.parseBoolean(request.getParameter("__contentIsHtml"));
		
		String v;
		if((v = request.getParameter("viewId")) != null && !v.isEmpty()) {
			viewId = Integer.parseInt(v);
			getViewSession().isNew = false;
		}else
		{
			if(!isAjax) request.getSession(true);			
			Integer lastViewID = (Integer) request.getSession().getAttribute("LAST_VIEW_ID");
			
			request.getSession().setAttribute("LAST_VIEW_ID", viewId = (lastViewID == null ? 1 : ++lastViewID));
		}
		
		getViewSession().access();
		
		this.cid = (v = request.getParameter("cid")) == null ? 1 : Integer.parseInt(request.getParameter("cid"));
		
		this.conversation = new Conversation(getViewSession(), cid);
		
		this.userPrincipal = (UserPrincipal) getSession().getAttribute("__USER_PRINCIPAL__");
		
		
	}
	
	public Principal getUserPrincipal() { return this.userPrincipal; }
	
	public void setUserPrincipal(UserPrincipal user) {
		if(this.userPrincipal != null)
			throw new OperationNotAllowedException(LogMessage.getMessage("green-0039"));
		
		getSession().setAttribute("__USER_PRINCIPAL__", this.userPrincipal = user);
	}
	
	private static final Pattern pattern = Pattern.compile("up.browser|up.link|windows ce|iphone|iemobile|mini|mmp|symbian|midp|wap|phone|pocket|mobile|pda|psp", Pattern.CASE_INSENSITIVE);
	
	static boolean isMobile(String userAgent) { return pattern.matcher(userAgent).find(); }
	
	public boolean isMobile() {
		if(isMobile == null)
			isMobile = isMobile(this.getHeader("user-agent"));
		
		return isMobile;
	}
	
	public boolean isMethod(RequestMethod methodType) { return getMethod().equals(methodType.name()); }
	
	public boolean isAjax() { return isAjax; }
	
	public int getConversationId() { return cid; }
	
	public Extension getExtension() {		
		return (extension != null ? extension = new Extension(this.getServletPath()) : extension);
	}
	
	public ViewSession getViewSession() {
		if(this.viewSession == null) {
			ViewSessionContext viewContext = new ViewSessionContext(getSession());
			
			this.viewSession = viewContext.getViewSession(viewId);
			
			if(this.viewSession == null) {
				if(GreenContext.getInstance().getResponse().isCommitted())
					throw new IllegalStateException("Cannot create a view session after the response has been committed");
				
				this.viewSession = new ViewSession(viewId, getSession(), viewContext);
			}
		}
		
		return this.viewSession;
	}
	
	public Conversation getConversation() { return conversation; }
	
	public final class Extension {		
		private final String ext;		
		private final boolean isCss, isJS, isView;

		private Extension(String path) {
			ext = FileUtils.getExtension(path);		
			isCss = ext.equals("css");
			isJS = ext.equals("js");
			isView = ext.equals("html") || ext.equals("xhtml") || ext.equals("jsp") || ext.equals("htm");
		}
		
		public String get() { return this.ext; }		
		public boolean isCss() { return this.isCss; }
		public boolean isJS() { return this.isJS; }
		public boolean isView() { return this.isView; }		
	}
	
	@Override
	public String getParameter(String name) {
		String value = params.get(name);
		if(value == null)
			return super.getParameter(name);
		
		return value;
	}

	public boolean isIFrameHttpRequest() { return isIFrameHttpRequest; }
}
