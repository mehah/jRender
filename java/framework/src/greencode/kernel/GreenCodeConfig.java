package greencode.kernel;

import greencode.database.DatabaseConfig;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class GreenCodeConfig {	
	private GreenCodeConfig() {}
	
	static void load() throws IOException
	{		
		System.out.print("Setting Config Parameters ...");

		Document src = null;
		try {
			URL configXml = Core.class.getClassLoader().getResource("greencode.config.xml");
			
			if(configXml != null)
			{
				src = Jsoup.parse(new File(configXml.getPath()), View.getCharset());
				String value;
				
				Element greencodeCofig = src.getElementsByTag("greencode-cofig").get(0);
				
				List<Element> listDataBaseConfigFile = greencodeCofig.getElementsByTag("database-config-file");
				if(!listDataBaseConfigFile.isEmpty())
				{
					value = listDataBaseConfigFile.get(0).text();
					if(!value.isEmpty())
					{
						DataBase.defaultConfigFile = value;
					}
				}
				
				List<Element> listConsole = greencodeCofig.getElementsByTag("console");
				if(!listConsole.isEmpty())
				{					
					boolean writeLog = Boolean.parseBoolean(listConsole.get(0).attr("writeLog").trim());
					
					Console.writeLog = writeLog;
				}
				
				List<Element> listView = greencodeCofig.getElementsByTag("view");
				if(!listView.isEmpty())
				{					
					List<Element> listTemplateFile = listView.get(0).getElementsByTag("template-file");
					
					if(!listTemplateFile.isEmpty())
					{
						value = listTemplateFile.get(0).text();
						
						if(!value.isEmpty())
						{
							View.templateFile = value;
						}
					}
					
					View.charset = listView.get(0).attr("charset");
					View.bootable = Boolean.parseBoolean(listView.get(0).attr("bootable"));
					View.useMinified = Boolean.parseBoolean(listView.get(0).getElementsByTag("use-minified").get(0).text());
					View.seekChange = Boolean.parseBoolean(listView.get(0).attr("seek-change"));
				}
								
				List<Element> listAntiFlood = greencodeCofig.getElementsByTag("anti-flood");
				if(!listAntiFlood.isEmpty())
				{	
					Element defaultMaxRequest = listAntiFlood.get(0).getElementsByTag("default-max-request").get(0);
					
					if(!defaultMaxRequest.text().isEmpty())
					{
						value = defaultMaxRequest.text();
						if(!value.isEmpty())
						{
							AntiFlood.defaultMaxRequest = Short.parseShort(value);
						}
					}
				}
				
				List<Element> _listDatabaseConfig = greencodeCofig.getElementsByTag("database");
				if(!_listDatabaseConfig.isEmpty())
				{					
					Element databaseConfig = _listDatabaseConfig.get(0);
					
					List<Element> listDefaultConfigFile = databaseConfig.getElementsByTag("default-config-file");
					if(!listDefaultConfigFile.isEmpty())
					{
						value = listDefaultConfigFile.get(0).text();
						if(!value.isEmpty())
						{
							DataBase.defaultConfigFile = value;
						}
					}
					
					List<Element> listShowQuery = databaseConfig.getElementsByTag("show-query");
					if(!listShowQuery.isEmpty())
					{
						value = listShowQuery.get(0).text();
						if(!value.isEmpty())
						{
							DataBase.showResultQuery = Boolean.parseBoolean(value);
						}
					}
					
					List<Element> listDriver = databaseConfig.getElementsByTag("drivers");
					if(!listDriver.isEmpty())
					{
						for (Element element : listDriver.get(0).getAllElements()) {
							DataBase.getDrives().put(element.tagName(), element.text());
						}
					}
				}

				Internationalization.newVariantInstance(Internationalization.logsLocale, "pt", "BR", "greencode/message/log_pt-BR.properties", "utf-8");
				
				List<Element> _listInternationalizationConfig = greencodeCofig.getElementsByTag("internationalization");
				if(!_listInternationalizationConfig.isEmpty())
				{
					for (Element element : _listInternationalizationConfig) {
						for (Element _locale : element.getElementsByTag("locale")) {
							Internationalization.newVariantInstance(Internationalization.pagesLocale, _locale.attr("language"), _locale.attr("country"), _locale.attr("file"), _locale.attr("charset"));
						}
					}
				}

			}

		}finally
		{			
			if(src != null)
			{
				src.empty();
				src = null;
			}
		}
				
		System.out.println(" [done]");
	}
	
	public static class Console {
		static boolean writeLog = true;

		public static boolean writeLog() {
			return writeLog;
		}
	}
	
	public static class View {
		static boolean bootable = false;
		static String templateFile;
		static boolean useMinified = false;
		static String charset = "utf-8";
		static boolean seekChange = true;

		public static boolean isBootable() {return bootable;}
		public static String getTemplateFile() {return templateFile;}
		public static boolean usingMinified() {return useMinified;}
		public static String getCharset() {return charset;}
		public static Boolean seekChange() {return seekChange;}
	}

	public static class AntiFlood {			
		static short defaultMaxRequest = 200;
		public static short getDefaultMaxRequest() {return defaultMaxRequest;}
	}
	
	public abstract static class DataBase {
		static String defaultConfigFile;
		static boolean showResultQuery;
		
		static HashMap<String, String> drives = new HashMap<String, String>();
		static HashMap<String, DatabaseConfig> configs = new HashMap<String, DatabaseConfig>();

		public static String getDefaultConfigFile() {return defaultConfigFile;}

		public static HashMap<String, String> getDrives() {return drives;}
		public static HashMap<String, DatabaseConfig> getConfigs() {return configs;}

		public static boolean isShowingResultQuery() {return showResultQuery;}
		
		public static DatabaseConfig getConfig(String path)
		{
			if(configs.containsKey(path))
				return configs.get(path);
			else
			{
				System.out.println("Caching Database Config File: "+path);
				try {
					URL databaseConfigXml = GreenCodeConfig.class.getClassLoader().getResource(path);
					if(databaseConfigXml != null)
					{
						Document src = Jsoup.parse(new File(databaseConfigXml.getPath()), "utf-8");
						
						List<Element> listDatabaseConfig = src.getElementsByTag("database-config");
						if(!listDatabaseConfig.isEmpty())
						{
							String value;
							DatabaseConfig config = new DatabaseConfig();
							
							Element databaseConfig = listDatabaseConfig.get(0);
							
							List<Element> listServerName = databaseConfig.getElementsByTag("server-name");
							if(!listServerName.isEmpty())
							{
								value = listServerName.get(0).text();
								if(!value.isEmpty())
								{
									config.setServerName(value);
								}
							}						
							
							List<Element> listDatabase = databaseConfig.getElementsByTag("database");
							if(!listDatabase.isEmpty())
							{
								value = listDatabase.get(0).text();
								if(!value.isEmpty())
								{
									config.setDatabase(value);
								}
							}
							
							List<Element> listSchema = databaseConfig.getElementsByTag("schema");
							if(!listSchema.isEmpty())
							{
								value = listSchema.get(0).text();
								if(!value.isEmpty())
								{
									config.setSchema(value);
								}
							}
							
							
							List<Element> listUsername = databaseConfig.getElementsByTag("username");
							if(!listUsername.isEmpty())
							{
								value = listUsername.get(0).text();
								if(!value.isEmpty())
								{
									config.setUserName(value);
								}
							}
							
							List<Element> listPassword = databaseConfig.getElementsByTag("password");
							if(!listPassword.isEmpty())
							{
								value = listPassword.get(0).text();
								if(!value.isEmpty())
								{
									config.setPassword(value);
								}
							}
							
							List<Element> listReconnect = databaseConfig.getElementsByTag("reconnect");
							if(!listReconnect.isEmpty())
							{
								Element reconnectTag = listReconnect.get(0);
								
								String chance = reconnectTag.attr("chance");
								if(chance != null && !chance.isEmpty())
								{
									config.setChanceReconnect(Byte.parseByte(chance));
								}
								
								List<Element> listConnection = reconnectTag.getElementsByTag("connection");
								
								if(!listConnection.isEmpty())
								{
									value = listConnection.get(0).text();
									if(!value.isEmpty())
									{
										config.setConnectionFileName(value);
									}										
								}
							}
						
							if(config.getDatabase() != null && !config.getDatabase().isEmpty())
							{
								if(DataBase.getDrives().containsKey(config.getDatabase()))
								{
									String driverName = DataBase.getDrives().get(config.getDatabase());
									
									try {
										Class.forName(driverName);
									} catch (Exception e) {
										try {
											DriverManager.registerDriver((Driver)Class.forName(driverName).newInstance());
										} catch (ClassNotFoundException e1) {
											greencode.kernel.Console.error(LogMessage.getMessage("green-db-0000", config.getDatabase()));
										} catch (Exception e1) {
											greencode.kernel.Console.error(e1);
										}
									}
								}else
								{
									greencode.kernel.Console.log("Driver do Banco de Dados especificado n�o foi declarado no arquivo de configura��o 'greencode.config.xml'.");
								}
							}
							
							getConfigs().put(path, config);
							
							return config;
						}
						
						src.empty();
						src = null;
					}else
						greencode.kernel.Console.error("N�o foi possivel encontrar o arquivo de configura��o do banco de dados chamado '"+path+"'.");
				} catch (Exception e) {
					greencode.kernel.Console.error(e);
				}
				return null;
			}
		}
	}
	
	public abstract static class Internationalization {
		static List<Variant> logsLocale = new ArrayList<Variant>();
		static List<Variant> pagesLocale = new ArrayList<Variant>();

		public static Variant getVariantLogByLocale(Locale locale) {
			for (Variant v : logsLocale) {
				if(v.locale.equals(locale))
				{
					return v;
				}
			}
			
			return null;
		}
		
		public static Variant getVariantPageByLocale(Locale locale) {
			for (Variant v : pagesLocale) {
				if(v.locale.equals(locale))
				{
					return v;
				}
			}
			
			return null;
		}
		
		static class Variant {
			Locale locale;
			URL resource;
			String fileName;
			String charsetName;
			
			public Locale getLocale() { return locale; }
			public URL getResource() { return resource; }
			public String getFileName() { return fileName; }
			public String getCharsetName() { return charsetName; }
		}
		
		static void newVariantInstance(List<Variant> list, String language, String country, String file, String charsetName) {
			Locale locale = new Locale(language, country);
			
			Variant variant = new Variant();
			variant.resource = Thread.currentThread().getContextClassLoader().getResource(file);
			variant.fileName = file;
			variant.locale = locale;
			variant.charsetName = (charsetName == null || charsetName.isEmpty())? "UTF8" : charsetName;
			
			list.add(variant);
		}
	}
}
