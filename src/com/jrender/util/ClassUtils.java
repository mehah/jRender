package com.jrender.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class ClassUtils {
	private ClassUtils() {}
	
	private static final HashMap<Class<?>, Class<?>[]> cachedClass = new HashMap<Class<?>, Class<?>[]>();
	private static final HashMap<Class<?>, HashMap<Class<?>, Class<?>[]>> cachedClassWithDelimiter = new HashMap<Class<?>, HashMap<Class<?>, Class<?>[]>>();
	private static final HashMap<Class<?>, HashSet<Class<?>>> parentsClass = new HashMap<Class<?>, HashSet<Class<?>>>();
	private static final HashSet<Class<?>> wrapperTypes = new HashSet<Class<?>>();
	private static final HashSet<Class<?>> primitivesTypes = new HashSet<Class<?>>();
	
	static{
		wrapperTypes.add(String.class);
		wrapperTypes.add(Boolean.class);
		wrapperTypes.add(Character.class);
		wrapperTypes.add(Byte.class);
		wrapperTypes.add(Short.class);
		wrapperTypes.add(Integer.class);
		wrapperTypes.add(Long.class);
		wrapperTypes.add(Float.class);
		wrapperTypes.add(Double.class);
		wrapperTypes.add(Void.class);
		
		primitivesTypes.add(boolean.class);
		primitivesTypes.add(char.class);
		primitivesTypes.add(byte.class);
		primitivesTypes.add(short.class);
		primitivesTypes.add(int.class);
		primitivesTypes.add(long.class);
		primitivesTypes.add(float.class);
		primitivesTypes.add(double.class);
		primitivesTypes.add(void.class);
	}
	
	public final static Class<?> toWrapperClass(Class<?> c) {
		if(c.equals(boolean.class))
			return Boolean.class;
		
		if(c.equals(char.class))
			return Character.class;
		
		if(c.equals(byte.class))
			return Byte.class;
		
		if(c.equals(short.class))
			return Short.class;
		
		if(c.equals(int.class))
			return Integer.class;
		
		if(c.equals(long.class))
			return Long.class;
		
		if(c.equals(float.class))
			return Float.class;
		
		if(c.equals(double.class))
			return Double.class;
		
		if(c.equals(void.class))
			return Void.class;
		
		return c;
	}
	
	public final static boolean isPrimitiveOrWrapper(Class<?> c) { return primitivesTypes.contains(c) || wrapperTypes.contains(c); }
	
	public final static boolean isWrapperType(Class<?> c) { return wrapperTypes.contains(c); }
	
	public final static boolean isPrimitiveType(Class<?> c) { return primitivesTypes.contains(c); }
	
	public final static Object getDefaultValue(Class<?> c) {
		if(c.equals(byte.class) || c.equals(short.class) || c.equals(int.class))
			return 0;
		if(c.equals(long.class))
			return 0L;
		if(c.equals(float.class))
			return 0.0f;
		if(c.equals(double.class))
			return 0.0d;
		if(c.equals(char.class))
			return '\u0000';
		if(c.equals(boolean.class))
			return false;

		return null;
	}
	
	public final static boolean isParent(Class<?> clazz, Class<?> parentClass) {
		if(clazz.isArray())
			clazz = clazz.getComponentType();
		
		HashSet<Class<?>> parents = parentsClass.get(clazz);
		
		if(parents == null) {
			parentsClass.put(clazz, parents = new HashSet<Class<?>>());
			
			boolean isParent = false;
			
			Class<?> parent = clazz;
			while((parent = parent.getSuperclass()) != Object.class && parent != null) {
				if(!isParent && parent.equals(parentClass))
					isParent = true;
				
				parents.add(parent);
			}
			
			return isParent;
		}
		
		return parents.contains(parentClass);
	}
	
	public static Class<?>[] getParents(final Class<?> Class) { return getParents(Class, null); }
	
	public static Class<?>[] getParents(final Class<?> Class, Class<?> delimiter) { return getParents(Class, delimiter, null); }
	
	public static<E> Class<E>[] getParents(final Class<?> clazz, Class<?> delimiter, Class<E> cast) {
		HashMap<Class<?>, Class<?>[]> classHash;
		final Class<?> index;
		if(delimiter != null) {
			if((classHash = cachedClassWithDelimiter.get(clazz)) == null)
				cachedClassWithDelimiter.put(clazz, classHash = new HashMap<Class<?>, Class<?>[]>());
			
			index = delimiter;
		}else {
			classHash = cachedClass;
			delimiter = Object.class;
			index = clazz;
		}
		
		Class<?>[] classes;
		if((classes = classHash.get(index)) == null) {
			final List<Class<?>> _classes = new ArrayList<Class<?>>();
			
			Class<?> parent = clazz;
			while((parent = parent.getSuperclass()) != delimiter && parent != null)
				_classes.add(parent);
			
			classes = _classes.toArray(new Class<?>[_classes.size()]);			
			classHash.put(index, classes);	
		}
		
		return (Class<E>[]) classes;
	}
}