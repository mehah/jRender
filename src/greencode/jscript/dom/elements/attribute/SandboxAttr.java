package greencode.jscript.dom.elements.attribute;

public enum SandboxAttr {
	EMPTY(""), ALLOW_SAME_ORIGIN("allow-same-origin"), ALLOW_TOP_NAVIGATION("allow-top-navigation"), ALLOW_FORMS("allow-forms"), ALLOW_SCRIPTS("allow-scripts");
	public final String value;
	
	private SandboxAttr(String value) { this.value = value; }
	
	public static SandboxAttr getByValue(String value) {
		for (SandboxAttr sb : SandboxAttr.values()) {
			if(sb.value.equals(value))
				return sb;
		}
		
		return null;
	}
}
