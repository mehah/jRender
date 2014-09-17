package greencode.jscript.elements.attribute;

public enum HttpEquivAttr {
	CONTENT_TYPE("content-type"), DEFAULT_STYLE("default-style"), REFRESH("refresh");
	public final String value;
	
	private HttpEquivAttr(String value) { this.value = value; }
	
	public static HttpEquivAttr getByValue(String value) {
		for (HttpEquivAttr sb : HttpEquivAttr.values()) {
			if(sb.value.equals(value))
				return sb;
		}
		
		return null;
	}
}
