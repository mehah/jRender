package com.jrender.http;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public final class $HttpRequest {

	public static boolean contentIsHtml(HttpRequest request) {
		return request.contentIsHtml;
	}

	public static Map<String, String[]> getParameters(HttpRequest request) {
		return request.params;
	}

	public static boolean isMobile(HttpServletRequest request) {
		return HttpRequest.isMobile(request);
	}

	public static List<ViewSession> getGlobalViewList() {
		return ViewSessionContext.globalViewList;
	}
}
