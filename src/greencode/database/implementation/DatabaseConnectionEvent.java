package greencode.database.implementation;

import greencode.database.annotation.Connection;
import greencode.kernel.GreenContext;

public abstract interface DatabaseConnectionEvent {	
	void beforeRequest(GreenContext context, Connection connection);
	void afterRequest(GreenContext context);
	void onError(GreenContext context, Exception e);
	void onSuccess(GreenContext context);
}
