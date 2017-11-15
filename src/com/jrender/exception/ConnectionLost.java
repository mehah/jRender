package com.jrender.exception;

public class ConnectionLost extends RuntimeException {
	private static final long serialVersionUID = -6760478253509665854L;
	public ConnectionLost() {}	
	public ConnectionLost(String message) { super(message); }
}
