Html: index.html

```html
<html>
  <head>
  <meta charset="UTF-8">
    <title>Basic</title>
  </head>
  <body>
  	<input type="text" name="name" id="name" />
  </body>
</html>
````
Java: IndexController.java

```java
@Page(name="index", path="index.html")
public class IndexController extends Window {

	private InputTextElement inputElement = InputTextElement.cast(document.getElementById("name"));
	
	public void init() {
		// Register Event
		inputElement.addEventListener(Events.KEY_UP, new FunctionHandle("onKeyup"));
	}

	@ForceSync(value="value", onlyOnce=true)
	public void onKeyup() {
		// Get and print Input(Name) Value
		System.out.println(inputElement.value());
	}
}
```
