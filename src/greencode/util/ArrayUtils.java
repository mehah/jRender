package greencode.util;

public final class ArrayUtils {
	private ArrayUtils() {}
	
	public static boolean like(String[] list1, Object[] list2) {
		for (Object o : list2) if(contains(list1, o))
				return true;
		
		return false;
	}
	
	public static boolean contains(Object[] list, Object value) {
		for (Object o : list) if(o.equals(value))
				return true;
		
		return false;
	}
	
	public static Object wrapperToPrimitive(Object[] list) {
		if(list instanceof Boolean[]) {
			boolean[] array = new boolean[list.length];
			for(int i = -1; ++i < array.length;) {
				array[i] = ((Boolean)list[i]).booleanValue();
			}
			return array;
		}else if(list instanceof Character[]) {
			char[] array = new char[list.length];
			for(int i = -1; ++i < array.length;) {
				array[i] = ((Character)list[i]).charValue();
			}
			return array;
		}else if(list instanceof Byte[]) {
			byte[] array = new byte[list.length];
			for(int i = -1; ++i < array.length;) {
				array[i] = ((Byte)list[i]).byteValue();
			}
			return array;
		}else if(list instanceof Short[]) {
			short[] array = new short[list.length];
			for(int i = -1; ++i < array.length;) {
				array[i] = ((Short)list[i]).shortValue();
			}
			return array;
		}else if(list instanceof Integer[]) {
			int[] array = new int[list.length];
			for(int i = -1; ++i < array.length;) {
				array[i] = ((Integer)list[i]).intValue();
			}
			return array;
		}else if(list instanceof Long[]) {
			long[] array = new long[list.length];
			for(int i = -1; ++i < array.length;) {
				array[i] = ((Long)list[i]).longValue();
			}
			return array;
		}else if(list instanceof Float[]) {
			float[] array = new float[list.length];
			for(int i = -1; ++i < array.length;) {
				array[i] = ((Float)list[i]).floatValue();
			}
			return array;
		}else if(list instanceof Double[]) {
			double[] array = new double[list.length];
			for(int i = -1; ++i < array.length;) {
				array[i] = ((Double)list[i]).doubleValue();
			}
			return array;
		}
		return null;
	}
}
