package greencode.kernel;

import greencode.database.DatabaseConfig;
import greencode.kernel.implementation.PluginImplementation;
import greencode.util.GenericReflection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.ConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class GreenCodeConfig {	
	private GreenCodeConfig() {}
	
	private final static String DEFAULT_CHARSET = "UTF-8";
	
	static void load() throws IOException, ClassNotFoundException {		
		System.out.print("["+Core.projectName+"] Setting Config Parameters ...");

		Document src = null;
		try {
			InputStream configXml = Core.class.getClassLoader().getResourceAsStream("greencode.config.xml");
			
			if(configXml == null)
				throw new IOException("Could not find file: src/greencode.config.xml");
			
			Element greencodeCofig = (src = Jsoup.parse(configXml, View.charset, "")).getElementsByTag("greencode-config").get(0);
			
			String value;
			List<Element> listCurrentElement = greencodeCofig.getElementsByTag("database-config-file");
			if(!listCurrentElement.isEmpty() && !(value = listCurrentElement.get(0).text()).isEmpty())
				GenericReflection.NoThrow.setFinalStaticValue(DataBase.class, "defaultConfigFile", value);
			
			listCurrentElement = greencodeCofig.getElementsByTag("console");
			if(!listCurrentElement.isEmpty())
				GenericReflection.NoThrow.setFinalStaticValue(Console.class, "writeLog", Boolean.parseBoolean(listCurrentElement.get(0).attr("writeLog").trim()));
			
			listCurrentElement = greencodeCofig.getElementsByTag("browser");
			if(!listCurrentElement.isEmpty())
				GenericReflection.NoThrow.setFinalStaticValue(Browser.class, "consoleDebug", Boolean.parseBoolean(listCurrentElement.get(0).attr("consoleDebug").trim()));
			
			listCurrentElement = greencodeCofig.getElementsByTag("multipart");
			if(!listCurrentElement.isEmpty()) {
				GenericReflection.NoThrow.setFinalStaticValue(Multipart.class, "autodectetion", Boolean.parseBoolean(listCurrentElement.get(0).attr("autodectetion").trim()));
				GenericReflection.NoThrow.setFinalStaticValue(Multipart.class, "maxRequestSize", Integer.parseInt(listCurrentElement.get(0).attr("max-request-size").trim()));
			}
			
			listCurrentElement = greencodeCofig.getElementsByTag("response");
			if(!listCurrentElement.isEmpty())
				GenericReflection.NoThrow.setFinalStaticValue(Response.class, "gzipSupport", Boolean.parseBoolean(listCurrentElement.get(0).attr("gzip").trim()));
			
			listCurrentElement = greencodeCofig.getElementsByTag("view");
			if(!listCurrentElement.isEmpty()) {
				Element currentElement = listCurrentElement.get(0);
				GenericReflection.NoThrow.setFinalStaticValue(View.class, "charset", currentElement.attr("charset"));
				GenericReflection.NoThrow.setFinalStaticValue(View.class, "bootable", Boolean.parseBoolean(currentElement.attr("bootable")));
				GenericReflection.NoThrow.setFinalStaticValue(View.class, "useMinified", Boolean.parseBoolean(currentElement.attr("use-minified")));
				GenericReflection.NoThrow.setFinalStaticValue(View.class, "seekChange", Boolean.parseBoolean(currentElement.attr("seek-change")));
				
				listCurrentElement = currentElement.getElementsByTag("templates");
				if(!listCurrentElement.isEmpty()) {
					currentElement = listCurrentElement.get(0);
					listCurrentElement = currentElement.getElementsByTag("file");
					if(!listCurrentElement.isEmpty()) {
						Map<String, String> list = new HashMap<String, String>();
						
						for (Element element : listCurrentElement) {
							if(View.defaultTemplatePath == null && element.hasAttr("default"))
								GenericReflection.NoThrow.setFinalStaticValue(View.class, "defaultTemplatePath", element.attr("path"));
							list.put(element.attr("name"), element.attr("path"));
						}
						GenericReflection.NoThrow.setFinalStaticValue(View.class, "templatePaths", Collections.unmodifiableMap(list));
					}
				}
					
			}
			
			listCurrentElement = greencodeCofig.getElementsByTag("viewSession");
			if(!listCurrentElement.isEmpty())
				GenericReflection.NoThrow.setFinalStaticValue(ViewSession.class, "maxInactiveInterval", Integer.parseInt(listCurrentElement.get(0).attr("maxInactiveInterval")));
							
			listCurrentElement = greencodeCofig.getElementsByTag("anti-flood");
			if(!listCurrentElement.isEmpty()) {	
				Element defaultMaxRequest = listCurrentElement.get(0).getElementsByTag("default-max-request").get(0);
				
				if(!(value = defaultMaxRequest.text()).isEmpty())
					GenericReflection.NoThrow.setFinalStaticValue(AntiFlood.class, "defaultMaxRequest", Short.parseShort(value));
			}
			
			listCurrentElement = greencodeCofig.getElementsByTag("database");
			if(!listCurrentElement.isEmpty()) {					
				Element databaseConfig = listCurrentElement.get(0);
				
				listCurrentElement = databaseConfig.getElementsByTag("default-config-file");
				if(!listCurrentElement.isEmpty() && !(value = listCurrentElement.get(0).text()).isEmpty())
					GenericReflection.NoThrow.setFinalStaticValue(DataBase.class, "defaultConfigFile", value);
				
				listCurrentElement = databaseConfig.getElementsByTag("show-query");
				if(!listCurrentElement.isEmpty() && !(value = listCurrentElement.get(0).text()).isEmpty())
					GenericReflection.NoThrow.setFinalStaticValue(DataBase.class, "showResultQuery", Boolean.parseBoolean(value));
				
				listCurrentElement = databaseConfig.getElementsByTag("drivers");
				if(!listCurrentElement.isEmpty()) {
					for (Element element : listCurrentElement.get(0).getAllElements())
						DataBase.drives.put(element.tagName(), element.text());
					
					GenericReflection.NoThrow.setFinalStaticValue(DataBase.class, "drives", DataBase.drives/*Collections.unmodifiableMap(DataBase.drives)*/);
				}
			}

			Internationalization.newVariantInstance(Internationalization.logsLocale, "pt", "BR", "greencode/message/log_pt-BR.properties", "utf-8");
			
			listCurrentElement = greencodeCofig.getElementsByTag("internationalization");
			if(!listCurrentElement.isEmpty()) {
				for (Element element : listCurrentElement) {
					for (Element _locale : element.getElementsByTag("locale"))
						Internationalization.newVariantInstance(Internationalization.pagesLocale, _locale.attr("language"), _locale.attr("country"), _locale.attr("file"), _locale.attr("charset"));
				}
			}
			
			listCurrentElement = greencodeCofig.getElementsByTag("plugins");
			if(!listCurrentElement.isEmpty()) {
				List<Class<?>> list = new ArrayList<Class<?>>();
				for (Element element : listCurrentElement) {
					for (Element e : element.getElementsByTag("plugin"))
						list.add(Class.forName(e.attr("class")));
				}
				
				GenericReflection.NoThrow.setFinalStaticValue(Plugins.class, "list", list.toArray(new Class<?>[list.size()]));
			}
			
			listCurrentElement = null;
		} finally {			
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
	
	public final static class Browser {
		public final static Boolean consoleDebug = false;
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
		public final static String defaultTemplatePath = null;
		public final static Map<String, String> templatePaths = null;
		public final static Boolean useMinified = false;
		public final static String charset = DEFAULT_CHARSET;
		public final static Boolean seekChange = true;
	}
	
	public final static class ViewSession {
		public final static Integer maxInactiveInterval = 1800;
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
			else {
				System.out.println(Core.defaultLogMsg+"Caching Database Config File: "+path);
				
				Document src = null;
				try {
					final URL databaseConfigXml = GreenCodeConfig.class.getClassLoader().getResource(path);
					if(databaseConfigXml == null)
						throw new IOException("Could not find the configuration file database called '"+path+"'.");
					
					src = Jsoup.parse(new File(databaseConfigXml.getPath()), GreenCodeConfig.DEFAULT_CHARSET);
					
					List<Element> listCurrentElement = src.getElementsByTag("database-config");
					if(!listCurrentElement.isEmpty()) {
						String value;
						DatabaseConfig config = new DatabaseConfig();
						
						Element databaseConfig = listCurrentElement.get(0);
						
						listCurrentElement = databaseConfig.getElementsByTag("server-name");
						if(!listCurrentElement.isEmpty()) {
							value = listCurrentElement.get(0).text();
							if(!value.isEmpty())
								config.setServerName(value);
						}						
						
						listCurrentElement = databaseConfig.getElementsByTag("database");
						if(!listCurrentElement.isEmpty()) {
							value = listCurrentElement.get(0).text();
							if(!value.isEmpty())
								config.setDatabase(value);
						}
						
						listCurrentElement = databaseConfig.getElementsByTag("schema");
						if(!listCurrentElement.isEmpty()) {
							value = listCurrentElement.get(0).text();
							if(!value.isEmpty())
								config.setSchema(value);
						}
						
						
						listCurrentElement = databaseConfig.getElementsByTag("username");
						if(!listCurrentElement.isEmpty()) {
							value = listCurrentElement.get(0).text();
							if(!value.isEmpty())
								config.setUserName(value);
						}
						
						listCurrentElement = databaseConfig.getElementsByTag("password");
						if(!listCurrentElement.isEmpty()) {
							value = listCurrentElement.get(0).text();
							if(!value.isEmpty())
								config.setPassword(value);
						}
						
						listCurrentElement = databaseConfig.getElementsByTag("reconnect");
						if(!listCurrentElement.isEmpty()) {
							Element reconnectTag = listCurrentElement.get(0);
							
							String chance = reconnectTag.attr("chance");
							if(chance != null && !chance.isEmpty())
								config.setChanceReconnect(Byte.parseByte(chance));
							
							listCurrentElement = reconnectTag.getElementsByTag("connection");
							
							if(!listCurrentElement.isEmpty()) {
								value = listCurrentElement.get(0).text();
								if(!value.isEmpty())
									config.setConnectionFileName(value);					
							}
						}
						
						listCurrentElement = null;
					
						if(config.getDatabase() != null && !config.getDatabase().isEmpty()) {
							String driverName = DataBase.drives.get(config.getDatabase());
							if(driverName == null)
								throw new ConfigurationException("Driver do Banco de Dados especificado n�o foi declarado no arquivo de configuração 'greencode.config.xml'.");
															
							try {
								Class.forName(driverName);
							} catch (Exception e) {
								try {
									DriverManager.registerDriver((Driver)Class.forName(driverName).newInstance());
								} catch (ClassNotFoundException e1) {
									throw new ClassNotFoundException(LogMessage.getMessage("green-db-0000", config.getDatabase()));
								} catch (Exception e2) {
									throw new RuntimeException(e2);
								}
							}
						}
						
						configs.put(path, config);						
						return config;
					}
				} catch (Exception e) {
					greencode.kernel.Console.error(e);
				} finally {
					if(src != null) {
						src.empty();
						src = null;
					}
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
					(charsetName == null || charsetName.isEmpty()) ? "UTF8" : charsetName
				)
			);
		}
	}
	
	public final static class Plugins {
		static final Class<PluginImplementation>[] list = null;
	}
}
