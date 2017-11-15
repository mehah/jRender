package com.jrender.exception;

public class OperationNotAllowedException extends RuntimeException {
	private static final long serialVersionUID = -6760478253509665854L;	
	public OperationNotAllowedException() {}	
	public OperationNotAllowedException(String message) { super(message); }
}
