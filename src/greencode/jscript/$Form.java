package greencode.jscript;

import greencode.exception.OperationNotAllowedException;
import greencode.kernel.LogMessage;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Part;

public final class $Form {
	private $Form() {}
	
	private static boolean registerFields(Class<?> Class, List<Field> fieldList) {
		if(Class.equals(Form.class))
			return false;
		
		for (Field field : GenericReflection.getDeclaredFields(Class)) {
			if(field.isAnnotationPresent(greencode.jscript.form.annotation.ElementValue.class))
			{
				Class<?> type = field.getType();
				if(type.isArray())
					type = type.getComponentType();
				
				if(!type.equals(Date.class) && !ClassUtils.isPrimitiveOrWrapper(type) && !type.equals(Part.class))
					throw new OperationNotAllowedException(LogMessage.getMessage("green-0028", field.getName(), Class.getSimpleName()));
					
				fieldList.add(field);
			}
		}
		
		return true;
	}
	
	public static Field[] processFields(Class<? extends Form> currentClass)
	{
		Field[] fields = GenericReflection.getDeclaredFieldsByConditionId(currentClass, "form:elements");
		if(fields == null)
		{			
			List<Field> fieldList = new ArrayList<Field>();
			
			registerFields(currentClass, fieldList);
			
			for (Class<?> Class : ClassUtils.getParents(currentClass)) {
				if(!registerFields(Class, fieldList))
					break;
			}
			
			fields = fieldList.toArray(new Field[fieldList.size()]);
			
			GenericReflection.registerDeclaredFields(currentClass, "form:elements", fields);
		}
		
		return fields;
	}
}
