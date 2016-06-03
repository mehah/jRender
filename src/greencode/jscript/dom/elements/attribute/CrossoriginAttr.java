package greencode.jscript.dom.elements.attribute;

public enum CrossoriginAttr {
	ANONYMOUS("anonymous"), USE_CREDENTIALS("use-credentials");
	public final String value;
	
	private CrossoriginAttr(String value) { this.value = value; }
	
	public static CrossoriginAttr getByValue(String value) {
		for (CrossoriginAttr sb : CrossoriginAttr.values()) {
			if(sb.value.equals(value))
				return sb;
		}
		
		return null;
	}
}
