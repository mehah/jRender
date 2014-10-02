package greencode.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

public final class StringUtils {
	public final static String toCharset(String value, String toCharset) throws UnsupportedEncodingException {
		return new String(value.getBytes("iso-8859-1"), toCharset);
	}
	
	public final static String toCharset(String value, String originalCharset, String toCharset) throws UnsupportedEncodingException {
		return new String(value.getBytes(originalCharset), toCharset);
	}
	
	public final static String[] splitTrim(String arg0, String arg1) {
		String[] list = arg0.split(arg1);
		
		final int c = list.length;
		for(int i = -1; ++i < c;)
			list[i] = list[i].trim();
		
		return list;
	}
	
	public static boolean isEmpty(String str) { return str == null || str.length() == 0; }
	
	/* Java Commons */
	
	/**
	 * The empty String {@code ""}.
	 * @since 2.0
	 */
	public static final String EMPTY = "";
	
	/**
	 * Represents a failed index search.
	 * @since 2.1
	 */
	public static final int INDEX_NOT_FOUND = -1;
	
	/**
	 * <p>Replaces all occurrences of a String within another String.</p>
	 *
	 * <p>A {@code null} reference passed to this method is a no-op.</p>
	 *
	 * <pre>
	 * StringUtils.replace(null, *, *)			= null
	 * StringUtils.replace("", *, *)			= ""
	 * StringUtils.replace("any", null, *)		= "any"
	 * StringUtils.replace("any", *, null)		= "any"
	 * StringUtils.replace("any", "", *)		= "any"
	 * StringUtils.replace("aba", "a", null)	= "aba"
	 * StringUtils.replace("aba", "a", "")		= "b"
	 * StringUtils.replace("aba", "a", "z")		= "zbz"
	 * </pre>
	 *
	 * @see #replace(String text, String searchString, String replacement, int max)
	 * @param text  text to search and replace in, may be null
	 * @param searchString  the String to search for, may be null
	 * @param replacement  the String to replace it with, may be null
	 * @return the text with any replacements processed,
	 *  {@code null} if null String input
	 */
	public static String replace(String text, String searchString, String replacement) {
		return replace(text, searchString, replacement, -1);
	}

	/**
	 * <p>Replaces a String with another String inside a larger String,
	 * for the first {@code max} values of the search String.</p>
	 *
	 * <p>A {@code null} reference passed to this method is a no-op.</p>
	 *
	 * <pre>
	 * StringUtils.replace(null, *, *, *)		 = null
	 * StringUtils.replace("", *, *, *)		   = ""
	 * StringUtils.replace("any", null, *, *)	 = "any"
	 * StringUtils.replace("any", *, null, *)	 = "any"
	 * StringUtils.replace("any", "", *, *)	   = "any"
	 * StringUtils.replace("any", *, *, 0)		= "any"
	 * StringUtils.replace("abaa", "a", null, -1) = "abaa"
	 * StringUtils.replace("abaa", "a", "", -1)   = "b"
	 * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
	 * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
	 * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
	 * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
	 * </pre>
	 *
	 * @param text  text to search and replace in, may be null
	 * @param searchString  the String to search for, may be null
	 * @param replacement  the String to replace it with, may be null
	 * @param max  maximum number of values to replace, or {@code -1} if no maximum
	 * @return the text with any replacements processed,
	 *  {@code null} if null String input
	 */
	public static String replace(String text, String searchString, String replacement, int max) {
		if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) return text;
		int start = 0, end = text.indexOf(searchString, start);
		if (end == INDEX_NOT_FOUND) return text;
		int replLength = searchString.length(), increase = replacement.length() - replLength;
		
		increase = increase < 0 ? 0 : increase;
		increase *= max < 0 ? 16 : max > 64 ? 64 : max;
		StringBuilder buf = new StringBuilder(text.length() + increase);
		while (end != INDEX_NOT_FOUND) {
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			if (--max == 0) break;
			end = text.indexOf(searchString, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}
	
	 // Joining
	//-----------------------------------------------------------------------
	/**
	 * <p>Joins the elements of the provided array into a single String
	 * containing the provided list of elements.</p>
	 *
	 * <p>No separator is added to the joined String.
	 * Null objects or empty strings within the array are represented by
	 * empty strings.</p>
	 *
	 * <pre>
	 * StringUtils.join(null)			= null
	 * StringUtils.join([])			  = ""
	 * StringUtils.join([null])		  = ""
	 * StringUtils.join(["a", "b", "c"]) = "abc"
	 * StringUtils.join([null, "", "a"]) = "a"
	 * </pre>
	 *
	 * @param <T> the specific type of values to join together
	 * @param elements  the values to join together, may be null
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 * @since 3.0 Changed signature to use varargs
	 */
	public static <T> String join(T... elements) { return join(elements, null); }

	/**
	 * <p>Joins the elements of the provided array into a single String
	 * containing the provided list of elements.</p>
	 *
	 * <p>No delimiter is added before or after the list.
	 * Null objects or empty strings within the array are represented by
	 * empty strings.</p>
	 *
	 * <pre>
	 * StringUtils.join(null, *)			   = null
	 * StringUtils.join([], *)				 = ""
	 * StringUtils.join([null], *)			 = ""
	 * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
	 * StringUtils.join(["a", "b", "c"], null) = "abc"
	 * StringUtils.join([null, "", "a"], ';')  = ";;a"
	 * </pre>
	 *
	 * @param array  the array of values to join together, may be null
	 * @param separator  the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 */
	public static String join(Object[] array, char separator) {
		if (array == null) return null;
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>Joins the elements of the provided array into a single String
	 * containing the provided list of elements.</p>
	 *
	 * <p>No delimiter is added before or after the list.
	 * Null objects or empty strings within the array are represented by
	 * empty strings.</p>
	 *
	 * <pre>
	 * StringUtils.join(null, *)			   = null
	 * StringUtils.join([], *)				 = ""
	 * StringUtils.join([null], *)			 = ""
	 * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
	 * StringUtils.join(["a", "b", "c"], null) = "abc"
	 * StringUtils.join([null, "", "a"], ';')  = ";;a"
	 * </pre>
	 *
	 * @param array  the array of values to join together, may be null
	 * @param separator  the separator character to use
	 * @param startIndex the first index to start joining from.  It is
	 * an error to pass in an end index past the end of the array
	 * @param endIndex the index to stop joining from (exclusive). It is
	 * an error to pass in an end index past the end of the array
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 */
	public static String join(Object[] array, char separator, int startIndex, int endIndex) {
		if (array == null) return null; 
		int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) return EMPTY;
		
		StringBuilder buf = new StringBuilder(noOfItems * 16);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) buf.append(separator);
			if (array[i] != null) buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * <p>Joins the elements of the provided array into a single String
	 * containing the provided list of elements.</p>
	 *
	 * <p>No delimiter is added before or after the list.
	 * A {@code null} separator is the same as an empty String ("").
	 * Null objects or empty strings within the array are represented by
	 * empty strings.</p>
	 *
	 * <pre>
	 * StringUtils.join(null, *)				= null
	 * StringUtils.join([], *)				  = ""
	 * StringUtils.join([null], *)			  = ""
	 * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
	 * StringUtils.join(["a", "b", "c"], null)  = "abc"
	 * StringUtils.join(["a", "b", "c"], "")	= "abc"
	 * StringUtils.join([null, "", "a"], ',')   = ",,a"
	 * </pre>
	 *
	 * @param array  the array of values to join together, may be null
	 * @param separator  the separator character to use, null treated as ""
	 * @return the joined String, {@code null} if null array input
	 */
	public static String join(Object[] array, String separator) {
		if (array == null) return null;
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>Joins the elements of the provided array into a single String
	 * containing the provided list of elements.</p>
	 *
	 * <p>No delimiter is added before or after the list.
	 * A {@code null} separator is the same as an empty String ("").
	 * Null objects or empty strings within the array are represented by
	 * empty strings.</p>
	 *
	 * <pre>
	 * StringUtils.join(null, *)				= null
	 * StringUtils.join([], *)					= ""
	 * StringUtils.join([null], *)				= ""
	 * StringUtils.join(["a", "b", "c"], "--")	= "a--b--c"
	 * StringUtils.join(["a", "b", "c"], null)	= "abc"
	 * StringUtils.join(["a", "b", "c"], "")	= "abc"
	 * StringUtils.join([null, "", "a"], ',')	= ",,a"
	 * </pre>
	 *
	 * @param array  the array of values to join together, may be null
	 * @param separator  the separator character to use, null treated as ""
	 * @param startIndex the first index to start joining from.  It is
	 * an error to pass in an end index past the end of the array
	 * @param endIndex the index to stop joining from (exclusive). It is
	 * an error to pass in an end index past the end of the array
	 * @return the joined String, {@code null} if null array input
	 */
	public static String join(Object[] array, String separator, int startIndex, int endIndex) {
		if (array == null) return null;
		if (separator == null) separator = EMPTY;

		// endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
		//		   (Assuming that all Strings are roughly equally long)
		int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) return EMPTY;

		StringBuilder buf = new StringBuilder(noOfItems * 16);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) buf.append(separator);
			if (array[i] != null) buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * <p>Joins the elements of the provided {@code Iterator} into
	 * a single String containing the provided elements.</p>
	 *
	 * <p>No delimiter is added before or after the list. Null objects or empty
	 * strings within the iteration are represented by empty strings.</p>
	 *
	 * <p>See the examples here: {@link #join(Object[],char)}. </p>
	 *
	 * @param iterator  the {@code Iterator} of values to join together, may be null
	 * @param separator  the separator character to use
	 * @return the joined String, {@code null} if null iterator input
	 * @since 2.0
	 */
	public static String join(Iterator<?> iterator, char separator) {

		// handle null, zero and one elements before building a buffer
		if (iterator == null) return null;
		if (!iterator.hasNext()) return EMPTY;
		Object first = iterator.next();
		if (!iterator.hasNext()) return first == null ? "" : first.toString();
		
		// two or more elements
		StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
		if (first != null) buf.append(first);

		while (iterator.hasNext()) {
			buf.append(separator);
			Object obj = iterator.next();
			if (obj != null) buf.append(obj);
		}

		return buf.toString();
	}

	/**
	 * <p>Joins the elements of the provided {@code Iterator} into
	 * a single String containing the provided elements.</p>
	 *
	 * <p>No delimiter is added before or after the list.
	 * A {@code null} separator is the same as an empty String ("").</p>
	 *
	 * <p>See the examples here: {@link #join(Object[],String)}. </p>
	 *
	 * @param iterator  the {@code Iterator} of values to join together, may be null
	 * @param separator  the separator character to use, null treated as ""
	 * @return the joined String, {@code null} if null iterator input
	 */
	public static String join(Iterator<?> iterator, String separator) {

		// handle null, zero and one elements before building a buffer
		if (iterator == null) return null;
		if (!iterator.hasNext()) return EMPTY;
		Object first = iterator.next();
		if (!iterator.hasNext()) return first == null ? "" : first.toString();

		// two or more elements
		StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
		if (first != null) buf.append(first);

		while (iterator.hasNext()) {
			if (separator != null) buf.append(separator);
			Object obj = iterator.next();
			if (obj != null) buf.append(obj);
		}
		return buf.toString();
	}

	/**
	 * <p>Joins the elements of the provided {@code Iterable} into
	 * a single String containing the provided elements.</p>
	 *
	 * <p>No delimiter is added before or after the list. Null objects or empty
	 * strings within the iteration are represented by empty strings.</p>
	 *
	 * <p>See the examples here: {@link #join(Object[],char)}. </p>
	 *
	 * @param iterable  the {@code Iterable} providing the values to join together, may be null
	 * @param separator  the separator character to use
	 * @return the joined String, {@code null} if null iterator input
	 * @since 2.3
	 */
	public static String join(Iterable<?> iterable, char separator) {
		if (iterable == null) return null;
		return join(iterable.iterator(), separator);
	}

	/**
	 * <p>Joins the elements of the provided {@code Iterable} into
	 * a single String containing the provided elements.</p>
	 *
	 * <p>No delimiter is added before or after the list.
	 * A {@code null} separator is the same as an empty String ("").</p>
	 *
	 * <p>See the examples here: {@link #join(Object[],String)}. </p>
	 *
	 * @param iterable  the {@code Iterable} providing the values to join together, may be null
	 * @param separator  the separator character to use, null treated as ""
	 * @return the joined String, {@code null} if null iterator input
	 * @since 2.3
	 */
	public static String join(Iterable<?> iterable, String separator) {
		if (iterable == null) return null;
		return join(iterable.iterator(), separator);
	}
}
