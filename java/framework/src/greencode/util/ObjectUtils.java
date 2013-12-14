package greencode.util;

public final class ObjectUtils {
	public final static boolean isNumber(Object o) {
		if(o == null) return false;
		
		try {
			Double.parseDouble(String.valueOf(o));
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public final static boolean isBoolean(Object o) {
		if(o == null) return false;
		
		return String.valueOf(o) == "true";
	}
}
