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

	private InputTextElement<String> inputElement = document.getElementById("name", InputTextElement.class);
	
	public void init(GreenContext context) {
		// Register Event
		inputElement.addEventListener(Events.KEY_UP, new FunctionHandle("onKeyup"));
	}

	@ForceSync
	public void onKeyup() {
		// Get and print Input(Name) Value
		System.out.println(inputElement.value());
	}
}
```
