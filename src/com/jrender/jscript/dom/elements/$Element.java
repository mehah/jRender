package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Window;

public class $Element {	
	public static BodyElement getBodyInstance(Window window) { return new BodyElement(window); }
	public static HeadElement getHeadInstance(Window window) { return new HeadElement(window); }
	
	public static boolean isValueText(Class<?> clazz) {
		return clazz.equals(TextareaElement.class) || clazz.equals(InputTextElement.class) || clazz.equals(InputRadioElement.class) || clazz.equals(InputCheckboxElement.class)
				|| clazz.equals(InputPasswordElement.class) || clazz.equals(InputHiddenElement.class);
	}
	
	public static boolean isValueSelectable(Class<?> clazz) {
		return clazz.equals(SelectElement.class);
	}
	
	public static boolean isValueMultiSelectable(Class<?> clazz) {
		return clazz.equals(SelectMultipleElement.class);
	}
	
	public static boolean isValueCheckable(Class<?> clazz) {
		return clazz.equals(InputCheckboxElement.class);
	}
	
	public static boolean isElementWithValue(Class<?> clazz) {
		return isValueText(clazz) || isValueSelectable(clazz) || isValueMultiSelectable(clazz) || isValueCheckable(clazz) || clazz.equals(InputFileElement.class); 
	}
}
