package greencode.jscript.elements.attribute;

public enum KindAttr {
	CAPTIONS, CHAPTERS, DESCRIPTIONS, METADATA, SUBTITLES;
	public String toString() { return this.name().toLowerCase(); };
}
