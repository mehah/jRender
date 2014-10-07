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
		<div><label for="city">City</label>
			<select name="city">
				<option value="Rio de Janeiro">Rio de Janeiro</option>
				<option value="Curitiba">Curitiba</option>
			</select>
		</div>
		<div>
			<div><label for="countries">Which countries have you visit?</label></div>
			<ul>
				<li><input type="checkbox" name="countries" value="Afghanistan" /> Afghanistan</li>
				<li><input type="checkbox" name="countries" value="Australia" /> Australia</li>
				<li><input type="checkbox" name="countries" value="Brazil" /> Brazil</li>
				<li><input type="checkbox" name="countries" value="Cuba" /> Cuba</li>
				<li><input type="checkbox" name="countries" value="Egypt" /> Egypt</li>
				<li><input type="checkbox" name="countries" value="United States" /> United States</li>
			</ul>
		</div>
	</form>
	<button type="button" name="buttonRegister" id="buttonRegister">Register</button> <!-- outside the <form> -->
</body>
</html>
````
Java: IndexController.java

```java
@Page(name="index", path="index.html")
public class IndexController extends Window {
	
    public void init() {
    	document.getElementById("buttonRegister").addEventListener(Events.CLICK, new FunctionHandle("register"));
    }
    
    /* As the button(id: buttonregister) is out of the form, it requires to set which one is gonna be used,
     * cuz the button doesnt have any bound with the form. The framework knows which form to use,
     * when the atribute 'form' is filled
    */
    @Form(MaintainUserForm.class)
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