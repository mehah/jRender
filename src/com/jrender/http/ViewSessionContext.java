package com.jrender.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

public final class ViewSessionContext {
	static final List<ViewSession> globalViewList = new ArrayList<ViewSession>();
	
	final Map<Integer, ViewSession> views;
	
	void addView(Integer id, ViewSession view) {
		views.put(id, view);
		globalViewList.add(view);
	}
	
	@SuppressWarnings("unused")
	private ViewSessionContext() {views = null;}
	
	@SuppressWarnings("unchecked")
	ViewSessionContext(HttpSession session) {	
		Map<Integer, ViewSession> _views = (Map<Integer, ViewSession>) session.getAttribute("VIEW_SESSIONS");
		if(_views == null)
			session.setAttribute("VIEW_SESSIONS", views = new ConcurrentHashMap<Integer, ViewSession>());
		else
			views = _views;
		
		_views = null;
	}
	
	public Set<Integer> getIds() { return views.keySet(); }

	public ViewSession getViewSession(int arg0) { return views.get(arg0); }
	
	void removeViewSession(int arg0) { globalViewList.remove(views.remove(arg0)); }
}
