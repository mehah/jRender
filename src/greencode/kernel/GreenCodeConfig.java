package greencode.kernel;

import greencode.database.DatabaseConfig;
import greencode.util.GenericReflection;

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
	
	private final static String DEFAULT_CHARSET = "UTF-8";
	
	static void load() throws IOException {		
		System.out.print("["+Core.projectName+"] Setting Config Parameters ...");

		Document src = null;
		try {
			URL configXml = Core.class.getClassLoader().getResource("greencode.config.xml");
			
			if(configXml != null)
			{
				Element greencodeCofig = (src = Jsoup.parse(new File(configXml.getPath()), View.charset)).getElementsByTag("greencode-cofig").get(0);
				
				String value;
				List<Element> listDataBaseConfigFile = greencodeCofig.getElementsByTag("database-config-file");
				if(!listDataBaseConfigFile.isEmpty() && !(value = listDataBaseConfigFile.get(0).text()).isEmpty())
					GenericReflection.NoThrow.setFinalStaticValue(DataBase.class, "defaultConfigFile", value);
				
				List<Element> listConsole = greencodeCofig.getElementsByTag("console");
				if(!listConsole.isEmpty())
					GenericReflection.NoThrow.setFinalStaticValue(Console.class, "writeLog", Boolean.parseBoolean(listConsole.get(0).attr("writeLog").trim()));
				
				List<Element> listMultipart = greencodeCofig.getElementsByTag("multipart");
				if(!listMultipart.isEmpty()) {
					GenericReflection.NoThrow.setFinalStaticValue(Multipart.class, "autodectetion", Boolean.parseBoolean(listMultipart.get(0).attr("autodectetion").trim()));
					GenericReflection.NoThrow.setFinalStaticValue(Multipart.class, "maxRequestSize", Integer.parseInt(listMultipart.get(0).attr("max-request-size").trim()));
				}
				
				List<Element> listResponse = greencodeCofig.getElementsByTag("response");
				if(!listResponse.isEmpty())
					GenericReflection.NoThrow.setFinalStaticValue(Response.class, "gzipSupport", Boolean.parseBoolean(listResponse.get(0).attr("gzip").trim()));
				
				List<Element> listView = greencodeCofig.getElementsByTag("view");
				if(!listView.isEmpty()) {					
					List<Element> listTemplateFile = listView.get(0).getElementsByTag("template-file");
					
					if(!listTemplateFile.isEmpty() && !(value = listTemplateFile.get(0).text()).isEmpty())
						GenericReflection.NoThrow.setFinalStaticValue(View.class, "templateFile", value);
					
					GenericReflection.NoThrow.setFinalStaticValue(View.class, "charset", listView.get(0).attr("charset"));
					GenericReflection.NoThrow.setFinalStaticValue(View.class, "bootable", Boolean.parseBoolean(listView.get(0).attr("bootable")));
					GenericReflection.NoThrow.setFinalStaticValue(View.class, "useMinified", Boolean.parseBoolean(listView.get(0).getElementsByTag("use-minified").get(0).text()));
					GenericReflection.NoThrow.setFinalStaticValue(View.class, "seekChange", Boolean.parseBoolean(listView.get(0).attr("seek-change")));
				}
								
				List<Element> listAntiFlood = greencodeCofig.getElementsByTag("anti-flood");
				if(!listAntiFlood.isEmpty())
				{	
					Element defaultMaxRequest = listAntiFlood.get(0).getElementsByTag("default-max-request").get(0);
					
					if(!(value = defaultMaxRequest.text()).isEmpty())
						GenericReflection.NoThrow.setFinalStaticValue(AntiFlood.class, "defaultMaxRequest", Short.parseShort(value));
				}
				
				List<Element> _listDatabaseConfig = greencodeCofig.getElementsByTag("database");
				if(!_listDatabaseConfig.isEmpty())
				{					
					Element databaseConfig = _listDatabaseConfig.get(0);
					
					List<Element> listDefaultConfigFile = databaseConfig.getElementsByTag("default-config-file");
					if(!listDefaultConfigFile.isEmpty() && !(value = listDefaultConfigFile.get(0).text()).isEmpty())
						GenericReflection.NoThrow.setFinalStaticValue(DataBase.class, "defaultConfigFile", value);
					
					List<Element> listShowQuery = databaseConfig.getElementsByTag("show-query");
					if(!listShowQuery.isEmpty() && !(value = listShowQuery.get(0).text()).isEmpty())
						GenericReflection.NoThrow.setFinalStaticValue(DataBase.class, "showResultQuery", Boolean.parseBoolean(value));
					
					List<Element> listDriver = databaseConfig.getElementsByTag("drivers");
					if(!listDriver.isEmpty())
					{
						for (Element element : listDriver.get(0).getAllElements())
							DataBase.drives.put(element.tagName(), element.text());
						
						GenericReflection.NoThrow.setFinalStaticValue(DataBase.class, "drives", DataBase.drives/*Collections.unmodifiableMap(DataBase.drives)*/);
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

		}finally {			
			if(src != null) {
				src.empty();
				src = null;
			}
		}
				
		System.out.println(" [done]");
	}
	
	public final static class Console {
		public final static Boolean writeLog = true;
	}
	
	public final static class Multipart {
		public final static Boolean autodectetion = false;
		public final static Integer maxRequestSize = -1;
	}
	
	public final static class Response {
		public final static Boolean gzipSupport = false;
	}
	
	public final static class View {
		public final static Boolean bootable = false;
		public final static String templateFile = null;
		public final static Boolean useMinified = false;
		public final static String charset = DEFAULT_CHARSET;
		public final static Boolean seekChange = true;
	}

	public final static class AntiFlood {			
		public final static Short defaultMaxRequest = 200;
	}
	
	public final static class DataBase {
		public final static Boolean showResultQuery = true;
		
		public final static String defaultConfigFile = null;
		
		public final static HashMap<String, String> drives = new HashMap<String, String>();
		final static HashMap<String, DatabaseConfig> configs = new HashMap<String, DatabaseConfig>();
		
		public static DatabaseConfig getConfig(String path) {
			if(configs.containsKey(path))
				return configs.get(path);
			else
			{
				System.out.println(Core.defaultLogMsg+"Caching Database Config File: "+path);
				try {
					final URL databaseConfigXml = GreenCodeConfig.class.getClassLoader().getResource(path);
					if(databaseConfigXml != null)
					{
						Document src = Jsoup.parse(new File(databaseConfigXml.getPath()), "utf-8");
						
						List<Element> listDatabaseConfig = src.getElementsByTag("database-config");
						if(!listDatabaseConfig.isEmpty()) {
							String value;
							DatabaseConfig config = new DatabaseConfig();
							
							Element databaseConfig = listDatabaseConfig.get(0);
							
							List<Element> listServerName = databaseConfig.getElementsByTag("server-name");
							if(!listServerName.isEmpty())
							{
								value = listServerName.get(0).text();
								if(!value.isEmpty())
									config.setServerName(value);
							}						
							
							List<Element> listDatabase = databaseConfig.getElementsByTag("database");
							if(!listDatabase.isEmpty())
							{
								value = listDatabase.get(0).text();
								if(!value.isEmpty())
									config.setDatabase(value);
							}
							
							List<Element> listSchema = databaseConfig.getElementsByTag("schema");
							if(!listSchema.isEmpty())
							{
								value = listSchema.get(0).text();
								if(!value.isEmpty())
									config.setSchema(value);
							}
							
							
							List<Element> listUsername = databaseConfig.getElementsByTag("username");
							if(!listUsername.isEmpty())
							{
								value = listUsername.get(0).text();
								if(!value.isEmpty())
									config.setUserName(value);
							}
							
							List<Element> listPassword = databaseConfig.getElementsByTag("password");
							if(!listPassword.isEmpty())
							{
								value = listPassword.get(0).text();
								if(!value.isEmpty())
									config.setPassword(value);
							}
							
							List<Element> listReconnect = databaseConfig.getElementsByTag("reconnect");
							if(!listReconnect.isEmpty())
							{
								Element reconnectTag = listReconnect.get(0);
								
								String chance = reconnectTag.attr("chance");
								if(chance != null && !chance.isEmpty())
									config.setChanceReconnect(Byte.parseByte(chance));
								
								List<Element> listConnection = reconnectTag.getElementsByTag("connection");
								
								if(!listConnection.isEmpty())
								{
									value = listConnection.get(0).text();
									if(!value.isEmpty())
										config.setConnectionFileName(value);					
								}
							}
						
							if(config.getDatabase() != null && !config.getDatabase().isEmpty())
							{
								String driverName = DataBase.drives.get(config.getDatabase());
								if(driverName != null) {									
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
									greencode.kernel.Console.log("Driver do Banco de Dados especificado n�o foi declarado no arquivo de configuração 'greencode.config.xml'.");
							}
							
							configs.put(path, config);
							
							return config;
						}
						
						src.empty();
						src = null;
					}else
						greencode.kernel.Console.error("Não foi possivel encontrar o arquivo de configuração do banco de dados chamado '"+path+"'.");
				} catch (Exception e) {
					greencode.kernel.Console.error(e);
				}
				
				return null;
			}
		}
	}
	
	public final static class Internationalization {
		static final List<Variant> pagesLocale = new ArrayList<Variant>();
		private static final List<Variant> logsLocale = new ArrayList<Variant>();

		public static Variant getVariantLogByLocale(Locale locale) {
			for (Variant v : logsLocale) if(v.locale.equals(locale))
				return v;
			
			return null;
		}
		
		public static Variant getVariantPageByLocale(Locale locale) {
			for (Variant v : pagesLocale) if(v.locale.equals(locale))
					return v;
			
			return null;
		}
		
		static class Variant {
			public final Locale locale;
			public final URL resource;
			public final String fileName;
			public final String charsetName;
			
			public Variant(Locale locale, URL resource, String fileName, String charsetName) {
				this.locale = locale;
				this.resource = resource;
				this.fileName = fileName;
				this.charsetName = charsetName;
			}
		}
		
		static void newVariantInstance(List<Variant> list, String language, String country, String file, String charsetName) {
			list.add(
				new Variant(
					new Locale(language, country),
					Thread.currentThread().getContextClassLoader().getResource(file),
					file,
					(charsetName == null || charsetName.isEmpty())? "UTF8" : charsetName
				)
			);
		}
	}
}
