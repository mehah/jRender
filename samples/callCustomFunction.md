Html: index.html

```html
<!DOCTYPE html>
<html>
	<head>
	    <meta charset="UTF-8">
	    <title>Greencode</title>
	    <script>
	    	function customMethod() {
	    		alert("Teste");
	    	}
	    	
	    	function customMethodWithArgument(arg01) {
	    		alert(arg01);
	    	}
	    </script>
	</head>
	<body></body>
</html>
````
Java: IndexController.java

```java
@Page(name="index", path="index.html")
public class IndexController extends Window {
    public void init(GreenContext context) {
    	DOMHandle.execCommand(window, "customMethod");
    	DOMHandle.execCommand(window, "customMethodWithArgument", "Test Argument");    	
    }
}
```
