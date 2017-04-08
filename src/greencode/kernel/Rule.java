package greencode.kernel;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletResponse;

import greencode.http.security.UserPrincipal;
import greencode.jscript.dom.window.annotation.RulesAllowed;
import greencode.util.LogMessage;

final class Rule {

	static boolean forClass(GreenContext context, FileWeb page) throws IOException {
		if(page.pageAnnotation.rules().length > 0) 
			return process(context, page.pageAnnotation.rules());
		
		return true;
	}
	
	static void forMethod(GreenContext context, Method method) throws IOException {
		RulesAllowed rulesAllowed = method.getAnnotation(RulesAllowed.class);
		if(rulesAllowed != null && !process(context, rulesAllowed.value())) {
			runAuthorizationMethod(context);
		}
	}
	
	private static boolean process(GreenContext context, String[] rules) throws IOException {
		boolean hasAccess = false;
		
		if(context.request.getUserPrincipal() != null) {
			for (String rule : rules) {
				if(((UserPrincipal)context.request.getUserPrincipal()).hasRule(rule)) {
					hasAccess = true;
					break;
				}
			}
		}		
	
		return hasAccess;
	}
	
	static void runAuthorizationMethod(GreenContext context) throws IOException {
		if((Cache.bootAction == null || !Cache.bootAction.whenUnauthorized(context)) && !context.request.isWebSocket()) {
			context.response.sendError(HttpServletResponse.SC_UNAUTHORIZED, LogMessage.getMessage("green-0040"));
		}
		
		if(greencode.kernel.$DOMScanner.hasRegisteredCommand(context.getRequest().getViewSession())) {
			context.response.getWriter().println("<script type=\"text/javascript\" src=\"" + Core.SRC_CORE_JS_FOR_SCRIPT_HTML + "\" charset=\""+GreenCodeConfig.Server.View.charset+"\"></script>");
		}
 	}
}
