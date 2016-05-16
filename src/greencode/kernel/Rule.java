package greencode.kernel;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletResponse;

import greencode.exception.StopProcess;
import greencode.http.security.UserPrincipal;
import greencode.jscript.window.annotation.RulesAllowed;

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
		boolean haveAccess = false;
		
		if(context.request.getUserPrincipal() != null) {
			for (String rule : rules) {
				if(((UserPrincipal)context.request.getUserPrincipal()).hasRule(rule)) {
					haveAccess = true;
					break;
				}
			}
		}		
	
		return haveAccess;
	}
	
	static void runAuthorizationMethod(GreenContext context) throws IOException {
		if((Cache.bootAction == null || !Cache.bootAction.whenUnauthorized(context)) && !context.request.isWebSocket()) {
			context.response.sendError(HttpServletResponse.SC_UNAUTHORIZED, LogMessage.getMessage("green-0040"));
		}
		throw new StopProcess();
	}
}
