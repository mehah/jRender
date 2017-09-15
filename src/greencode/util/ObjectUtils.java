package greencode.util;

public final class ObjectUtils {
	public final static boolean isNumber(Object o) {
		if(o == null) return false;
		
		try {
			Double.parseDouble(String.valueOf(o));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public final static boolean isBoolean(Object o) { return (o != null) && (String.valueOf(o).equals("true") || String.valueOf(o).equals("false")); }
}
