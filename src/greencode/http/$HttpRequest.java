package greencode.http;

import java.util.HashMap;
import java.util.List;

public final class $HttpRequest {

	public static boolean contentIsHtml(HttpRequest request) {
		return request.contentIsHtml;
	}
	
	public static boolean __contentIsHtml(HttpRequest request) {
		return request.__contentIsHtml;
	}

	public static HashMap<String, String[]> getParameters(HttpRequest request) {
		return request.params;
	}

	public static boolean isMobile(String userAgent) {
		return HttpRequest.isMobile(userAgent);
	}

	public static List<ViewSession> getGlobalViewList() {
		return ViewSessionContext.globalViewList;
	}
}
