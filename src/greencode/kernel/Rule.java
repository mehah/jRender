package greencode.kernel;

import java.io.IOException;
import java.lang.reflect.Method;

import greencode.exception.StopProcess;
import greencode.http.security.UserPrincipal;
import greencode.jscript.window.annotation.RulesAllowed;

import javax.servlet.http.HttpServletResponse;

final class Rule {

	static void forClass(GreenContext context, Page page) throws IOException {
		if(page.pageAnnotation.rules().length > 0) 
			process(context, page.pageAnnotation.rules());
	}
	
	static void forMethod(GreenContext context, Method method) throws IOException {
		RulesAllowed rulesAllowed = method.getAnnotation(RulesAllowed.class);
		if(rulesAllowed != null)
			process(context, rulesAllowed.value());
	}
	
	private static void process(GreenContext context, String[] rules) throws IOException {
		boolean hasAccess = false;
		
		if(context.request.getUserPrincipal() != null) {
			for (String rule : rules) {
				if(((UserPrincipal)context.request.getUserPrincipal()).hasRule(rule)) {
					hasAccess = true;
					break;
				}
			}
		}
		
		if(!hasAccess) {
			if(Cache.bootAction == null || !Cache.bootAction.whenUnauthorized(context))
				context.response.sendError(HttpServletResponse.SC_UNAUTHORIZED, LogMessage.getMessage("green-0040"));
			throw new StopProcess();
		}
	}
}
