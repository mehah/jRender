greencode
=========

Framework que trabalha similar à aplicações de desktop orientado a eventos, como Java Swing. Podendo chamar funções javascript, registrar eventos, preencher formulário, tudo através da programação Java sem intervenção no HTML.

Exemplo:

Html: index.html

```
<html>
  <head>
  <meta charset="ISO-8859-1">
    <title>Sample</title>
  </head>
  <body>
  	<input type="text" name="nome" id="nome" />
  </body>
</html>
```
Java: IndexController.java

```
@Page(name="index", path="index.html")
public class IndexController extends Window {

	private InputTextElement inputElement = InputTextElement.cast(document.getElementById("nome"));
	
  public void init() {
		inputElement.addEventListener(Events.KEY_UP, new FunctionHandle("onKeyup"));
		
		final DivElement div = document.createElement(DivElement.class);
		document.body.appendChild(div);
		
		// Sample: Comet System
		// http://en.wikipedia.org/wiki/Comet_(programming)
		setTimeout(new SimpleFunction() {
			int i = -1;
			public void init() {
				while(true) {					
					try {
						div.textContent(++i+"");
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
	}

	@ForceSync(value="value", onlyOnce=true)
	public void onKeyup() {
		System.out.println(inputElement.value());
	}
}
```
