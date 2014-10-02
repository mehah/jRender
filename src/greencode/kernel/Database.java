package greencode.kernel;

import greencode.database.DatabaseConfig;
import greencode.database.annotation.Connection;

import java.sql.SQLException;

class Database {
	static void startConnection(GreenContext context, Connection cA) throws SQLException {
		final DatabaseConfig config;
		
		if(!cA.value().isEmpty())
			config = GreenCodeConfig.DataBase.getConfig(cA.value());
		else {
			DatabaseConfig defaultConfig = GreenCodeConfig.DataBase.configs.get(GreenCodeConfig.DataBase.defaultConfigFile);
			config = new DatabaseConfig();			
			config.setDatabase(cA.database().isEmpty() ? defaultConfig.getDatabase() : cA.database());
			config.setPassword(cA.password().isEmpty() ? defaultConfig.getPassword() : cA.password());			
			config.setSchema(cA.schema().isEmpty() ? defaultConfig.getSchema() : cA.schema());
			config.setServerName(cA.serverName().isEmpty() ? defaultConfig.getServerName() : cA.serverName());
			config.setUserName(cA.userName().isEmpty() ? defaultConfig.getUserName() : cA.userName());
		}
		
		context.getDatabaseConnection().setConfig(config).start();
	}
}
