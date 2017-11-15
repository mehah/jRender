package com.jrender.database.implementation;

import com.jrender.database.annotation.Connection;
import com.jrender.kernel.JRenderContext;

public abstract interface DatabaseConnectionEvent {	
	void beforeRequest(JRenderContext context, Connection connection);
	void afterRequest(JRenderContext context);
	void onError(JRenderContext context, Exception e);
	void onSuccess(JRenderContext context);
}
