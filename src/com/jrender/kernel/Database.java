package com.jrender.kernel;

import java.sql.SQLException;

import com.jrender.database.DatabaseConfig;
import com.jrender.database.annotation.Connection;

class Database {
	static void startConnection(JRenderContext context, Connection cA) throws SQLException {
		final DatabaseConfig config;
		
		if(!cA.value().isEmpty())
			config = JRenderConfig.Server.DataBase.getConfig(cA.value());
		else {
			DatabaseConfig defaultConfig = JRenderConfig.Server.DataBase.configs.get(JRenderConfig.Server.DataBase.defaultConfigFile);
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
