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
	<div>Teste</div>
	<div>
		<button type="button" name="showContent" id="showContent">Show Content in newContent.html</button><button type="button" name="empty" id="empty">Empty</button>
	</div>
	<div id="content"></div>
</body>
</html>
````
Html: newContent.html

```html
New Content (Any Text)
````

Java: IndexController.java

```java
@Page(name="index", path="index.html")
public class IndexController extends Window {
	private final Element div = document.getElementById("content");
	
    public void init(GreenContext context) {
		document.getElementById("showContent").addEventListener(Events.CLICK, new FunctionHandle(new SimpleFunction() {
			public void init(GreenContext context) {
				div.replaceWith(AnyController.class);
			}
		}));
		
		document.getElementById("empty").addEventListener(Events.CLICK, new FunctionHandle(new SimpleFunction() {
			public void init(GreenContext context) {
				ElementHandle.empty(div); // OR div.innerHTML("");
			}
		}));
    }
}
```
Java: AnyController.java

```java
@Page(name="any", path="newContent.html")
public class AnyController extends Window {	
    public void init(GreenContext context) {}
}
```