Html: index.html

```html
<html>
  <head>
  <meta charset="UTF-8">
    <title>Comet</title>
  </head>
  <body></body>
</html>
````
Java: IndexController.java

```java
@Page(name="index", path="index.html")
public class IndexController extends Window {
    public void init() {
        // Creating Div Element
        final DivElement div = document.createElement(DivElement.class);

        // Append div on Body
        document.body.appendChild(div);

        // Sample: Comet System - With Anonymous Classes
        // http://en.wikipedia.org/wiki/Comet_(programming)
        setTimeout(new SimpleFunction() {			
			private int i = -1;
            public void init() {
			
				//Loop to keep the conection and releasing the mods when necessary, using flushing method
                while(true) {                   
                    try {
                        div.textContent(++i+""); // Set Text Content
                        flush(); // Flushing

                        Thread.sleep(1000); // Sleep 1 Second
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ConnectionLost e) {
                        // On Connection Lost
						break;
                    }
                }
            }
        }, 0);

        // Sample 2: Comet System - Calling Method
        // http://en.wikipedia.org/wiki/Comet_(programming)
        setTimeout(new FunctionHandle("autoChangeColor"), 0);
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
			//Loop to keep the conection and releasing the mods when necessary, using flushing method
            while(true)  {          
                try {
                    div.style("color", colors[++i]); // Change Text Color
                    if(colors.length-1 == i)
                        i = -1; // Reset Variable to -1

                    flush();  // Flushing
                    Thread.sleep(200); // Sleep 200ms
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
