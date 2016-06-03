package greencode.jscript.dom.elements.attribute;

public enum SortedAttr {
	REVERSED("reversed"), NUMBER("number"), REVERSED_NUMBER("reversed number"), NUMBER_REVERSED("number reversed");
	
	private final String id;
	
	private SortedAttr(String id) { this.id = id; }
	
	public String toString() { return this.id; };
}
