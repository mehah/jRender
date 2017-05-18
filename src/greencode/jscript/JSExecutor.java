package greencode.jscript;

import greencode.http.ViewSession;
import greencode.jscript.DOMHandle.UIDReference;
import greencode.jscript.dom.FunctionHandle;
import greencode.kernel.GreenContext;


@SuppressWarnings("unused")
public final class JSExecutor {
	
	public static enum TYPE {
		METHOD, PROPERTY, VECTOR, INSTANCE
	}
	
	public final transient ViewSession view;
	
	private final Integer[] uidSave;
	
	private final Object[] parameters;
		
	private final String name, cast;
	
	private final int type, uid;
	
	public JSExecutor(DOM owner, String commandName, TYPE type, Object... args) {
		this.view = greencode.jscript.$DOMHandle.getViewSession(owner);
		this.uidSave = null;
		this.uid = owner.uid;
		this.name = commandName;
		this.type = type.ordinal();
		this.parameters = args.length > 0 ? args : null;
		this.cast = null;
	}
	
	public JSExecutor(DOM owner, Class<?> cast, String name, TYPE type, Object... args) {
		this.view = greencode.jscript.$DOMHandle.getViewSession(owner);
		this.uidSave = null;
		this.uid = owner.uid;
		this.name = name;
		this.type = type.ordinal();
		this.parameters = args.length > 0 ? args : null;
		this.cast = cast.getName();
	}
	
	public JSExecutor(DOM[] domSave, DOM owner, String name, TYPE type, Object... args) {
		this(domSave, owner, null, name, type, args);
	}
	
	public JSExecutor(FunctionHandle function, DOM owner, String name, Object... args) {
		this.view = greencode.jscript.$DOMHandle.getViewSession(owner);
		
		this.uidSave = new Integer[]{function.hashCode()};
		this.uid = owner.uid;
		this.name = name;
		this.type = TYPE.METHOD.ordinal();
		this.parameters = args.length > 0 ? args : null;
		this.cast = null;
	}
	
	public JSExecutor(DOM[] domSave, DOM owner, Class<?> cast, String name, TYPE type, Object... args) {
		this.view = greencode.jscript.$DOMHandle.getViewSession(owner);
		
		Integer[] uids = new Integer[domSave.length];
		for (int i = -1; ++i < domSave.length;) {
			uids[i] = domSave[i].uid;
		}
		
		this.uidSave = uids;
		this.uid = owner.uid;
		this.name = name;
		this.type = type.ordinal();
		this.parameters = args.length > 0 ? args : null;
		this.cast = null;
	}

	public JSExecutor(GreenContext context, String name, TYPE type, Object... args) {
		this.view = context.getRequest().getViewSession();
		this.uidSave = null;
		this.uid = UIDReference.WINDOW_ID.ordinal();
		this.name = name;
		this.type = type.ordinal();
		this.parameters = args.length > 0 ? args : null;
		this.cast = null;
	}
}
