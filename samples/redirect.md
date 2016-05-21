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
	<div>Teste Redirect</div>
	<button type="button" name="redirect" id="redirect">Redirect</button>
	<button type="button" name="redirectAjax" id="redirectAjax" href="register.html" appendTo="body" empty changeURL keepViewId>Redirect With AJAX</button>
</body>
</html>
````
Html: register.html

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
	<label for="name">Name</label>: <input type="text" name="name" id="name"/>
	<div>
		<button type="button" name="alertName" id="alertName">Alert Name</button>
	</div>
</body>
</html>
````

Java: IndexController.java

```java
@Page(name="index", path="index.html")
public class IndexController extends Window {	
    public void init(GreenContext context) {
		document.getElementById("redirect").addEventListener(Events.CLICK, new FunctionHandle(new SimpleFunction() {
			public void init() {
				location.href(RegisterController.class);
			}
		}));
    }
}
```

Java: RegisterController.java

```java
@Page(name="register", path="register.html", ajaxSelector="body")
public class RegisterController extends Window {	
    public void init() {
    	final InputTextElement inputText = document.getElementById("name", InputTextElement.class);
    	
    	Element button = document.getElementById("alertName");
    	
    	button.addEventListener(Events.CLICK, new FunctionHandle(new SimpleFunction() {    		
    		@ForceSync
			public void init() {
				alert("Name: "+inputText.value());
			}
		}));
    }
}
```
