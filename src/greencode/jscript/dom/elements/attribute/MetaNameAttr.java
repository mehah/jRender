package greencode.jscript.dom.elements.attribute;

public enum MetaNameAttr {
	APPLICATION_NAME("application-name"), AUTHOR("author"), DESCRIPTION("description"), GENERATOR("generator"), KEYWORDS("keywords");
	public final String value;
	
	private MetaNameAttr(String value) { this.value = value; }
	
	public static MetaNameAttr getByValue(String value) {
		for (MetaNameAttr sb : MetaNameAttr.values()) {
			if(sb.value.equals(value))
				return sb;
		}
		
		return null;
	}
}
