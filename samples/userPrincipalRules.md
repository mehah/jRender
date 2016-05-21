Html: index.html

```html
<html>
	<head>
		<meta charset="UTF-8">
		<title>Greencode</title>
	</head>
	<body>
		<form name="loginForm">
			<label for="name">User Name</label><input type="text" name="name" id="name" />
			<label for="password">Password</label><input type="text" name="password" id="password" />
			<button type="button" name="login" id="login">Login</button>
		</form>
	</body>
</html>
````
Html: home.html

```html
<html>
	<head>
		<meta charset="UTF-8">
		<title>Greencode</title>
	</head>
	<body>
		<a href="#" id="exit">Exit</a>
	</body>
</html>
````
Java: IndexController.java

```java
@Page(name="index", path="index.html")
public class IndexController extends Window {
    public void init(GreenContext context) {
    	document.getElementById("login").addEventListener(Events.CLICK, new FunctionHandle("login"));
    	    	
    	if(context.getRequest().getUserPrincipal() != null) {
    		location.href(HomeController.class);
    	}
    }
    
    public void login(GreenContext context) {
    	LoginForm form = document.forms(LoginForm.class);
    	
    	if(form.getName() != null && form.getPassword() != null &&
    		form.getName().equals("greencode") && form.getPassword().equals("123456")){
    		context.getRequest().setUserPrincipal(new Usuario("Greencode"));
    		location.href(HomeController.class);
    	}else
    		alert("User: greencode / Password: 123456");
    }
}
```
Java: HomeController.java

```java
@Page(name="home", path="home.html", rules="ACCESS_HOME")
public class HomeController extends Window {
	
	// OR @RulesAllowed("ACCESS_HOME")
    public void init(GreenContext context) {
    	document.getElementById("exit").addEventListener(Events.CLICK, new FunctionHandle(new SimpleFunction() {
			public void init(GreenContext context) {
				context.getRequest().getSession().invalidate();
				location.href(IndexController.class);
			}
		}));
    }
}
```
Java: LoginForm.java

```java
//Same name in html
@Name("loginForm")
public class LoginForm extends Form {

	@ElementValue(trim=true)
	private String name;
	
	@ElementValue
	private String password;

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}
}
```
Java: Usuario.java

```java
public class Usuario extends UserPrincipal {
	private String name;
	
	public Usuario(String name) {
		this.name = name;
		
		addRule("ACCESS_HOME");
	}
	
	public String getName() {
		return name;
	}
}
```