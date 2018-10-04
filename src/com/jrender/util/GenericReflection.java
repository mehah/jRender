package com.jrender.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GenericReflection {
	private static final Map<Class<?>, Map<Integer, Constructor<?>>> cacheConstructor = new ConcurrentHashMap<Class<?>, Map<Integer,Constructor<?>>>();
	private static final Map<Class<?>, Map<Integer, Constructor<?>>> cacheDeclaredConstructor = new ConcurrentHashMap<Class<?>, Map<Integer,Constructor<?>>>();
	private static final Map<Class<?>, Method[]> cacheMethods = new ConcurrentHashMap<Class<?>, Method[]>();
	private static final Map<Class<?>, Field[]> cacheFields = new ConcurrentHashMap<Class<?>, Field[]>();
	private static final Map<Class<?>, Map<String, Field[]>> cacheConditionFields = new ConcurrentHashMap<Class<?>, Map<String, Field[]>>();
	private static final Map<Class<?>, Map<String, Field>> cacheField = new ConcurrentHashMap<Class<?>, Map<String, Field>>();
	private static final Map<Class<?>, Map<String, Method>> cacheDeclaredMethods = new ConcurrentHashMap<Class<?>, Map<String,Method>>();
	private static final Map<Class<?>, Map<String, Map<Integer, Method>>> cacheMethod = new ConcurrentHashMap<Class<?>, Map<String,Map<Integer,Method>>>();
	private static final Map<Class<?>, HashSet<Class<?>>> interfaces = new ConcurrentHashMap<Class<?>, HashSet<Class<?>>>(); 

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
		Map<String, Field> fields = cacheField.get(Class);
		
		if(fields == null)
			cacheField.put(Class, fields = new ConcurrentHashMap<String, Field>());
		
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
		Map<String, Field[]> hashFields = cacheConditionFields.get(Class);
		if(hashFields == null)
			cacheConditionFields.put(Class, hashFields = new ConcurrentHashMap<String, Field[]>());
		
		hashFields.put(id, fields);
	}
	
	public static Field[] getDeclaredFieldsByConditionId(Class<?> Class, String id) {
		Map<String, Field[]> hashFields = cacheConditionFields.get(Class);
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
		Map<String, Method> methods = cacheDeclaredMethods.get(Class);
		
		if(methods == null)
			cacheDeclaredMethods.put(Class, methods = new ConcurrentHashMap<String, Method>());
		
		Method method = methods.get(methodName);
		
		if(method == null) {
			(method = Class.getDeclaredMethod(methodName, parameterTypes)).setAccessible(true);
			methods.put(methodName, method);
		}
		
		return method;
	}
	
	public static Method getMethod(Class<?> Class, String methodName, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException
	{
		Map<String, Map<Integer, Method>> methods = cacheMethod.get(Class);
		
		if(methods == null)
			cacheMethod.put(Class, methods = new ConcurrentHashMap<String, Map<Integer,Method>>());
		
		Map<Integer, Method> methodArgs = methods.get(methodName);
		
		if(methodArgs == null)
			methods.put(methodName, methodArgs = new ConcurrentHashMap<Integer, Method>());
		
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
		
		Map<Integer, Constructor<?>> constructors = cacheConstructor.get(Class);
		
		if(constructors == null)
			cacheConstructor.put(Class, constructors = new ConcurrentHashMap<Integer, Constructor<?>>());
		
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
		
		Map<Integer, Constructor<?>> constructors = cacheDeclaredConstructor.get(Class);
		
		if(constructors == null)
			cacheDeclaredConstructor.put(Class, constructors = new ConcurrentHashMap<Integer, Constructor<?>>());
		
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
	
	public static Enum<?> getEnumValue(Class<?> clazz, String name) {
		if(name == null) {
			return null;
		}
		
		Object[] enums = clazz.getEnumConstants();
		for (int i = -1, s = enums.length; ++i < s;) {
			Enum<?> _enum = (Enum<?>) enums[i];
			if(_enum.name().equals(name)) {
				return _enum;
			}
		}
		
		return null;
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
		
		public static Object invokeMethod(Class<?> Class, String methodName, Object... objs) {
			try {
				Class<?>[] parameters = new Class<?>[objs.length];
				for (int i = -1, s = parameters.length; ++i < s;) {
					parameters[i] = objs[i].getClass();
				}
				
				return GenericReflection.getMethod(Class, methodName, parameters).invoke(objs);
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
		
		public static Object invakeDeclaredMethod(Class<?> Class, String methodName, Object... objs) {
			try {
				Class<?>[] parameters = new Class<?>[objs.length];
				for (int i = -1, s = parameters.length; ++i < s;) {
					parameters[i] = objs[i].getClass();
				}
				
				return GenericReflection.getDeclaredMethod(Class, methodName, parameters).invoke(objs);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static Object newInstance(String className, Class<?>[] parameterTypes, Object... objects) {
			try {
				return newInstance(Class.forName(className), parameterTypes, objects);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		
		public static<C> C newInstance(Class<C> _class, Class<?>[] parameterTypes, Object... objects) {
			try {
				return getDeclaredConstrutor(_class, parameterTypes).newInstance(objects);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static void setFinalAccessible(Field field) {
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public interface Condition<R extends AccessibleObject> {
		public abstract boolean init(R arg0);
	}
}
