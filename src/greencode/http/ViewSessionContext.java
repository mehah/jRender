package greencode.http;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

public final class ViewSessionContext {
	static final List<ViewSession> globalViewList = new ArrayList<ViewSession>();
	
	final ConcurrentHashMap<Integer, ViewSession> views;
	
	void addView(Integer id, ViewSession view) {
		views.put(id, view);
		globalViewList.add(view);
	}
	
	@SuppressWarnings("unused")
	private ViewSessionContext() {views = null;}
	
	@SuppressWarnings("unchecked")
	ViewSessionContext(HttpSession session) {	
		ConcurrentHashMap<Integer, ViewSession> _views = (ConcurrentHashMap<Integer, ViewSession>) session.getAttribute("VIEW_SESSIONS");
		if(_views == null)
			session.setAttribute("VIEW_SESSIONS", views = new ConcurrentHashMap<Integer, ViewSession>());
		else
			views = _views;
		
		_views = null;
	}
	
	public Enumeration<Integer> getIds() { return views.keys(); }

	public ViewSession getViewSession(int arg0) { return views.get(arg0); }
	
	void removeViewSession(int arg0) { globalViewList.remove(views.remove(arg0)); }
}
