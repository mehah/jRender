package greencode.jscript.elements.attribute;

public enum ScopeAttr {
	COL, COLGROUP, ROW, ROWGROUP;
	public String toString() { return this.name().toLowerCase(); };
}
