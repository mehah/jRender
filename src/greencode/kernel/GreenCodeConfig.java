package greencode.kernel;

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
import org.jsoup.select.Elements;

import greencode.database.DatabaseConfig;
import greencode.exception.GreencodeError;
import greencode.kernel.implementation.PluginImplementation;
import greencode.util.GenericReflection;

public final class GreenCodeConfig {
	private GreenCodeConfig() {
	}

	private final static String DEFAULT_CHARSET = "UTF-8";

	static void load() throws IOException, ClassNotFoundException {
		System.out.print("[" + Core.projectName + "] Setting Config Parameters ...");
		
		InputStream configXml = Core.class.getClassLoader().getResourceAsStream("greencode.config.xml");

		if(configXml == null)
			throw new IOException("Could not find file: src/greencode.config.xml");

		Element greencodeCofig = Jsoup.parse(configXml, Server.View.charset, "").getElementsByTag("greencode-config").first();

		String value;

		Element browser = greencodeCofig.getElementsByTag("browser").first();
		GenericReflection.NoThrow.setFinalStaticValue(Browser.class, "consoleDebug", Boolean.parseBoolean(browser.attr("consoleDebug").trim()));
		GenericReflection.NoThrow.setFinalStaticValue(Browser.class, "websocketSingleton", Boolean.parseBoolean(browser.attr("websocket-singleton").trim()));

		Element server = greencodeCofig.getElementsByTag("server").first();
		{
			GenericReflection.NoThrow.setFinalStaticValue(Server.class, "writeLog", Boolean.parseBoolean(server.attr("writeLog").trim()));

			Element currentElement;
			Elements listCurrentElement;

			currentElement = server.getElementsByTag("request").first();
			{
				Element subCurrentElement = currentElement.getElementsByTag("multipart").first();
				GenericReflection.NoThrow.setFinalStaticValue(Server.Request.Multipart.class, "autodectetion", Boolean.parseBoolean(subCurrentElement.attr("autodectetion").trim()));
				GenericReflection.NoThrow.setFinalStaticValue(Server.Request.Multipart.class, "maxRequestSize", Integer.parseInt(subCurrentElement.attr("max-request-size").trim()));
				
				subCurrentElement = currentElement.getElementsByTag("event").first();
				GenericReflection.NoThrow.setFinalStaticValue(Server.Request.Event.class, "requestType", subCurrentElement.attr("requestType").trim().toLowerCase());
				GenericReflection.NoThrow.setFinalStaticValue(Server.Request.Event.class, "methodType", subCurrentElement.attr("methodType").trim());
			}

			currentElement = server.getElementsByTag("view").first();
			{
				GenericReflection.NoThrow.setFinalStaticValue(Server.View.class, "charset", currentElement.attr("charset"));
				GenericReflection.NoThrow.setFinalStaticValue(Server.View.class, "bootable", Boolean.parseBoolean(currentElement.attr("bootable")));
				GenericReflection.NoThrow.setFinalStaticValue(Server.View.class, "useMinified", Boolean.parseBoolean(currentElement.attr("use-minified")));
				GenericReflection.NoThrow.setFinalStaticValue(Server.View.class, "seekChange", Boolean.parseBoolean(currentElement.attr("seek-change")));

				Element subCurrentElement = currentElement.getElementsByTag("templates").first();
				if(subCurrentElement != null) {
					listCurrentElement = subCurrentElement.getElementsByTag("file");
					if(!listCurrentElement.isEmpty()) {
						Map<String, String> list = new HashMap<String, String>();

						for(Element element: listCurrentElement) {
							if(Server.View.defaultTemplatePath == null && element.hasAttr("default"))
								GenericReflection.NoThrow.setFinalStaticValue(Server.View.class, "defaultTemplatePath", element.attr("path"));
							list.put(element.attr("name"), element.attr("path"));
						}
						GenericReflection.NoThrow.setFinalStaticValue(Server.View.class, "templatePaths", Collections.unmodifiableMap(list));
					}					
				}

				subCurrentElement = currentElement.getElementsByTag("session").first();
				{
					GenericReflection.NoThrow.setFinalStaticValue(Server.View.Session.class, "maxInactiveInterval", Integer.parseInt(subCurrentElement.attr("maxInactiveInterval")));
				}
			}

			currentElement = server.getElementsByTag("database").first();
			if(currentElement != null) {
				listCurrentElement = currentElement.getElementsByTag("default-config-file");
				if(!listCurrentElement.isEmpty() && !(value = listCurrentElement.first().text()).isEmpty())
					GenericReflection.NoThrow.setFinalStaticValue(Server.DataBase.class, "defaultConfigFile", value);

				listCurrentElement = currentElement.getElementsByTag("show-query");
				if(!listCurrentElement.isEmpty() && !(value = listCurrentElement.first().text()).isEmpty())
					GenericReflection.NoThrow.setFinalStaticValue(Server.DataBase.class, "showResultQuery", Boolean.parseBoolean(value));

				listCurrentElement = currentElement.getElementsByTag("drivers");
				if(!listCurrentElement.isEmpty()) {
					for(Element element: listCurrentElement.first().getAllElements())
						Server.DataBase.drives.put(element.tagName(), element.text());

					GenericReflection.NoThrow.setFinalStaticValue(Server.DataBase.class, "drives",
							Server.DataBase.drives/*
													 * Collections.
													 * unmodifiableMap(
													 * DataBase.drives)
													 */);
				}
			}

			Server.Internationalization.newVariantInstance(Server.Internationalization.logsLocale, "pt", "BR", "greencode/message/log_pt-BR.properties", "utf-8");

			currentElement = server.getElementsByTag("internationalization").first();
			if(currentElement != null) {
				for(Element _locale: currentElement.getElementsByTag("locale"))
					Server.Internationalization.newVariantInstance(Server.Internationalization.pagesLocale, _locale.attr("language"), _locale.attr("country"), _locale.attr("file"), _locale.attr("charset"));
			}

			currentElement = server.getElementsByTag("plugins").first();
			if(currentElement != null) {
				listCurrentElement = currentElement.getElementsByTag("plugin");
				final Class<?>[] list = new Class<?>[listCurrentElement.size()];
				
				if(list.length > 0) {
					for(int i = -1; ++i < list.length;) {
						list[0] = Class.forName(listCurrentElement.get(i).attr("class"));
					}
					GenericReflection.NoThrow.setFinalStaticValue(Server.Plugins.class, "list", list);
				}
			}

			listCurrentElement = null;
			currentElement = null;
		}

		System.out.println(" [done]");
	}

	public final static class Browser {
		public final static Boolean consoleDebug = false;
		public final static Boolean websocketSingleton = false;
	}

	public final static class Server {
		public final static Boolean writeLog = true;

		public final static class Request {
			public final static class Multipart {
				public final static Boolean autodectetion = false;
				public final static Integer maxRequestSize = -1;
			}
			
			public final static class Event {
				public final static String requestType = null;
				public final static String methodType = null;
			}
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

			public final static class Session {
				public final static Integer maxInactiveInterval = 1800;
			}
		}

		public final static class DataBase {
			public final static Boolean showResultQuery = true;

			public final static String defaultConfigFile = null;

			public final static HashMap<String, String> drives = new HashMap<String, String>();
			final static HashMap<String, DatabaseConfig> configs = new HashMap<String, DatabaseConfig>();

			public static DatabaseConfig getConfig(String path) {
				if(path == null)
					return null;
				
				if(configs.containsKey(path))
					return configs.get(path);

				System.out.println(Core.defaultLogMsg + "Caching Database Config File: " + path);

				Document src = null;
				try {
					final URL databaseConfigXml = GreenCodeConfig.class.getClassLoader().getResource(path);
					if(databaseConfigXml == null)
						throw new IOException();

					src = Jsoup.parse(new File(databaseConfigXml.getPath()), GreenCodeConfig.DEFAULT_CHARSET);

					Element databaseConfig = src.getElementsByTag("database-config").first();
					if(databaseConfig != null) {
						Elements listCurrentElement;

						DatabaseConfig config = new DatabaseConfig();

						config.setServerName(databaseConfig.attr("server-name"));
						config.setDatabase(databaseConfig.attr("database"));
						config.setSchema(databaseConfig.attr("schema"));
						config.setUserName(databaseConfig.attr("username"));
						config.setPassword(databaseConfig.attr("password"));

						listCurrentElement = databaseConfig.getElementsByTag("reconnect");
						if(!listCurrentElement.isEmpty()) {
							Element reconnectTag = listCurrentElement.first();

							config.setChanceReconnect(Byte.parseByte(reconnectTag.attr("chance")));
							config.setConnectionFileName(reconnectTag.attr("file"));
						}

						listCurrentElement = null;

						if(config.getDatabase() != null && !config.getDatabase().isEmpty()) {
							String driverName = DataBase.drives.get(config.getDatabase());
							if(driverName == null)
								throw new ConfigurationException("Driver do Banco de Dados especificado não foi declarado no arquivo de configuração 'greencode.config.xml'.");

							try {
								Class.forName(driverName);
							} catch(Exception e) {
								try {
									DriverManager.registerDriver((Driver) Class.forName(driverName).newInstance());
								} catch(ClassNotFoundException e1) {
									throw new ClassNotFoundException(LogMessage.getMessage("green-db-0000", config.getDatabase()));
								} catch(Exception e2) {
									throw new RuntimeException(e2);
								}
							}
						}

						configs.put(path, config);
						return config;
					}
				} catch(Exception e) {
					throw new GreencodeError(LogMessage.getMessage("green-db-0007", path));
				} finally {
					if(src != null) {
						src.empty();
						src = null;
					}
				}

				return null;
			}
		}

		public final static class Internationalization {
			static final List<Variant> pagesLocale = new ArrayList<Variant>();
			private static final List<Variant> logsLocale = new ArrayList<Variant>();

			public static Variant getVariantLogByLocale(Locale locale) {
				for(Variant v: logsLocale)
					if(v.locale.equals(locale))
						return v;

				return null;
			}

			public static Variant getVariantPageByLocale(Locale locale) {
				for(Variant v: pagesLocale)
					if(v.locale.equals(locale))
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
				list.add(new Variant(new Locale(language, country), Thread.currentThread().getContextClassLoader().getResource(file), file, (charsetName == null || charsetName.isEmpty()) ? "UTF8" : charsetName));
			}
		}

		public final static class Plugins {
			static final Class<PluginImplementation>[] list = null;
		}
	}
}
