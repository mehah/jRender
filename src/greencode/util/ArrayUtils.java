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
}
