package greencode.http;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpSession;

public final class ViewSessionContext {
	final Hashtable<Integer, ViewSession> views;
	
	@SuppressWarnings("unused")
	private ViewSessionContext() {
		views = null;
	}
	
	@SuppressWarnings("unchecked")
	ViewSessionContext(HttpSession session)
	{	
		Hashtable<Integer, ViewSession> _views = (Hashtable<Integer, ViewSession>) session.getAttribute("VIEW_SESSIONS");
		if(_views == null)
		{
			views = new Hashtable<Integer, ViewSession>();
			session.setAttribute("VIEW_SESSIONS", views);
		}else
			views = _views;
		
		_views = null;
	}
	
	public Enumeration<Integer> getIds() {
		return views.keys();
	}

	public ViewSession getViewSession(int arg0) {
		return views.get(arg0);
	}
	
	void removeViewSession(int arg0)
	{
		views.remove(arg0);
	}

}
