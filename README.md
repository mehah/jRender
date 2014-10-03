Greencode
=========

Framework que trabalha similar à aplicações de desktop orientado a eventos, como Java Swing. Podendo chamar funções javascript, registrar eventos, preencher formulário, tudo através da programação Java sem intervenção no HTML.

Exemplo:

Html: index.html

```html
<html>
  <head>
  <meta charset="UTF-8">
    <title>Sample</title>
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
		
		// Creating Div Element
		final DivElement div = document.createElement(DivElement.class);
		
		// Append div on Body
		document.body.appendChild(div);
		
		// Sample: Comet System - With Anonymous Classes
		// http://en.wikipedia.org/wiki/Comet_(programming)
		setTimeout(new SimpleFunction() {
			int i = -1;
			public void init() {
				while(true) {					
					try {
						div.textContent(++i+""); // Set Text Content
						flush(); // Flushing
						
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ConnectionLost e) {
						// On Connection Lost
					}
				}
			}
		}, 0);
		
		// Sample 2: Comet System - Calling Method
		// http://en.wikipedia.org/wiki/Comet_(programming)
		setTimeout(new FunctionHandle("autoChangeColor"), 0);
	}

	@ForceSync(value="value", onlyOnce=true)
	public void onKeyup() {
		// Get and print Input(Name) Value
		System.out.println(inputElement.value());
	}
	
	private final String[] colors = new String[]{"red", "blue", "green"};
	private int i = -1;
	public void autoChangeColor() {
	
		// Creating Element
		final DivElement div = document.createElement(DivElement.class);
		
		// Append div on Body
		document.body.appendChild(div);
		
		// Set Text Content
		div.textContent("Auto Change Color");
		
		try {
			while(true)  {			
				try {
					div.style("color", colors[++i]);
					if(colors.length-1 == i)
						i = -1;
					
					flush();
					Thread.sleep(200);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		} catch(ConnectionLost e) {
			// On Connection Lost
		}
	}
}
```
