package greencode.jscript;

import greencode.jscript.DOMHandle.UIDReference;

public final class JSCommand {
	@SuppressWarnings("unused")
	private final Integer uid;
	@SuppressWarnings("unused")
	private final String name;
	@SuppressWarnings("unused")
	private final Object[] parameters;
	@SuppressWarnings("unused")
	private final String cast;
	
	public JSCommand(DOM owner, Class<?> cast, String name, Object... args) {
		this.uid = owner == null ? UIDReference.WINDOW_ID.ordinal() : owner.uid;
		this.name = name;
		this.parameters = args.length > 0 ? args : null;
		this.cast = cast == null ? null : cast.getName();
	}
}
