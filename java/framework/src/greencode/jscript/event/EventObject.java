package greencode.jscript.event;

import greencode.jscript.Element;

public class EventObject {
	private Element target;
	private Element relatedTarget;
	private int pageX;
	private int pageY;
	private int which;
	private boolean metaKey;
	private String type;
	private long timeStamp;
	
	private boolean preventDefault;
	private boolean stopImmediatePropagation;
	private boolean stopPropagation;
	
	public Element getTarget() {
		return target;
	}
	
	public Element getRelatedTarget() {
		return relatedTarget;
	}
	
	public int getPageX() {
		return pageX;
	}
	
	public int getPageY() {
		return pageY;
	}
	
	public int getWhich() {
		return which;
	}
	
	public boolean getMetaKey() {
		return metaKey;
	}

	public String getType() {
		return type;
	}

	/**
	 * Só funciona se a requisição estiver em modo sicronizado.
	 */
	public void preventDefault()
	{
		preventDefault = true;
	}
	
	/**
	 * Só funciona se a requisição estiver em modo sicronizado.
	 */
	public void stopImmediatePropagation()
	{
		stopImmediatePropagation = true;
	}
	
	/**
	 * Só funciona se a requisição estiver em modo sicronizado.
	 */
	public void stopPropagation()
	{
		stopPropagation = true;
	}

	public boolean isDefaultPrevented() {
		return preventDefault;
	}
	public boolean isImmediatePropagationStopped() {
		return stopImmediatePropagation;
	}

	public boolean isPropagationStopped() {
		return stopPropagation;
	}

	public long getTimeStamp() {
		return timeStamp;
	}
}
