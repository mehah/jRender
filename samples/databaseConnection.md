Requirements:
- [MYSQL JDBC](http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.33.tar.gz)  

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
	private final TbodyElement tbody = document.getElementById("userList").querySelector("tbody", TbodyElement.class);
	private final InputTextElement<String> userNameInput = document.getElementById("userName", InputTextElement.class, String.class);
	
	@Connection
    public void init(JRenderContext context) {
		document.getElementById("register").addEventListener(Events.CLICK, new FunctionHandle("register"));
		
		DatabaseConnection connection = context.getDatabaseConnection();
		
		try {
			ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM users");
			
			while(rs.next())
				addUser(rs.getInt("id"), rs.getString("name"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
	@ForceSync
	@Connection
	public void register(JRenderContext context) {
		try {
			String value = userNameInput.value().trim();
			
			DatabaseConnection connection = context.getDatabaseConnection();
			
			DatabasePreparedStatement ps = connection.prepareStatement("INSERT INTO users(name) values(?)");			
			ps.setString(1, value);			
			ps.execute();
			
			ResultSet rs = connection.createStatement().executeQuery("SELECT LAST_INSERT_ID();");
			rs.next();
			
			addUser(rs.getInt(1), value);
			
			userNameInput.value("");
		} catch (SQLException e) {
			Console.error(e);
		}
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

XML: Add in greencode.config.xml
```xml
	<database>
		<default-config-file>database.config.xml</default-config-file>
		<show-query>true</show-query>	
		<drivers>
			<mysql>com.mysql.jdbc.Driver</mysql>
		</drivers>
	</database>
```

XML: src/database.config.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<database-config
	server-name="127.0.0.1"
	database="mysql"
	schema="sample"
	username="root"
	password=""
>	
	<!-- <reconnect chance="5" file="" /> -->
</database-config>
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
