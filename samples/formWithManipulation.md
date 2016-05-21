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
@Page(name="index", path="index.html")
public class IndexController extends Window {
	
    public void init(GreenContext context) {
    	document.getElementById("buttonRegister").addEventListener(Events.CLICK, new FunctionHandle("register"));
    	
    	final InputCheckboxElement<String>[] countries = ElementHandle.cast(document.getElementsByName("countries"), InputCheckboxElement.class);
    	final InputCheckboxElement<String> checkboxAllOptions = document.getElementById("allOptions", InputCheckboxElement.class);
    	
    	checkboxAllOptions.addEventListener(Events.CLICK, new FunctionHandle(new SimpleFunction() {			
    		@ForceSync("checked")
			public void init(GreenContext context) {
    			for (InputCheckboxElement<String> inputCheckboxElement : countries) {
    				inputCheckboxElement.checked(checkboxAllOptions.checked());
				}
			}
		}));
    	
    	SelectElement<String> citySelect = SelectElement.cast(document.getElementById("city"));
    	
    	String[] citys = new String[] {"New York", "Los Angeles", "Chicago", "Houston"};
    	
    	for (String c : citys) {
        	OptionElement option = document.createElement(OptionElement.class);
        	option.value(c);
        	option.text(c);
        	citySelect.add(option);			
		}
    }
    
    public void register() {
    	MaintainUserForm form = document.forms(MaintainUserForm.class);
    	
    	System.out.println("Name: "+form.getName());
    	System.out.println("Sex: "+(form.getSex().equals('M') ? "Male" : "Female"));
    	System.out.println("City: "+form.getCity());    	
    	System.out.print("Countries: ");
    	if(form.getCountries() != null) {
	    	for (int i = -1, s = form.getCountries().length; ++i < s;) {
	    		Character separator = ' ';
	    		if(i > 0)
	    			separator = ',';
	    		System.out.print(separator+form.getCountries()[i]);
			}
    	}
    	System.out.println();
    }
}
```
Java: MaintainUserForm.java

```java
// Same name in html
@Name("maintainUserForm")
public class MaintainUserForm extends Form {
	
	// @ElementValue will say that the field will be a value retrived by the form through atribute 'name'
	// Trim is there for eliminate the spaces on begining and the end of the value
	@ElementValue(trim=true)
	private String name;
	
	@ElementValue
	private Character sex;
	
	@ElementValue
	private String city;
	
	@ElementValue
	private String[] countries;
	
	public String getName() {
		return name;
	}

	public Character getSex() {
		return sex;
	}

	public String getCity() {
		return city;
	}

	public String[] getCountries() {
		return countries;
	}
}
```