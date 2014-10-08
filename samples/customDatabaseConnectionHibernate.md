- [MYSQL JDBC](http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.33.tar.gz)  
  
[[Hibernate](http://hibernate.org/)]  
Libs:  
- antlr-2.7.7.jar  
- c3p0-0.9.2.1.jar  
- dom4j-1.6.1.jar  
- hibernate-c3p0-4.2.7.SP1.jar  
- hibernate-commons-annotations-4.0.2.Final.jar  
- hibernate-core-4.2.2.Final.jar  
- hibernate-jpa-2.0-api-1.0.1.Final.jar  
- javassist-3.15.0-GA.jar  
- jboss-logging-3.1.0.GA.jar  
- jboss-transaction-api_1.1_spec-1.0.1.Final.jar  
- mchange-commons-java-0.2.3.4.jar  

Html: index.html

```html
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Form</title>
	<style>
		li {list-style-type: none;}
	</style>
</head>
<body>
	<input type="text" name="userName" id="userName" /> <button type="button" name="register" id="register">Register</button>
	<table id="userList">
		<tbody></tbody>
	</table>
</body>
</html>
````
Java: IndexController.java

```java
@Page(name="index", path="index.html")
public class IndexController extends Window {
	private final TbodyElement tbody = TbodyElement.cast(document.getElementById("userList").querySelector("tbody"));
	private final InputTextElement userNameInput = InputTextElement.cast(document.getElementById("userName"));
	
	@Connection
    public void init() {
		document.getElementById("register").addEventListener(Events.CLICK, new FunctionHandle("register"));
		
		List<User> users = (List<User>) HibernateUtil.getCurrentSession().getNamedQuery("User.findAll").list();
		
		for (User user : users)
			addUser(user.getId(), user.getName());
    }
	
	@ForceSync
	@Connection
	public void register() {
		String value = userNameInput.value().trim();
		
		User user = new User();
		user.setName(value);
		
		int id = (Integer) HibernateUtil.getCurrentSession().save(user);
		
		addUser(id, value);
		
		userNameInput.value("");
	}
	
	private void addUser(int id, String name) {
		Element tr = document.createElement("tr");
		
		Element idTd = document.createElement("td");
		Element nameTd = document.createElement("td");
		
		tr.appendChild(idTd);
		tr.appendChild(nameTd);
		
		idTd.textContent(id+"");
		nameTd.textContent(name);
		
		tbody.appendChild(tr);
	}
}
```
Java: BootAction.java

```java
public class BootAction implements BootActionImplementation {
	
	public void init(String projectName, ClassLoader classLoader, ServletContext context, CoreFileJS coreFileJS) {}

	public void destroy() {
		HibernateUtil.destroy();
	}
	
	public void initUserContext(GreenContext context) {}
	
	public void onRequest() {}

	public boolean beforeAction(GreenContext context, Method method) { return true; }

	public void afterAction(GreenContext context, Method method) {}	
}
```
Java: HibernateUtil.java

```java
public class HibernateUtil implements DatabaseConnectionEvent {
	private static final SessionFactory sessionFactory;
    	
    static {
       Configuration configuration = new Configuration();
        
       configuration.addAnnotatedClass(User.class);
       
        /*try {
			for (Class<?> c : PackageUtils.getClasses("database.model")) {
				configuration.addAnnotatedClass(c);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
        
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory(new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry());    	
    }
    
    private Session session = sessionFactory.getCurrentSession();
    private Transaction transaction = null;
    
	public void beforeRequest(Connection connection) {
		if (session.getTransaction() != null && session.getTransaction().isActive())
			this.transaction = session.getTransaction();
	    else
	    	this.transaction = session.beginTransaction();
	}
	
	public void afterRequest() {
		this.transaction.commit();
		close();
	}
	
	public void onError(Exception e) {
		this.transaction.rollback();
		close();
	}

	public void onSuccess() {}
	
	public void close() {
		if(session.isConnected())
			session.close();
	}
	
	public static void destroy() {
		sessionFactory.close();
	}
	
	public Session getSession() {
		return session;
	}
	
	public static Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}
```
Java: User.java

```java
@Entity
@Table(name="users")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id;

	@Column(nullable=false, length=60)
	private String name;
	
	public User() {}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
```

XML: src/hibernate.cfg.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
"-//Hibernate/Hibernate Configuration DTD 3.0//EN"
"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
    	<property name="hibernate.current_session_context_class">thread</property>  
        <property name="hibernate.bytecode.use_reflection_optimizer">true</property>
        <property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://127.0.0.1:3306/sample</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password"></property>
        
        <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
        <property name="show_sql">true</property>
        
		<property name="c3p0.min_size">5</property>
		<property name="c3p0.max_size">20</property>
		<property name="c3p0.timeout">1800</property>
		<property name="c3p0.max_statements">50</property>
		<property name="connection.provider_class">org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider</property>
		<property name="hibernate.c3p0.testConnectionOnCheckin">true</property>
		<!-- testConnectionOnCheckout -->
    </session-factory>
</hibernate-configuration>
```

Import SQL (Schema: sample)
```sql
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```