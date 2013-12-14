package greencode.database.implementation;

import greencode.database.annotation.Connection;

public abstract interface DatabaseConnectionEvent {	
	void beforeRequest(Connection connection);
	void afterRequest();
	void onError(Exception e);
	void onSuccess();
}
