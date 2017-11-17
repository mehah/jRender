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
	<form name="maintainUserForm">
		<div><label for="name">Name</label> <input type="text" name="name"/></div>
		<div><label for="sex">Sex</label> <input type="radio" name="sex" value="M" checked="checked"/> Male <input type="radio" name="sex" value="F"/> Female</div>
		<div><label for="city">City</label> <select name="city" id="city"></select></div>
		<div>
			<div>Which countries have you visit?</div>
			<ul>
				<li><input type="checkbox" name="countries" value="Afghanistan" /> Afghanistan</li>
				<li><input type="checkbox" name="countries" value="Australia" /> Australia</li>
				<li><input type="checkbox" name="countries" value="Brazil" /> Brazil</li>
				<li><input type="checkbox" name="countries" value="Cuba" /> Cuba</li>
				<li><input type="checkbox" name="countries" value="Egypt" /> Egypt</li>
				<li><input type="checkbox" name="countries" value="United States" /> United States</li>
				<li style="padding: 10px 0px;"><input type="checkbox" name="allOptions" id="allOptions" /> All options above</li>				
			</ul>
		</div>
		<button type="button" name="buttonRegister" id="buttonRegister">Register</button>
	</form>
</body>
</html>
````
Java: IndexController.java

```java
@Page(name = "index", path = "index.html")
public class IndexController extends Window {
	private static final String[] CITYS = new String[] { "New York", "Los Angeles", "Chicago", "Houston" };

	private final MaintainUserForm form = document.forms(MaintainUserForm.class);

	public void init(JRenderContext context) {
		document.getElementById("buttonRegister").addEventListener(Events.CLICK, new FunctionHandle("register"));

		final InputCheckboxElement<String>[] countries = document.getElementsByName("countries", InputCheckboxElement.class);

		form.getAllOptions().addEventListener(Events.CLICK, new FunctionHandle(new SimpleFunction() {
			@ForceSync("checked")
			public void init(JRenderContext context) {
				for (InputCheckboxElement<String> inputCheckboxElement : countries) {
					inputCheckboxElement.checked(form.getAllOptions().checked());
				}
			}
		}));

		for (String c : CITYS) {
			OptionElement<String> option = document.createElement(OptionElement.class, String.class);
			option.value(c);
			option.text(c);

			form.getCity().add(option);
		}
		
		form.reset();
	}

	public void register() {
		System.out.println("Name: " + form.getName());
		System.out.println("Sex: " + (form.getSex().equals('M') ? "Male" : "Female"));
		System.out.println("City: " + form.getCity().selectedValue());
		System.out.print("Countries: ");
		StringBuilder countries = new StringBuilder(' ');
		if (form.getCountries() != null) {
			for (int i = -1, s = form.getCountries().length; ++i < s;) {
				if (i > 0)
					countries.append(',');
				countries.append(form.getCountries()[i]);
			}
		} else {
			countries.append("none");
		}
		System.out.println(countries.toString());
	}
}
```
Java: MaintainUserForm.java

```java
//Same name in html
@Name("maintainUserForm")
public class MaintainUserForm extends Form {
	
	// @ElementValue will say that the field will be a value retrived by the form through atribute 'name'
	// Trim is there for eliminate the spaces on begining and the end of the value
	@ElementValue(trim=true)
	private String name;
	
	@ElementValue
	private Character sex;
	
	@ElementValue
	private SelectElement<String> city;
	
	@ElementValue
	private String[] countries;
	
	@ElementValue
	private InputCheckboxElement<String> allOptions;
	
	public String getName() {
		return name;
	}

	public Character getSex() {
		return sex;
	}

	public SelectElement<String> getCity() {
		return city;
	}

	public String[] getCountries() {
		return countries;
	}

	public InputCheckboxElement<String> getAllOptions() {
		return allOptions;
	}

	public void setAllOptions(InputCheckboxElement<String> allOptions) {
		this.allOptions = allOptions;
	}
}
```
