package greencode.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {
	private DateUtils() {}
	
	public static Date toDate(String date, String pattern) throws ParseException
	{
        DateFormat formatter = new SimpleDateFormat(pattern);  
        return formatter.parse(date);
	}
	
	public static String toString(Date date)
	{
		return toString(date, "yyyy-MM-dd");
	}
	
	public static String toString(Date date, String pattern)
	{
		SimpleDateFormat formatador = new SimpleDateFormat(pattern);  
		  
		return formatador.format(date);
	}
}
