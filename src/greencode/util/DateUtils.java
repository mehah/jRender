package greencode.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {
	private DateUtils() {}
	
	public static Date toDate(String date, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(date);
	}
	
	public static String toString(Date date) { return toString(date, "yyyy-MM-dd"); }	
	public static String toString(Date date, String pattern) { return new SimpleDateFormat(pattern).format(date); }
}
