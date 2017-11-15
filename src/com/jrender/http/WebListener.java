package com.jrender.http;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.jrender.kernel.Core;

@javax.servlet.annotation.WebListener
public final class WebListener implements HttpSessionListener {

	public WebListener() {}
	
	public void sessionCreated(HttpSessionEvent arg0) {}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		HttpSession session = arg0.getSession();
		if (Core.getBoot() != null) {
			Core.getBoot().sessionDestroyed(session);
		}
		
		ViewSessionContext context = new ViewSessionContext(session);
		for (ViewSession view : context.views.values())
			view.invalidate();			
	}

}
