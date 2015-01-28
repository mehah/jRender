Html: index.html

```html
<template:import />

<template:define name="content">
	Carlos
</template:define>
````
Html: index2.html

```html
<template:import />

<template:define name="content">
	Marcia
</template:define>
````
Html: index3.html

```html
<template:import name="template2" />

<template:define name="content">
	Leo
</template:define>
````
Html: template/defaultText.html

```html
Text1 text2 text3 text4
````
Html: template/template1.html

```html
<html>
	<head>
		<title>Template1</title>
	</head>
	<body>
		<template:include src="defaultText.html"></template:include>
		<div>HI</div>
		<template:insert name="content"></template:insert>
	</body>
</html>
````
Html: template/template2.html

```html
<html>
	<head>
		<title>Template2</title>
	</head>
	<body>
		<template:include src="defaultText.html"></template:include>
		<div>HELLO</div>
		<template:insert name="content"></template:insert>
	</body>
</html>
````
XML: Add inside 'VIEW tag' in greencode.config.xml
```xml
	<templates>
		<file name="template" path="template/template1.html" default="default" />
		<file name="template2" path="template/template2.html" />
	</templates>
```
