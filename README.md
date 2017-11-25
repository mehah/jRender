jRender
=========
It's a library that allows Java to manipulate DOM, exactly like Javascript, using object orientation and language typing Java. Render and validate your application with more security and performance

**Security:**
 All of its logic, as well as rendering and validation, can be performed inside the server, this way we hide code.

**Performance:**
The client-server communication is done with JSON, with this we have fewer bytes passing, which can be accomplished with either ajax, iframe or websocket.

**JavaScript frameworks:**
As the library allows interac with Dom, then it will have access to any framework created for Javascript, but to let the writing more close to Javascript, it will be necessary to create plug-ins, for example : [jQuery](https://github.com/mehah/JQuery) and [jQueryUI](https://github.com/mehah/JQueryUI).

Min. Requirements
- Java 1.6
- Servlet 3.0
- [Gson](https://code.google.com/p/google-gson/)  
- [JSOUP](http://jsoup.org/)  
- [HTMLCompressor](https://code.google.com/p/htmlcompressor/) (Optional)  

Javascript Cross-Browser Lib Support
- [Sizzle](http://sizzlejs.com/)  
- [JSON3](http://bestiejs.github.io/json3/)


Example
========
Small example of visitor counts.

index.html
```html
<html>
	<body>
		Amount of people that had seen this page: <span id="count"></span>
	</body>
</html>
```

IndexController.java
```java
@Page(name="index", path="index.html")
public class IndexController extends Window {
	private static int VISITORS_COUNT = 0;
	
	public void init(JRenderContext arg0) {
		document.getElementById("count").textContent((++VISITORS_COUNT)+"");		
	}
}
```
**OR**

Real time visitor count, so that a refresh page isnt necessary.
```java
@Page(name="index", path="index.html")
public class IndexController extends Window {	
	private static int VISITORS_COUNT = 0;
	
	private final Element spanCount = document.getElementById("count");
	
	private int lastCount;
	
	public void init(JRenderContext context) {		
		spanCount.textContent((++VISITORS_COUNT)+"");
		
		this.lastCount = VISITORS_COUNT;
		setInterval(new FunctionHandle("realTimeUpdate"), 1000);
	}
	
	public void realTimeUpdate() {
		if(this.lastCount != VISITORS_COUNT) {
			this.lastCount = VISITORS_COUNT;
			spanCount.textContent(VISITORS_COUNT+"");
		}
	}
}
```

**Understand:**
- Annotation
	- [Page and RegisterPage](/understand/pageRegisterPage.md)

**More examples:**
- [Form](/samples/formBasic.md)
- [Form With outside Button](/samples/formBasicWithOutsideButton.md)
- [Form Validation](/samples/formValidation.md)
- [Form With Data Manipulation](/samples/formWithManipulation.md)
- [Comet](/samples/comet.md)
- [Internationalization](/samples/internationalization.md)
- [Database Connection with MYSQL](/samples/databaseConnection.md)
- [Database Connection with MYSQL/Hibernate](/samples/customDatabaseConnectionHibernate.md)
- [Replace Content](/samples/replaceContent.md)
- [Redirect](/samples/redirect.md)
- [Call Custom Function](/samples/callCustomFunction.md)
- [Call Custom Function With Callback](/samples/callCustomFunctionWithCallbackFunction.md)
- [Template](/samples/template.md)
- [Join Header Files](/samples/joinHeaderFiles.md)
- [User Principal/Rules](/samples/userPrincipalRules.md)
- [Java Script Module System](/samples/javaScriptModuleSystem.md)
