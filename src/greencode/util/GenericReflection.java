package greencode.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

public final class GenericReflection {
	private static final Hashtable<Class<?>, Hashtable<Integer, Constructor<?>>> cacheConstructor = new Hashtable<Class<?>, Hashtable<Integer,Constructor<?>>>();
	private static final Hashtable<Class<?>, Hashtable<Integer, Constructor<?>>> cacheDeclaredConstructor = new Hashtable<Class<?>, Hashtable<Integer,Constructor<?>>>();
	private static final Hashtable<Class<?>, Method[]> cacheMethods = new Hashtable<Class<?>, Method[]>();
	private static final Hashtable<Class<?>, Field[]> cacheFields = new Hashtable<Class<?>, Field[]>();
	private static final Hashtable<Class<?>, Hashtable<String, Field[]>> cacheConditionFields = new Hashtable<Class<?>, Hashtable<String, Field[]>>();
	private static final Hashtable<Class<?>, Hashtable<String, Field>> cacheField = new Hashtable<Class<?>, Hashtable<String, Field>>();
	private static final Hashtable<Class<?>, Hashtable<String, Method>> cacheDeclaredMethods = new Hashtable<Class<?>, Hashtable<String,Method>>();
	private static final Hashtable<Class<?>, Hashtable<String, Hashtable<Integer, Method>>> cacheMethod = new Hashtable<Class<?>, Hashtable<String,Hashtable<Integer,Method>>>();
	private static final Hashtable<Class<?>, HashSet<Class<?>>> interfaces = new Hashtable<Class<?>, HashSet<Class<?>>>(); 

	private GenericReflection() {}
	
	public static Object getValue(Class<?> Class, String nameField, Object reference) {
		try {
			return getDeclaredField(Class, nameField).get(reference);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void setValue(Class<?> Class, String nameField, Object value, Object reference) {
		try {
			getDeclaredField(Class, nameField).set(reference, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Field getDeclaredField(Class<?> Class, String fieldName) throws SecurityException, NoSuchFieldException {
		Hashtable<String, Field> fields = cacheField.get(Class);
		
		if(fields == null)
			cacheField.put(Class, fields = new Hashtable<String, Field>());
		
		Field field = fields.get(fieldName);
		
		if(field == null) {
			(field = Class.getDeclaredField(fieldName)).setAccessible(true);
			fields.put(fieldName, field);
		}
		
		return field;
	}
	
	public static Field[] getDeclaredFields(Class<?> Class) {
		Field[] fields = cacheFields.get(Class);
		
		if(fields == null) {
			fields = Class.getDeclaredFields();
			for (Field f : fields)
				f.setAccessible(true);
			
			cacheFields.put(Class, fields);
		}
		
		return fields;
	}
	
	public static void registerDeclaredFields(Class<?> Class, String id, Field[] fields) {
		Hashtable<String, Field[]> hashFields = cacheConditionFields.get(Class);
		if(hashFields == null)
			cacheConditionFields.put(Class, hashFields = new Hashtable<String, Field[]>());
		
		hashFields.put(id, fields);
	}
	
	public static Field[] getDeclaredFieldsByConditionId(Class<?> Class, String id) {
		Hashtable<String, Field[]> hashFields = cacheConditionFields.get(Class);
		if(hashFields != null)
			return hashFields.get(id);
		
		return null;
	}
	
	public static Field[] getDeclaredFieldsByCondition(Class<?> Class, String id, Condition<Field> condition) {
		return getDeclaredFieldsByCondition(Class, id, condition, false);
	}
	
	public static Field[] getDeclaredFieldsByCondition(Class<?> Class, String id, Condition<Field> condition, boolean considerParents)
	{
		final ArrayList<Field> _fields = new ArrayList<Field>();
		
		Field[] fields = getDeclaredFields(Class);
				
		for (Field field : fields) if(condition.init(field))
				_fields.add(field);
		
		if(considerParents) {
			final Class<?>[] parents = ClassUtils.getParents(Class);
			for (Class<?> parent : parents) {
				fields = getDeclaredFields(parent);				
				for (Field field : fields) {
					if(condition.init(field))
						_fields.add(field);
				}
			}
		}
		
		fields = _fields.toArray(new Field[_fields.size()]);		
		registerDeclaredFields(Class, id, fields);
		
		return fields;
	}
	
	public static Method getDeclaredMethod(Class<?> Class, String methodName, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException
	{
		Hashtable<String, Method> methods = cacheDeclaredMethods.get(Class);
		
		if(methods == null)
			cacheDeclaredMethods.put(Class, methods = new Hashtable<String, Method>());
		
		Method method = methods.get(methodName);
		
		if(method == null) {
			(method = Class.getDeclaredMethod(methodName, parameterTypes)).setAccessible(true);
			methods.put(methodName, method);
		}
		
		return method;
	}
	
	public static Method getMethod(Class<?> Class, String methodName, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException
	{
		Hashtable<String, Hashtable<Integer, Method>> methods = cacheMethod.get(Class);
		
		if(methods == null)
			cacheMethod.put(Class, methods = new Hashtable<String, Hashtable<Integer,Method>>());
		
		Hashtable<Integer, Method> methodArgs = methods.get(methodName);
		
		if(methodArgs == null)
			methods.put(methodName, methodArgs = new Hashtable<Integer, Method>());
		
		final int hashCode = Arrays.hashCode(parameterTypes);
		
		Method method = methodArgs.get(hashCode);
		
		if(method == null) {
			(method = Class.getMethod(methodName, parameterTypes)).setAccessible(true);
			methodArgs.put(hashCode, method);
		}
		
		return method;
	}
	
	public static Method[] getDeclaredMethods(Class<?> Class)
	{
		Method[] methods = cacheMethods.get(Class);
		
		if(methods == null)
			cacheMethods.put(Class, methods = Class.getDeclaredMethods());
		
		return methods;
	}
	
	public static void setFinalAccessible(Field field) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
	    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}
	
	@SuppressWarnings("unchecked")
	public static<C> Constructor<C> getConstrutor(Class<C> Class, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException
	{
		final int hashcode = Arrays.hashCode(parameterTypes);
		
		Hashtable<Integer, Constructor<?>> constructors = cacheConstructor.get(Class);
		
		if(constructors == null)
			cacheConstructor.put(Class, constructors = new Hashtable<Integer, Constructor<?>>());
		
		Constructor<C> constructor = (Constructor<C>) constructors.get(hashcode);
		
		if(constructor == null) {
			(constructor = Class.getConstructor(parameterTypes)).setAccessible(true);
			constructors.put(hashcode, constructor);
		}
		
		return constructor;
	}
	
	@SuppressWarnings("unchecked")
	public static<C> Constructor<C> getDeclaredConstrutor(Class<C> Class, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException
	{
		final int hashcode = Arrays.hashCode(parameterTypes);
		
		Hashtable<Integer, Constructor<?>> constructors = cacheDeclaredConstructor.get(Class);
		
		if(constructors == null)
			cacheDeclaredConstructor.put(Class, constructors = new Hashtable<Integer, Constructor<?>>());
		
		Constructor<C> constructor = (Constructor<C>) constructors.get(hashcode);
		
		if(constructor == null) {
			(constructor = Class.getDeclaredConstructor(parameterTypes)).setAccessible(true);
			constructors.put(hashcode, constructor);
		}
		
		return constructor;
	}
	
	public static boolean hasInterface(Class<?> Class, Class<?> Interface)
	{
		HashSet<Class<?>> interfs = interfaces.get(Class);
		if(interfs == null)
			interfaces.put(Class, interfs = new HashSet<Class<?>>(Arrays.asList(Class.getInterfaces())));
		
		return interfs.contains(Interface);
	}
	
	public static class NoThrow {
		
		public static Object getValue(Field field, Object reference) {
			try {
				return field.get(reference);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static void setFinalStaticValue(Class<?> c, String fieldName, Object value) {
			setFinalValue(c, fieldName, value, null);
		}
		
		public static void setFinalValue(Class<?> c, String fieldName, Object value, Object reference) {
			GenericReflection.NoThrow.setFinalValue(GenericReflection.NoThrow.getDeclaredField(c, fieldName), value, reference);
		}
		
		public static void setFinalValue(Field field, Object value, Object reference) {
			try {
				setFinalAccessible(field);
				field.set(reference, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static void setValue(Field field, Object value, Object reference) {
			try {
				field.set(reference, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static Field getDeclaredField(Class<?> Class, String fieldName) {
			try {
				return GenericReflection.getDeclaredField(Class, fieldName);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	
		public static<C> Constructor<C> getConstrutor(Class<C> Class, Class<?>... parameterTypes) {
			try {
				return GenericReflection.getConstrutor(Class, parameterTypes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static<C> Constructor<C> getDeclaredConstrutor(Class<C> Class, Class<?>... parameterTypes) {
			try {
				return GenericReflection.getDeclaredConstrutor(Class, parameterTypes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static Method getMethod(Class<?> Class, String methodName, Class<?>... parameterTypes) {
			try {
				return GenericReflection.getMethod(Class, methodName, parameterTypes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static Method getDeclaredMethod(Class<?> Class, String methodName, Class<?>... parameterTypes) {
			try {
				return GenericReflection.getDeclaredMethod(Class, methodName, parameterTypes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static Object newInstance(String className, Class<?>[] parameterTypes, Object... objects) {
			try {
				return newInstance(Class.forName(className), parameterTypes, objects);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public static<C> C newInstance(Class<C> _class, Class<?>[] parameterTypes, Object... objects) {
			try {
				return getDeclaredConstrutor(_class, parameterTypes).newInstance(objects);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public static void setFinalAccessible(Field field) {
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
			    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public interface Condition<R extends AccessibleObject> {
		public abstract boolean init(R arg0);
	}
}
