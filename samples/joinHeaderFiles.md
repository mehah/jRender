Html: index.html

```html
<html>
	<head>
		<title>Join Files Header</title>
		<!-- HERE -->
		<link href="GREENCODE:{CONTEXT_PATH}/css/main.css" join="css/css1.css, css/css2.css" file="css/main.css" media="all" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<span class="class1 class2">Joined</span>
	</body>
</html>
````
CSS: css/css1.css

```css
span.class1 {
	color: red;
}
```
CSS: css/css2.css

```css
span.class2 {
	font-size: 20px;
}
```
