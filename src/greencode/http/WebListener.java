package greencode.http;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@javax.servlet.annotation.WebListener
public final class WebListener implements HttpSessionListener {

	public WebListener() {}
	
	public void sessionCreated(HttpSessionEvent arg0) {}

	public void sessionDestroyed(HttpSessionEvent arg0) {		
		ViewSessionContext context = new ViewSessionContext(arg0.getSession());
		for (ViewSession view : context.views.values())
			view.invalidate();			
	}

}
