jRender
=========
Is a java library that gives you the power to manipulate the DOM exactly like javascript, taking advantage of Object Orientation and language typing, rendering and validating your application with more security and performance.

**Security:**
All of its logic, as well as rendering and validation, can be performed inside the server, this way we hide the code from more intentional people.

**Performance:**
The client-server communication is done with JSON, with this we have fewer bytes passing, which can be accomplished with either ajax, iframe or websocket.

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
	private static int visitorsCount = 0;
	
	public void init(JRenderContext arg0) {
		document.getElementById("count").textContent((++visitorsCount)+"");		
	}
}
```

**Understand:**
- Annotation
	- [Page and RegisterPage](/understand/pageRegisterPage.md)

**More examples:**
- [Basic](/samples/basic.md)  
- [Basic Form ](/samples/formBasic.md)  
- [Basic Form With outside Button](/samples/formBasicWithOutsideButton.md)  
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
- [File Upload](/samples/fileUpload.md)  
- [Java Script Module System](/samples/javaScriptModuleSystem.md)  
