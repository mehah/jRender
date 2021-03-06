package com.jrender.http;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StandardSessionFacade;

import com.jrender.jscript.dom.$Window;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.WindowHandle;
import com.jrender.kernel.DOMScanner;
import com.jrender.kernel.JRenderConfig;
import com.jrender.util.GenericReflection;

public final class ViewSession implements Serializable {
	private static final long serialVersionUID = 1L;

	private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> currentSheduled;

	private final ViewSessionContext context;

	private final int id;

	private final long creationTime = System.currentTimeMillis();
	private long lastAccessedTime = creationTime, thisAccessedTime = creationTime;

	private int maxInactiveInterval = JRenderConfig.Server.View.Session.maxInactiveInterval;

	boolean isNew = true;
	private final StandardSession session;

	private final ViewSession This = this;
	
	final DOMScanner domScanner = com.jrender.kernel.$DOMScanner.getInstance();
	final Map<Integer, Map<String, Object>> conversations = new HashMap<Integer, Map<String, Object>>();

	private final Runnable task = new Runnable() {
		public void run() {
			if (!This.session.isValid() || This.thisAccessedTime + (maxInactiveInterval * 1000) < System.currentTimeMillis()) {
				// Console.log(LogMessage.getMessage("0026",
				// This.getId()));
				This.invalidate();
			}
		}
	};

	private final Hashtable<String, Object> attributes = new Hashtable<String, Object>();
	private final static Field f = GenericReflection.NoThrow.getDeclaredField(StandardSessionFacade.class, "session");

	ViewSession(int id, HttpSession session, ViewSessionContext context) {
		this.session = (StandardSession) GenericReflection.NoThrow.getValue(f, session);
		this.id = id;
		this.context = context;
		context.addView(id, this);

		setMaxInactiveInterval(maxInactiveInterval);

		// System.out.println("View session("+id+") created");
	}

	public void access() {
		this.lastAccessedTime = this.thisAccessedTime;
		this.thisAccessedTime = System.currentTimeMillis();

		session.access();
	}

	public Object getAttribute(String arg0) {
		return attributes.get(arg0);
	}

	public Enumeration<String> getAttributeNames() {
		return attributes.keys();
	}

	public long getCreationTime() {
		return creationTime;
	}

	public int getId() {
		return id;
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public ViewSessionContext getViewSessionContext() {
		return context;
	}

	public Object getValue(String arg0) {
		return attributes.values();
	}

	public String[] getValueNames() {
		return (String[]) attributes.keySet().toArray();
	}

	public void invalidate() {
		for (Integer key : this.conversations.keySet()) {
			final Conversation conversation = new Conversation(this, key);
			Map<Class<? extends Window>, Window> list = $Window.getMap(conversation);
			for (Class<? extends Window> clazz : list.keySet()) {
				WindowHandle.removeInstance(clazz, conversation);
			}
		}

		attributes.clear();
		context.removeViewSession(id);

		currentSheduled.cancel(true);
		scheduled.shutdownNow();
	}

	public boolean isValid() {
		return context.views.containsKey(id);
	}

	public boolean isNew() {
		return isNew;
	}

	public void putValue(String arg0, Object arg1) {
		attributes.put(arg0, arg1);
	}

	public void removeAttribute(String arg0) {
		attributes.remove(arg0);
	}

	public void removeValue(String arg0) {
		attributes.values().remove(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {
		attributes.put(arg0, arg1);
	}

	public void setMaxInactiveInterval(int arg0) {
		maxInactiveInterval = arg0;

		if (currentSheduled != null)
			currentSheduled.cancel(true);

		currentSheduled = scheduled.scheduleWithFixedDelay(task, maxInactiveInterval, maxInactiveInterval, TimeUnit.SECONDS);
	}
}
