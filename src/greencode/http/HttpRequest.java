package greencode.http;

import java.security.Principal;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import greencode.exception.OperationNotAllowedException;
import greencode.http.enumeration.RequestMethod;
import greencode.http.security.UserPrincipal;
import greencode.kernel.LogMessage;
import greencode.kernel.WebSocketData;
import greencode.util.FileUtils;

public final class HttpRequest extends HttpServletRequestWrapper {
	final HashMap<String, String[]> params;
	final boolean contentIsHtml;
	final boolean __contentIsHtml;

	private final boolean isAjax, isIFrameHttpRequest;
	private final int cid, viewId;
	private final String methodType;

	private Boolean isMobile = null;

	private final Conversation conversation;
	private final ServletResponse response;
	private final Session webSocketSession;
	
	private HttpSession httpSession;

	private Extension extension;
	private ViewSession viewSession = null;
	private UserPrincipal userPrincipal;
	
	private final String remoteHost;
	private final StringBuffer requestURL;
	private final String requestURI;
	private final int localPort;

	public HttpRequest(HttpServletRequest request, HttpServletResponse response) {
		this(request, response, null);
	}

	public HttpRequest(HttpServletRequest request, HttpServletResponse response, WebSocketData wsData) {
		super(request);

		if (wsData != null) {
			this.webSocketSession = wsData.getSession();
			this.params = wsData.getParameters();
			this.httpSession = wsData.getHttpSession();
			this.isIFrameHttpRequest = false;
			this.isAjax = false;
			this.methodType = RequestMethod.GET.name();
			this.remoteHost = wsData.getRemoteHost();
			this.requestURL = wsData.getRequestURL();
			this.requestURI = wsData.getRequestURI();
			this.localPort = wsData.getLocalPort();
		} else {
			this.webSocketSession = null;
			this.params = new HashMap<String, String[]>();
			this.httpSession = request.getSession();
			this.isIFrameHttpRequest = request.getParameterMap().containsKey("isIframe");
			this.isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
			this.methodType = request.getMethod();
			this.remoteHost = request.getRemoteHost();
			this.requestURL = request.getRequestURL();
			this.requestURI = request.getRequestURI();
			this.localPort = request.getLocalPort();
			
		}

		this.response = response;

		__contentIsHtml = Boolean.parseBoolean(getParameter("__contentIsHtml"));

		contentIsHtml = isFirst() || __contentIsHtml;

		String v = getParameter("viewId");
		if (v != null && !v.isEmpty()) {
			viewId = Integer.parseInt(v);
			getViewSession().isNew = false;
		} else {
			Integer lastViewID = (Integer) this.httpSession.getAttribute("LAST_VIEW_ID");

			this.httpSession.setAttribute("LAST_VIEW_ID", viewId = (lastViewID == null ? 1 : ++lastViewID));
		}

		getViewSession().access();

		this.cid = (v = getParameter("cid")) == null ? 1 : Integer.parseInt(v);

		this.conversation = new Conversation(getViewSession(), cid);

		this.userPrincipal = (UserPrincipal) getSession().getAttribute("__USER_PRINCIPAL__");
	}
	
	public boolean isFirst() {
		return !(isAjax || isIFrameHttpRequest || isWebSocket());
	}
	
	public Session getWebSocketSession() {
		return webSocketSession;
	}
	
	public boolean isWebSocket() {
		return webSocketSession != null;
	}
	
	public String getRemoteHost() {
		return this.remoteHost;
	}
	
	public String getRequestURI() {
		return this.requestURI;
	}
	
	public StringBuffer getRequestURL() {
		return this.requestURL;
	}
	
	public int getLocalPort() {
		return this.localPort;
	}
	
	public Principal getUserPrincipal() {
		return this.userPrincipal;
	}

	public void setUserPrincipal(UserPrincipal user) {
		if (this.userPrincipal != null)
			throw new OperationNotAllowedException(LogMessage.getMessage("green-0039"));

		getSession().setAttribute("__USER_PRINCIPAL__", this.userPrincipal = user);
	}

	private static final Pattern pattern = Pattern.compile("up.browser|up.link|windows ce|iphone|iemobile|mini|mmp|symbian|midp|wap|phone|pocket|mobile|pda|psp", Pattern.CASE_INSENSITIVE);

	static boolean isMobile(String userAgent) {
		return pattern.matcher(userAgent).find();
	}

	public boolean isMobile() {
		return isMobile == null ? isMobile = isMobile(this.getHeader("user-agent")) : isMobile;
	}

	public boolean isMethod(RequestMethod methodType) {
		return getMethod().equals(methodType.name());
	}

	public boolean isAjax() {
		return isAjax;
	}

	public int getConversationId() {
		return cid;
	}

	public Extension getExtension() {
		return (extension != null ? extension = new Extension(this.getServletPath()) : extension);
	}

	public ViewSession getViewSession() {
		if (this.viewSession == null) {
			ViewSessionContext viewContext = new ViewSessionContext(getSession());

			if ((this.viewSession = viewContext.getViewSession(viewId)) == null) {
				if (response.isCommitted())
					throw new IllegalStateException("Cannot create a view session after the response has been committed");

				this.viewSession = new ViewSession(viewId, getSession(), viewContext);
			}
		}

		return this.viewSession;
	}

	public HttpSession getSession() {
		return this.httpSession;
	}

	public HttpSession getSession(boolean create) {
		return this.httpSession;
	}

	public String getMethod() {
		return this.methodType;
	}

	public Conversation getConversation() {
		return conversation;
	}

	public final class Extension {
		private final String ext;
		private final boolean isCss, isJS, isView;

		private Extension(String path) {
			ext = FileUtils.getExtension(path);
			isCss = ext.equals("css");
			isJS = ext.equals("js");
			isView = ext.equals("html") || ext.equals("xhtml") || ext.equals("jsp") || ext.equals("htm");
		}

		public String get() {
			return this.ext;
		}

		public boolean isCss() {
			return this.isCss;
		}

		public boolean isJS() {
			return this.isJS;
		}

		public boolean isView() {
			return this.isView;
		}
	}

	public String getParameter(String name) {
		String[] value = params.get(name);
		if (value == null)
			return super.getParameter(name);

		return value[0];
	}

	public String[] getParameterValues(String name) {
		String[] value;
		int pos;
		if (isWebSocket() && (pos = name.indexOf("[]")) != -1) {
			value = params.get(name.substring(0, pos));
		} else {
			value = params.get(name);
		}

		if (value == null)
			return super.getParameterValues(name);

		return value;
	}

	public String getContextPath() {
		return greencode.kernel.$GreenContext.getContextPath();
	}

	public boolean isIFrameHttpRequest() {
		return isIFrameHttpRequest;
	}
}
