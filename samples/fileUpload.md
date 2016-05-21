Html: index.html

```html
<html>
	<head>
		<meta charset="UTF-8">
		<title>Greencode</title>
	</head>
	<body>
		<form name="fileForm">
			<input type="file" name="file" id="file" />
			<button type="button" name="send" id="send">Send</button>
		</form>
	</body>
</html>
````
Java: IndexController.java

```java
@MultipartConfig // Or set multipart autodectetion="true" in greencode.config.xml
@Page(name="index", path="index.html")
public class IndexController extends Window {
    public void init(GreenContext context) {
    	document.getElementById("send").addEventListener(Events.CLICK, new FunctionHandle("send"));
    }
    
    public void send() {
    	FileForm form = document.forms(FileForm.class);
    	
    	Part partFile = form.getFile();
    	
    	System.out.println("File Name: "+partFile.getName());
    	System.out.println("File Size: "+partFile.getSize());
    	System.out.println("File Content Type: "+partFile.getContentType());
    }
}
```
Java: FileForm.java

```java
//Same name in html
@Name("fileForm")
public class FileForm extends Form {

	@ElementValue
	private Part file;

	public Part getFile() {
		return file;
	}
}
```