package greencode.http;

import java.util.HashMap;

public class Conversation {
	public static final int FIRST = -1, CURRENT = 0;
	
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

	private static HashMap<Integer, HashMap<String, Object>> getConverstionMap(ViewSession view) {
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
	
	static Conversation getInstance(HttpRequest request, int cid)
	{		
		if(cid == FIRST)
			return new Conversation(request.getViewSession(), getConverstionMap(request.getViewSession()).keySet().iterator().next());
		else if(cid != CURRENT && request.getConversationId() != cid)
			return new Conversation(request.getViewSession(), cid);
		
		return request.getConversation();
	}
}
