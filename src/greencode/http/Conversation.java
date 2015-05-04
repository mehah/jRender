package greencode.http;

import java.util.HashMap;
import java.util.Iterator;

public class Conversation {
	public static final int FIRST = -3, NEXT = -2, LAST = -1, CURRENT = 0;
	
	private final ViewSession viewSession;
	private int id;
	
	private HashMap<String, Object> map;
	
	Conversation(ViewSession view, int id) {
		this.viewSession = view;
		this.id = id;
		this.map = getMap(view, id);
	}
	
	public int getId() { return this.id; }
	
	public void setAttribute(String key, Object value) { map.put(key, value); }
	
	public Object getAttribute(String key) { return map.get(key); }
	
	public Conversation next() { return new Conversation(viewSession, id+1); }
	
	public void changeConversation(int id) {
		if(this.id == id)
			return;
		
		this.id = id;
		this.map = getMap(viewSession, id);
	}

	static HashMap<Integer, HashMap<String, Object>> getConverstionMap(ViewSession view) {
		@SuppressWarnings("unchecked")
		HashMap<Integer, HashMap<String, Object>> conversations = (HashMap<Integer, HashMap<String, Object>>) view.getAttribute("CONVERSATION_LIST");
		if(conversations == null)
			view.setAttribute("CONVERSATION_LIST", conversations = new HashMap<Integer, HashMap<String, Object>>());
		
		return conversations;
	}
	
	private static HashMap<String, Object> getMap(ViewSession view, int cid) {
		HashMap<Integer, HashMap<String, Object>> conversations = getConverstionMap(view);
		
		HashMap<String, Object> map = conversations.get(cid);
		if(map == null)
			conversations.put(cid, map = new HashMap<String, Object>());
		
		return map;
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
		
		if(cid == request.getConversationId())
			return request.getConversation();
		
		return new Conversation(request.getViewSession(), cid);
	}
}
