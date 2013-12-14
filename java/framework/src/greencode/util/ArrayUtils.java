package greencode.util;

public final class ArrayUtils {
	private ArrayUtils() {}
	
	public static boolean like(String[] list1, Object[] list2)
	{
		for (int i = -1, s = list2.length; ++i < s;)
		{
			if(contains(list1, list2[i]))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean contains(Object[] list, Object value)
	{
		for (int i = -1, s = list.length; ++i < s;)
		{
			if(list[i].equals(value))
				return true;
		}
		
		return false;
	}
}
