package greencode.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Conversation {
	public static final int FIRST = -3, NEXT = -2, LAST = -1, CURRENT = 0;
	
	private final ViewSession viewSession;
	private int id;
	
	private Map<String, Object> map;
	
	Conversation(ViewSession view, int id) {
		this.viewSession = view;
		this.id = id;
		this.map = getMap(view, id);
	}
	
	public int getId() { return this.id; }
	
	public void setAttribute(String key, Object value) { map.put(key, value); }
	
	public Object getAttribute(String key) { return map.get(key); }
	
	public Conversation next() { return new Conversation(viewSession, id+1); }
	
	public ViewSession getViewSession() {
		return this.viewSession;
	}
	
	public void changeConversation(int id) {
		if(this.id == id)
			return;
		
		this.id = id;
		this.map = getMap(viewSession, id);
	}

	private static Map<String, Object> getMap(ViewSession view, int cid) {
		Map<Integer, Map<String, Object>> conversations = getConverstionMap(view);
		
		Map<String, Object> map = conversations.get(cid);
		if(map == null)
			conversations.put(cid, map = new HashMap<String, Object>());
		
		return map;
	}
	
	static Map<Integer, Map<String, Object>> getConverstionMap(ViewSession view) {
		@SuppressWarnings("unchecked")
		Map<Integer, Map<String, Object>> conversations = (Map<Integer, Map<String, Object>>) view.getAttribute("CONVERSATION_LIST");
		if(conversations == null)
			view.setAttribute("CONVERSATION_LIST", conversations = new HashMap<Integer, Map<String, Object>>());
		
		return conversations;
	}
	
	static Conversation getInstance(HttpRequest request, int cid) {
		if(cid == FIRST)
			cid = getConverstionMap(request.getViewSession()).keySet().iterator().next();
		else if(cid == NEXT)
			return request.getConversation().next();
		else if(cid == CURRENT)
			return request.getConversation();
		else if(cid == LAST) {
			final Iterator<Integer> it = getConverstionMap(request.getViewSession()).keySet().iterator();
			while (it.hasNext()) cid = it.next();
		}
		
		return cid == request.getConversationId() ? request.getConversation() : new Conversation(request.getViewSession(), cid);
	}
}
