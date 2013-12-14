package greencode.database;

public final class DatabaseConfig {
	private String serverName;
	private String database;
	private String userName;
	private String password;	
	private String schema;
	
	private String driverName;
	
	public String getServerName() {return serverName;}
	public void setServerName(String serverName) {this.serverName = serverName;}
	public String getDatabase() {return database;}
	public void setDatabase(String database) {this.database = database;}
	public String getUserName() {return userName;}
	public void setUserName(String userName) {this.userName = userName;}
	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}
	public String getSchema() {return schema;}
	public void setSchema(String schema) {this.schema = schema;}
	public String getDriverName() {return driverName;}
	public void setDriverName(String driverName) {this.driverName = driverName;}
	
	private byte chanceReconnect;	
	private String connectionFileName;
	
	public byte getChanceReconnect() {return chanceReconnect;}
	public void setChanceReconnect(byte chanceReconnect) {this.chanceReconnect = chanceReconnect;}
	public String getConnectionFileName() {return connectionFileName;}
	public void setConnectionFileName(String connectionFileName) {this.connectionFileName = connectionFileName;}
}
