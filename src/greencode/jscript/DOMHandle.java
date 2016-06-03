package greencode.jscript;

import java.util.HashMap;

import javax.servlet.http.Part;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import greencode.exception.ConnectionLost;
import greencode.exception.GreencodeError;
import greencode.http.ViewSession;
import greencode.jscript.dom.Node;
import greencode.jscript.dom.Window;
import greencode.kernel.Console;
import greencode.kernel.DOMScanner;
import greencode.kernel.GreenContext;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;
import greencode.util.LogMessage;

public final class DOMHandle {
	public static enum UIDReference {
		MAIN_ELEMENT_ID, WINDOW_ID, DOCUMENT_ID, HEAD_ID, BODY_ID;
	}

	private DOMHandle() {
	}

	public static Integer getUID(DOM d) {
		return d.uid;
	}

	public static Window getWindow(DOM d) {
		return d.window;
	}

	public static void registerElementByCommand(DOM owner, Node e, String name, Object... parameters) {
		registerReturnByCommand(owner, ((DOM) e).uid, name, parameters);
	}

	public static void registerElementByVector(DOM owner, Node e, int index) {
		DOMScanner.registerCommand(owner, ((DOM) e).uid + "*vector." + index);
	}

	public static void registerElementByProperty(DOM owner, Node e, String name) {
		registerReturnByProperty(owner, ((DOM) e).uid, name);
	}

	public static void registerReturnByProperty(DOM owner, int uid, String name) {
		DOMScanner.registerCommand(owner, uid + "*prop." + name);
	}

	public static void registerReturnByCommand(DOM owner, int uid, String name, Object... parameters) {
		DOMScanner.registerCommand(owner, uid + "*ref." + name, parameters);
	}

	public static void registerReturnByCommand(DOM owner, int[] uids, String name, Object... parameters) {
		assert (uids.length < 1);

		StringBuilder _uids = new StringBuilder("[").append(uids[0]);
		for (int i = 0; ++i < uids.length;) {
			_uids.append(',').append(uids[i]);
		}
		_uids.append(']');
		DOMScanner.registerCommand(owner, _uids + "*ref." + name, parameters);
	}

	public static void execCommand(DOM dom, String methodName, Object... args) {
		DOMScanner.registerCommand(dom, methodName, args);
	}

	public static void setProperty(DOM dom, String name, Object value) {
		dom.variables.put(name, value);
		DOMScanner.registerCommand(dom, "#" + name, value);
	}

	public static String getDefaultIdToRegisterReturn(int uid) {
		return uid + "*ref";
	}

	public static void deleteReference(DOM dom) {
		deleteReference(dom.window, dom.uid);
	}

	public static void deleteReference(Window window, int uid) {
		DOMHandle.execCommand(window, "Greencode.cache.remove", uid);
	}

	@SuppressWarnings("unchecked")
	public static <C> C getVariableValue(DOM owner, String varName, Class<C> cast) {
		return (C) owner.variables.get(varName);
	}

	public static void setVariableValue(DOM owner, String varName, Object value) {
		owner.variables.put(varName, value);
	}

	public static void removeVariable(DOM owner, String varName) {
		owner.variables.remove(varName);
	}

	public static boolean containVariableKey(DOM owner, String key) {
		return owner.variables.containsKey(key);
	}

	private static Object getSyncValue(GreenContext context, DOM owner, String varName, Class<?> cast, boolean isMethod, String methodOrPropName, Object... parameters) {
		final ViewSession viewSession = owner.viewSession;
		synchronized (greencode.kernel.$GreenContext.isImmediateSync(context) ? owner : Thread.currentThread()) {
			if (!isMethod)
				methodOrPropName = '#' + methodOrPropName;

			JSCommand jsCommand = new JSCommand(owner, cast, methodOrPropName, parameters);

			try {
				greencode.kernel.$DOMScanner.setSync(context, owner.uid, varName, jsCommand);
				getDOMSync(viewSession).put(owner.uid, owner);
				Console.log("Synchronizing: [varName=" + varName + ", command={uid=" + owner.uid + ", name=" + methodOrPropName + ", parameters=" + context.gsonInstance.toJson(parameters) + "]");

				if (greencode.kernel.$GreenContext.isImmediateSync(context)) {
					owner.flush();
					owner.wait(120000);
				}
			} catch (Exception e) {
				throw new ConnectionLost(LogMessage.getMessage("green-0011"));
			}
		}

		return greencode.kernel.$GreenContext.isImmediateSync(context) ? owner.variables.get(varName) : null;
	}

	@SuppressWarnings("unchecked")
	private static <C> C getVariableValue(final DOM owner, final String varName, Class<C> cast, boolean isMethod, final String _name, Object... parameters) {
		GreenContext context = GreenContext.getInstance();

		if ((ClassUtils.isPrimitiveOrWrapper(cast) || ClassUtils.isParent(cast, JsonElement.class)) && (greencode.kernel.$GreenContext.isForcingSynchronization(context, owner, _name) || !owner.variables.containsKey(varName))) {
			Object v = getSyncValue(context, owner, varName, cast, isMethod, _name, parameters);

			if (greencode.kernel.$GreenContext.isImmediateSync(context)) {
				return setVariableValue(context, owner, varName, cast, v);
			}

			return null;
		}

		return (C) owner.variables.get(varName);
	}

	@SuppressWarnings("unchecked")
	static <C> C setVariableValue(GreenContext context, final DOM owner, final String varName, Class<C> cast, Object v) {
		if (cast != null && !cast.equals(String.class) && !cast.equals(Part.class)) {
			if (ClassUtils.isPrimitiveOrWrapper(cast)) {
				try {
					v = GenericReflection.getDeclaredMethod(cast, "valueOf", String.class).invoke(null, v);
				} catch (Exception e) {
					throw new GreencodeError(e);
				}
			} else
				v = context.gsonInstance.fromJson((String) v, cast);
		}

		// Substitua o antigo valor String para o novo valor com o formato
		// certo.
		owner.variables.put(varName, v);
		return (C) v;
	}

	static HashMap<Integer, DOM> getDOMSync(ViewSession viewSession) {
		@SuppressWarnings("unchecked")
		HashMap<Integer, DOM> DOMList = (HashMap<Integer, DOM>) viewSession.getAttribute("DOM_SYNC");
		if (DOMList == null)
			viewSession.setAttribute("DOM_SYNC", DOMList = new HashMap<Integer, DOM>());

		return DOMList;
	}

	public static <C> C getVariableValueByCommand(DOM owner, String varName, Class<C> cast, String commandName, Object... parameters) {
		return getVariableValue(owner, varName, cast, true, commandName, parameters);
	}

	public static <C> C getVariableValueByProperty(DOM owner, String varName, Class<C> cast, String propName) {
		return getVariableValue(owner, varName, cast, false, propName);
	}

	public static <C> C getVariableValueByCommandNoCache(DOM owner, String varName, Class<C> cast, String commandName, Object... parameters) {
		C v = getVariableValue(owner, varName, cast, true, commandName, parameters);
		DOMHandle.removeVariable(owner, varName);
		return v;
	}

	public static <C> C getVariableValueByPropertyNoCache(DOM owner, String varName, Class<C> cast, String propName) {
		C v = getVariableValue(owner, varName, cast, false, propName);
		DOMHandle.removeVariable(owner, varName);
		return v;
	}

	public static JsonObject getJSONObject(DOM owner, String varName, String... propertyNames) {
		return getVariableValue(owner, varName, JsonObject.class, false, "", (Object[]) propertyNames);
	}

	public static JsonArray getJSONArray(DOM owner, String varName, String... propertyNames) {
		return getVariableValue(owner, varName, JsonArray.class, false, "[]", (Object[]) propertyNames);
	}

	public static JsonObject getJSONObjectByProperty(DOM owner, String varName, String propertyName, String... propertyNames) {
		return getVariableValue(owner, varName, JsonObject.class, false, '#' + propertyName, (Object[]) propertyNames);
	}

	public static JsonArray getJSONArrayByProperty(DOM owner, String varName, String propertyName, String... propertyNames) {
		return getVariableValue(owner, varName, JsonArray.class, false, "#[]" + propertyName, (Object[]) propertyNames);
	}

	public static boolean isForcingSynchronization(GreenContext context, final DOM dom, String property) {
		return greencode.kernel.$GreenContext.isForcingSynchronization(context, dom, property);
	}

	public static class CustomMethod {
		public static void call(DOM dom, String methodName, Object... args) {
			execCommand(dom, "@customMethod." + methodName, args);
		}

		public static void registerElement(DOM owner, Node e, String name, Object... parameters) {
			registerReturnByCommand(owner, ((DOM) e).uid, "@customMethod." + name, parameters);
		}

		public static void registerReturn(DOM owner, int uid, String name, Object... parameters) {
			registerReturnByCommand(owner, uid, "@customMethod." + name, parameters);
		}

		public static void registerReturn(DOM owner, int[] uids, String name, Object... parameters) {
			registerReturnByCommand(owner, uids, "@customMethod." + name, parameters);
		}
	}
}
