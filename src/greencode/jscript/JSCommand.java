package greencode.jscript;

public final class JSCommand {
	@SuppressWarnings("unused")
	private final Integer uid;
	@SuppressWarnings("unused")
	private final String name;
	@SuppressWarnings("unused")
	private final Object[] parameters;
	
	public JSCommand(DOM owner, String name, Object... args) {
		this.uid = owner.uid;
		this.name = name;
		this.parameters = args.length > 0 ? args : null;
	}
}
