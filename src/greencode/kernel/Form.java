package greencode.kernel;

import greencode.http.enumeration.RequestMethod;
import greencode.jscript.form.annotation.ConvertDateTime;
import greencode.util.ClassUtils;
import greencode.util.DateUtils;
import greencode.util.StringUtils;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.UnknownFormatConversionException;

import javax.servlet.ServletException;
import javax.servlet.http.Part;


final class Form {
	static void processRequestedForm(GreenContext context) throws IllegalArgumentException, IllegalAccessException, IllegalStateException, IOException, ServletException {
		String formName = context.request.getParameter("__requestedForm");
		if(formName != null) {
			final Class<? extends greencode.jscript.Form> formClass = Cache.forms.get(formName);
			if(formClass == null)
				Console.error(LogMessage.getMessage("green-0032", formName));
			
			context.requestedForm = context.currentWindow.document.forms(formClass);
			
			Field[] fields = greencode.jscript.$Form.processFields(formClass);
			
			final boolean METHOD_TYPE_IS_GET = context.getRequest().isMethod(RequestMethod.GET);
			
			for (Field f : fields) {	
				greencode.jscript.form.annotation.ElementValue element = f.getAnnotation(greencode.jscript.form.annotation.ElementValue.class);
				
				final String parametro = !element.name().isEmpty() ? element.name() : f.getName(); 
				
				Object valor = null;
				
				if(f.getType().equals(Part.class)) {
					f.set(context.requestedForm, GreenContext.getInstance().getRequest().getPart(parametro));
				}else if(f.getType().isArray()) {
					valor = context.request.getParameterValues(parametro+"[]");
					
					final String[] valores = (String[]) valor;
					if(valores != null) {
						final Object[] values = (Object[]) Array.newInstance(f.getType().getComponentType(), valores.length);
						
						try {
							for (int i = -1, l = valores.length; ++i < l;) {
								Object _value = greencode.kernel.Form.getFieldValue(f, f.getType().getComponentType(), valores[i]);
								
								if(_value == null)
									Console.error(LogMessage.getMessage("green-0019", f.getName(), f.getDeclaringClass().getSimpleName()));
								
								if(_value instanceof String) {
									if(element.trim())
										_value = ((String) _value).trim();										
										
									if(METHOD_TYPE_IS_GET)
										_value = StringUtils.toCharset((String) _value, GreenCodeConfig.View.charset);
								}
								
								values[i] = _value;
							}
							
							f.set(context.requestedForm, valor);
						} catch (UnknownFormatConversionException e) {
							Console.error(LogMessage.getMessage("green-0013", f.getName(), formClass.getSimpleName(), "Date", ConvertDateTime.class.getSimpleName()));
						}
					}
				}else
				{
					if(context.request.getParameterMap().containsKey(parametro)) {
						try {
							valor = greencode.kernel.Form.getFieldValue(f, f.getType(), context.request.getParameter(parametro));
							
							if(valor instanceof String) {
								if(element.trim())
									valor = ((String) valor).trim();										
									
								if(METHOD_TYPE_IS_GET)
									valor = StringUtils.toCharset((String) valor, GreenCodeConfig.View.charset);
							}
							
							f.set(context.requestedForm, valor);
						} catch (UnknownFormatConversionException e) {
							Console.error(LogMessage.getMessage("green-0013", f.getName(), formClass.getSimpleName(), "Date", ConvertDateTime.class.getSimpleName()));
						}
					}
				}
			}
		}
	}
	
	static Object getFieldValue(Field field, Class<?> instanceClass, final String valor) throws UnknownFormatConversionException {
		if(valor != null && !valor.isEmpty()) {
			instanceClass = ClassUtils.toWrapperClass(instanceClass);
			if(String.class.equals(instanceClass))
				return valor;
			if(Integer.class.equals(instanceClass))
				return Integer.parseInt(valor);
			if(Long.class.equals(instanceClass))
				return Long.parseLong(valor);
			if(Double.class.equals(instanceClass))
				return Double.parseDouble(valor);
			if(Float.class.equals(instanceClass))
				return Float.parseFloat(valor);
			if(Boolean.class.equals(instanceClass))
				return valor.equals("true");
			if(Character.class.equals(instanceClass))
				return valor.charAt(0);
			if(Byte.class.equals(instanceClass))
				return Byte.parseByte(valor);
			if(Short.class.equals(instanceClass))
				return Short.parseShort(valor);
			
			if(Date.class.equals(instanceClass)) {
				if(!field.isAnnotationPresent(ConvertDateTime.class))
					throw new UnknownFormatConversionException("green-0013");
				
				ConvertDateTime convert = field.getAnnotation(ConvertDateTime.class);
				
				try {  
					return DateUtils.toDate(valor, convert.pattern());
				} catch (ParseException e) {
					Console.warning(LogMessage.getMessage("green-0033", valor, convert.pattern()));
				}
			}else {
				/*return HttpParameter.Context.getObjectRequest(valor);*/
			}
		}
		
		return null;
	}
}
