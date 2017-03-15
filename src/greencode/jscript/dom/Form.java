package greencode.jscript.dom;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import greencode.exception.GreencodeError;
import greencode.http.enumeration.RequestMethod;
import greencode.jscript.DOMHandle;
import greencode.jscript.dom.annotation.QuerySelector;
import greencode.jscript.dom.elements.custom.ContainerElement;
import greencode.jscript.dom.elements.custom.implementation.ContainerElementImplementation;
import greencode.jscript.dom.form.annotation.ElementValue;
import greencode.jscript.dom.form.annotation.Event;
import greencode.jscript.dom.form.annotation.Name;
import greencode.jscript.dom.form.annotation.RegisterEvent;
import greencode.jscript.dom.form.annotation.Visible;
import greencode.kernel.GreenContext;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;
import greencode.util.GenericReflection.Condition;
import greencode.util.LogMessage;

public abstract class Form extends Element implements ContainerElementImplementation {
	final Field[] elementFields = $Container.processFields(getClass());
	Map<Integer, ContainerElement<?>> containers;

	private static final Condition<Field> fieldsWithRegisterEvent = new Condition<Field>() {
		public boolean init(Field arg0) {
			return arg0.isAnnotationPresent(RegisterEvent.class) && ClassUtils.isParent(arg0.getType(), EventTarget.class);
		}
	};

	private static final Condition<Field> fieldsWithFindElement = new Condition<Field>() {
		public boolean init(Field arg0) {
			return ClassUtils.isParent(arg0.getType(), Element.class) && (arg0.isAnnotationPresent(QuerySelector.class) || arg0.isAnnotationPresent(ElementValue.class));
		}
	};

	protected Form() {
		this(GreenContext.getInstance().currentWindow());
	}

	public Form(Window window) {
		super(window, "form");
		String name = getClass().getAnnotation(Name.class).value();

		Visible visibleAnnotation = getClass().getAnnotation(Visible.class);

		if(visibleAnnotation == null)
			DOMHandle.registerReturnByCommand(this, window.principalElement(), "querySelector", "form[name=\"" + name + "\"]");
		else
			DOMHandle.registerReturnByCommand(this, window.principalElement(), "querySelector", "form[name=\"" + name + "\"]", "return (this.offsetHeight " + (visibleAnnotation.value() ? "!" : "=") + "== 0);");
	}

	void processAnnotation() {
		Field[] fields = GenericReflection.getDeclaredFieldsByConditionId(getClass(), "form:fieldsWithFindElement");
		if(fields == null)
			fields = GenericReflection.getDeclaredFieldsByCondition(getClass(), "form:fieldsWithFindElement", fieldsWithFindElement, true);

		for(Field f: fields) {
			if(GenericReflection.NoThrow.getValue(f, this) != null)
				continue;

			QuerySelector a = f.getAnnotation(QuerySelector.class);
			Object v = null;
			Class<? extends Element> type = (Class<? extends Element>) f.getType();

			if(a != null) {
				Element context;
				searchContext: {
					if(!a.context().isEmpty()) {
						for(Field f2: fields) {
							if(f2.getName().equals(a.context())) {
								context = (Element) GenericReflection.NoThrow.getValue(f2, this);
								break searchContext;
							}
						}
						
						throw new GreencodeError(LogMessage.getMessage("green-0041", a.context(), f.getName(), getClass().getSimpleName()));
					}else
						context = this;
				}
				
				if(type.isArray()) {
					v = ElementHandle.cast(context.querySelectorAll(a.selector()), (Class<? extends Element>) type.getComponentType());
				} else {
					v = context.querySelector(a.selector(), type);
				}
			} else {
				if(!type.isArray()) {
					ElementValue aev = f.getAnnotation(ElementValue.class);
					String selector = "[name='" + (aev.name().isEmpty() ? f.getName() : aev.name()) + "']";
				
					Class<?> typeValue = null;
					
					if(greencode.jscript.dom.elements.$Element.isElementWithValue(type)) {
						typeValue = f.getGenericType() instanceof ParameterizedType ? (Class<?>)((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0] : String.class;						
					}
					
					v = this.querySelector(selector, type, typeValue);
				}

			}

			GenericReflection.NoThrow.setValue(f, v, this);
		}

		fields = GenericReflection.getDeclaredFieldsByConditionId(getClass(), "form:elementsWithRegisterEvent");
		if(fields == null)
			fields = GenericReflection.getDeclaredFieldsByCondition(getClass(), "form:elementsWithRegisterEvent", fieldsWithRegisterEvent);

		for(Field f: fields) {
			Event[] events = f.getAnnotation(RegisterEvent.class).value();
			EventTarget et = (EventTarget) GenericReflection.NoThrow.getValue(f, this);

			if(et == null)
				throw new RuntimeException(LogMessage.getMessage("green-0036", f.getName(), getClass().getSimpleName()));

			for(Event e: events) {
				FunctionHandle fh = new FunctionHandle(e.windowAction(), e.method());
				fh.setRequestMethod(e.requestMethod());

				for(String n: e.name())
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

	public void enctype(String enctype) {
		DOMHandle.setProperty(this, "enctype", enctype);
	}

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

	public void name(String name) {
		DOMHandle.setProperty(this, "name", name);
	}

	public String name() {
		return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name");
	}

	public void target(String target) {
		DOMHandle.setProperty(this, "target", target);
	}

	public String target() {
		return DOMHandle.getVariableValueByProperty(this, "target", String.class, "target");
	}

	public void reset() {
		Field[] fields = greencode.jscript.dom.$Container.getElementFields(this);
		try {
			for(Field f: fields) {
				Class<?> fieldType = f.getType();
				
				if(greencode.jscript.dom.elements.$Element.isElementWithValue(fieldType))
					DOMHandle.setVariableValue((Element) f.get(this), "value", null);
				else
					f.set(this, ClassUtils.getDefaultValue(fieldType));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		DOMHandle.execCommand(this, "resetForm");
	}

	public void submit() {
		DOMHandle.execCommand(this, "submit");
	}

	public void fill() {
		greencode.jscript.dom.$Container.fill(this, elementFields);
	}
}