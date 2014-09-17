package greencode.jscript.elements.attribute;

public enum ShapeArea {
	DEFAULT, RECT, CIRCLE, POLY;
	
	public String toString() { return this.name().toLowerCase(); };
}
