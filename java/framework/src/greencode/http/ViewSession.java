package greencode.http;

import greencode.kernel.Console;
import greencode.kernel.LogMessage;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ViewSession {
	private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
	private ViewSessionContext context;
	
	int id;
	
	long lastAccessedTime = 0;
	private int maxInactiveInterval = 1800000; // 60*30*1000 = 1800 (1800000ms) (30min) 
	boolean isNew = true;
	private final long creationTime = new Date().getTime();
	
	private Hashtable<String, Object> attributes = new Hashtable<String, Object>();
	
	ViewSession() {
		final ViewSession viewSession = this;
		
		scheduled.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if(viewSession.getLastAccessedTime()+maxInactiveInterval < new Date().getTime())
				{
					Console.log(LogMessage.getMessage("green-0026", viewSession.getId()));
					viewSession.invalidate();
					scheduled.shutdown();
				}
			}
		}, maxInactiveInterval, maxInactiveInterval, TimeUnit.MILLISECONDS);
	}
	
	void setContext(ViewSessionContext context)
	{
		this.context = context;
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
	
	public ViewSessionContext getViewSessionContext()
	{
		return context;
	}

	public Object getValue(String arg0) {
		return attributes.values();
	}

	public String[] getValueNames() {
		return (String[]) attributes.keySet().toArray();
	}

	public void invalidate() {
		attributes.clear();
		context.removeViewSession(id);
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
	}
}
