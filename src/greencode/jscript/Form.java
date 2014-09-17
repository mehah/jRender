package greencode.jscript;

import java.lang.reflect.Field;

import greencode.http.enumeration.RequestMethod;
import greencode.jscript.form.annotation.Event;
import greencode.jscript.form.annotation.Name;
import greencode.jscript.form.annotation.RegisterEvent;
import greencode.kernel.GreenContext;
import greencode.kernel.LogMessage;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;
import greencode.util.GenericReflection.Condition;

public abstract class Form extends Element {
	private static Condition<Field> fieldsWithRegisterEvent = new Condition<Field>() {		
		public boolean init(Field arg0) { return arg0.isAnnotationPresent(RegisterEvent.class) && ClassUtils.isParent(arg0.getType(), EventTarget.class); }
	};
	
	
	public Form() { this(GreenContext.getInstance()); }
	
	public Form(GreenContext context) {
		super(context.currentWindow());
		String name = getClass().getAnnotation(Name.class).value();
		
		DOMHandle.registerElementByCommand(context.currentWindow().principalElement(), this, "crossbrowser.querySelector", "form[name=\""+name+"\"]");
	}
	
	void processAnnotation() {
		Field[] fields = GenericReflection.getDeclaredFieldsByConditionId(getClass(), "form:elementsWithRegisterEvent");
		if(fields == null)
			fields = GenericReflection.getDeclaredFieldsByCondition(getClass(), "form:elementsWithRegisterEvent", fieldsWithRegisterEvent);
	
		for (Field f : fields) {
			Event[] events = f.getAnnotation(RegisterEvent.class).value();
			EventTarget et = (EventTarget) GenericReflection.NoThrow.getValue(f, this);
			
			if(et == null)
				throw new RuntimeException(LogMessage.getMessage("green-0036", f.getName(), getClass().getSimpleName()));
			
			for (Event e : events) {
				FunctionHandle fh = new FunctionHandle(e.windowAction(), e.method());
				fh.setRequestMethod(e.requestMethod());
				
				for (String n : e.name())
					et.addEventListener(n, fh);
			}
		}
	}
	
	public void acceptCharset(String acceptCharset) {
		DOMHandle.setProperty(this, "acceptCharset", acceptCharset);
	}
	
	public String acceptCharset() {
		return DOMHandle.getVariableValueByProperty(this, "acceptCharset", String.class, "acceptCharset");
	}
	
	public void action(String action) {
		DOMHandle.setProperty(this, "action", action);
	}
	
	public String action() {
		return DOMHandle.getVariableValueByProperty(this, "action", String.class, "action");
	}
	
	public void enctype(String enctype) { DOMHandle.setProperty(this, "enctype", enctype); }
	
	public String enctype() {
		return DOMHandle.getVariableValueByProperty(this, "enctype", String.class, "enctype");
	}
	
	public Integer length() {
		return DOMHandle.getVariableValueByProperty(this, "length", Integer.class, "length");
	}
	
	public void method(RequestMethod method) {
		DOMHandle.setProperty(this, "method", method);
	}
	
	public RequestMethod method() {
		return RequestMethod.valueOf(DOMHandle.getVariableValueByProperty(this, "method", String.class, "method").toUpperCase());
	}
	
	public void name(String name) { DOMHandle.setProperty(this, "name", name); }
	
	public String name() {
		return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name");
	}
	
	public void target(String target) { DOMHandle.setProperty(this, "target", target); }
	
	public String target() {
		return DOMHandle.getVariableValueByProperty(this, "target", String.class, "target");
	}
	
	public void reset() { DOMHandle.execCommand(this, "customMethod.resetForm"); }
	public void submit() { DOMHandle.execCommand(this, "submit"); }
}
