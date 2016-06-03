package greencode.jscript.dom.elements.attribute;

public enum KeytypeAttr {
	RSA, DSA, EC;	
	public String toString() { return this.name().toLowerCase(); };
}
