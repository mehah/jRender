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
	<div>
		<select name="language" id="language">
			<option value="pt-BR">Portuguese(Brazil)</option>
			<option value="en-US">English(United States)</option>
			<option value="fr-FR">French(France)</option>
			<option value="es-MX">Spanish(Mexico)</option>
		</select>
	</div>
	<div msg:key="message.welcomeGreencode"></div>
</body>
</html>
````
Java: IndexController.java

```java
@Page(name = "index", path = "index.html")
public class IndexController extends Window {

	public void init(JRenderContext context) {
		// Set default user locale to pt-BR
		context.setUserLocale(new Locale("pt", "BR"));

		// Get element by Id(language)
		final SelectElement<String> selectElement = document.getElementById("language", SelectElement.class, String.class);

		// Register Event 'Change'
		selectElement.addEventListener(Events.CHANGE, new FunctionHandle(new SimpleFunction() {

			// Force sync only for the selectedIndex atribute
			@ForceSync("selectedIndex")
			public void init(JRenderContext context) {
				// Retrieve the index highlighted from select
				Integer selectedIndex = selectElement.selectedIndex();

				// The method options(eager = true), retrieve all the options at once with their respective atributes
				for (OptionElement<String> option : selectElement.options(true)) {
					if (selectedIndex.equals(option.index())) {
						String[] language = option.value().split("-");

						// Sets user locale according to selected option.
						context.setUserLocale(new Locale(language[0], language[1]));

						// Show on console the current property of userLocale
						System.out.println(Message.getMessage("message.messageByController"));
						break;
					}
				}
			}
		}));
	}
}
```

XML: Add in greencode.config.xml
```xml
	<internationalization>
		<locale language="pt" country="BR" file="msg_pt-BR.properties" charset="utf-8"/>
		<locale language="en" country="US" file="msg_en-US.properties" charset="utf-8"/> 
		<locale language="fr" country="FR" file="msg_fr-FR.properties" charset="utf-8"/>
		<locale language="es" country="MX" file="msg_es-MX.properties" charset="utf-8"/> 
	</internationalization>
```

Propertie: src/msg_en-US.properties
```prop
message.welcomeGreencode = Welcome to Greencode
message.messageByController = Message by Controller
```

Propertie: src/msg_es-MX.properties
```prop
message.welcomeGreencode = Bienvenido a Greencode
message.messageByController = Mensaje por el Controlador
```

Propertie: src/msg_fr-FR.properties
```prop
message.welcomeGreencode = Bienvenue à Greencode
message.messageByController = un message par le dispositif de commande
```

Propertie: src/msg_pt-BR.properties
```prop
message.welcomeGreencode = Bem Vindo ao Greencode
message.messageByController = Mensagem pelo Controlador
```
