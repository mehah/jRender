Html: index.html

```html
<!DOCTYPE html>
<html>
	<head>
	    <meta charset="UTF-8">
	    <title>jRender</title>
	    <script>
	    	function customMethod(callback) {
	    		var arg = {
	    			methodName: "customMethod",
	    			randNumber: Math.random(),
	    			customAlert: function(msg) {
	    				alert(msg);
	    			}
	    		};
	    		
	    		callback(arg);
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
    public void init(JRenderContext context) {
    	DOMHandle.execCommand(window, "customMethod", new FunctionHandle(new CustomMethodFunction() {
			public void init(ObjectCustomMethod arg0) {
				System.out.println(arg0.methodName);
				System.out.println(arg0.randNumber);
				
				arg0.customAlert("Teste Custom Method");
			}
		}));
    }
}
```
Java: ObjectCustomMethod.java

```java
public class ObjectCustomMethod extends DOM {
	public final String methodName = null;
	public final Float randNumber = 0F;
	
	public void customAlert(String msg) {
		DOMHandle.execCommand(this, "customAlert", msg);
	}
	
	protected ObjectCustomMethod(Window window) {
		super(window);
	}
}
```

Java: CustomMethodFunction.java

```java
public interface CustomMethodFunction extends Function {
	public void init(ObjectCustomMethod arg0);
}
```
