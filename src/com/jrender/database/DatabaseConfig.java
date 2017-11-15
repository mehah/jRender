package com.jrender.database;

public final class DatabaseConfig {
	private String
		serverName, database, userName, password,
		schema, driverName, connectionFileName;
	
	private byte chanceReconnect;
	
	public String getServerName() {return serverName;}
	public String getDatabase() {return database;}
	public String getUserName() {return userName;}
	public String getPassword() {return password;}
	public String getSchema() {return schema;}
	public String getDriverName() {return driverName;}
	public String getConnectionFileName() {return connectionFileName;}
	public byte getChanceReconnect() {return chanceReconnect;}
	
	public void setServerName(String serverName) {this.serverName = serverName;}	
	public void setDatabase(String database) {this.database = database;}	
	public void setUserName(String userName) {this.userName = userName;}	
	public void setPassword(String password) {this.password = password;}	
	public void setSchema(String schema) {this.schema = schema;}	
	public void setDriverName(String driverName) {this.driverName = driverName;}	
	public void setConnectionFileName(String connectionFileName) {this.connectionFileName = connectionFileName;}
	public void setChanceReconnect(byte chanceReconnect) {this.chanceReconnect = chanceReconnect;}
}
