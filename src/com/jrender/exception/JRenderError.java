package com.jrender.exception;

public class JRenderError extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public JRenderError(String message) { super(message); }
	public JRenderError(Throwable arg0) { super(arg0); }
}
