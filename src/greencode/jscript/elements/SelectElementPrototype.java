package greencode.jscript.elements;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Form;
import greencode.jscript.Window;
import greencode.util.ClassUtils;

public abstract class SelectElementPrototype<T> extends Element {
	protected final Class<T> typeValue;
		
	protected SelectElementPrototype(String type, Window window, Class<?> typeValue) {
		super(window, "select");
		DOMHandle.setVariableValue(this, "type", type);
		
		this.typeValue = (Class<T>) (typeValue == null ?  ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] : typeValue);
	}
	
	public Form form() { return null; }
	
	public void name(String name) { DOMHandle.setProperty(this, "name", name); }
	
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	public String type() { return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type"); }
	
	public void selectedIndex(Integer value) { DOMHandle.setProperty(this, "selectedIndex", value); }
	
	public Integer selectedIndex() { return DOMHandle.getVariableValueByProperty(this, "selectedIndex", Integer.class, "selectedIndex"); }
	
	public void size(Integer size) { DOMHandle.setProperty(this, "size", size); }
	
	public Integer size() { return DOMHandle.getVariableValueByProperty(this, "size", Integer.class, "size"); }
	
	public Integer length() { return DOMHandle.getVariableValueByProperty(this, "length", Integer.class, "length"); }
	
	public void disabled(boolean disabled) { DOMHandle.setProperty(this, "disabled", disabled); }
	
	public Boolean disabled() { return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled"); }
	
	public void add(OptionElement<T> option) { add(option, null); }
	
	public void add(OptionElement<T> option, Integer index) {
		if(index == null)
			options().add(option);	
		else
			options().add(option, index);
	}
	
	public void remove(int index) {
		if(DOMHandle.containVariableKey(this, "length")) {
			Integer length = DOMHandle.getVariableValue(this, "length", Integer.class);
			if(index < length)
				DOMHandle.setVariableValue(this, "length", length-1);
		}
		
		DOMHandle.execCommand(this, "remove", index);
	}
	
	private OptionElementCollection<T> options;
	
	public OptionElementCollection<T> options() {
		if(options == null) {
			DOMHandle.registerReturnByProperty(this, DOMHandle.getUID(options = new OptionElementCollection<T>(this.window, typeValue)), "options");
			options.list = new ArrayList<OptionElement<T>>();
		}
		return options;
	}
	
	/*
	 * Synchronized Options 
	 */
	public OptionElementCollection<T> options(final boolean fetchEager) {
		options = null;		
		options();
		
		if(fetchEager) {
			JsonArray res = DOMHandle.getJSONArrayByProperty(this, "__contentOptions", "options", "value", "text", "id", "index", "selected", "disabled", "defaultSelected");
			DOMHandle.removeVariable(this, "__contentOptions");
			
			options.list = new ArrayList<OptionElement<T>>();
			final int size = res.size();
			if(size > 0) {
				for (int i = -1; ++i < size;) {
					JsonElement json = res.get(i);
					OptionElement<T> option = new OptionElement<T>(this.window, typeValue);
					
					Object value = ((JsonObject)json).get("value").getAsString();
					
					if(!ClassUtils.isPrimitiveOrWrapper(typeValue)) {
						value = greencode.jscript.$Window.getObjectParamter(window, (String) value);
					}
					
					DOMHandle.setVariableValue(option, "value", value);
					DOMHandle.setVariableValue(option, "text", ((JsonObject)json).get("text").getAsString());
					DOMHandle.setVariableValue(option, "id", ((JsonObject)json).get("id").getAsString());
					DOMHandle.setVariableValue(option, "index", ((JsonObject)json).get("index").getAsInt());
					DOMHandle.setVariableValue(option, "selected", ((JsonObject)json).get("selected").getAsBoolean());
					DOMHandle.setVariableValue(option, "disabled", ((JsonObject)json).get("disabled").getAsBoolean());
					DOMHandle.setVariableValue(option, "defaultSelected", ((JsonObject)json).get("defaultSelected").getAsBoolean());
					options.list.add(option);
					DOMHandle.registerElementByProperty(this, option, "options["+i+"]");
				}
			}
		}else
			options.length();
		
		return options;
	}
	
	public Boolean multiple() {
		return DOMHandle.getVariableValueByProperty(this, "multiple", Boolean.class, "multiple");
	}
	
	public SelectElementPrototype multiple(boolean multiple) {
		DOMHandle.setProperty(this, "multiple", multiple);
		
		if(this instanceof SelectMultipleElement) {
			if(!multiple)
				return ElementHandle.cast(this, SelectElement.class);
		} else if(multiple)
			return ElementHandle.cast(this, SelectMultipleElement.class);
		
		return this;
	}
}
