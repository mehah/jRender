Html: index.html

```html
<html>
  <head>
  <meta charset="UTF-8">
    <title>Greencode</title>
  </head>
  <body></body>
</html>
````
Java: IndexController.java

```java
@Page(name="index", path="index.html", jsModule="package.index")
public class IndexController extends Window {
    public void init() {}
    
    public void teste() {
    	alert("Alert on Java Controller");	
    }
}
```
JavaScript: src/package/index.js

```javascript
alert("Alert on Module");

teste(function() {
	alert("Alert after Complete Request");	
});
```