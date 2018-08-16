package com.jrender.jscript;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Part;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jrender.exception.ConnectionLost;
import com.jrender.exception.JRenderError;
import com.jrender.http.ViewSession;
import com.jrender.jscript.JSExecutor.TYPE;
import com.jrender.jscript.dom.FunctionHandle;
import com.jrender.jscript.dom.Node;
import com.jrender.jscript.dom.Window;
import com.jrender.kernel.Console;
import com.jrender.kernel.DOMScanner;
import com.jrender.kernel.JRenderContext;
import com.jrender.util.ClassUtils;
import com.jrender.util.GenericReflection;
import com.jrender.util.LogMessage;

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
	
	public static void newInstance(DOM owner, String className, Object... parameters) {		
		DOMScanner.registerExecution(new JSExecutor(new DOM[] { owner }, owner.window, className, TYPE.INSTANCE, parameters));
	}

	public static void registerFunctionHandleByCommand(FunctionHandle ret, DOM owner, String name, Object... parameters) {
		DOMScanner.registerExecution(new JSExecutor(ret, owner, name, parameters));
	}

	public static void registerReturnByVector(Node ret, DOM owner, int index) {
		DOMScanner.registerExecution(new JSExecutor(new DOM[] { ret }, owner, index + "", TYPE.VECTOR));
	}

	public static void registerReturnByProperty(DOM ret, DOM owner, String name) {
		DOMScanner.registerExecution(new JSExecutor(new DOM[] { ret }, owner, name, TYPE.PROPERTY));
	}

	public static void registerReturnByCommand(DOM ret, DOM owner, String name, Object... parameters) {
		DOMScanner.registerExecution(new JSExecutor(new DOM[] { ret }, owner, name, TYPE.METHOD, parameters));
	}

	public static void registerReturnsByCommand(DOM[] rets, DOM owner, String name, Object... parameters) {
		assert (rets.length < 1);
		DOMScanner.registerExecution(new JSExecutor(rets, owner, null, name, TYPE.METHOD, parameters));
	}

	public static void execCommand(DOM dom, String methodName, Object... args) {
		DOMScanner.registerExecution(new JSExecutor(dom, methodName, JSExecutor.TYPE.METHOD, args));
	}

	public static void setProperty(DOM dom, String name, Object value) {
		dom.variables.put(name, value);
		DOMScanner.registerExecution(new JSExecutor(dom, name, TYPE.PROPERTY, value));
	}

	public static String getDefaultIdToRegisterReturn(int uid) {
		return uid + "*ref";
	}

	public static void deleteReference(DOM dom) {
		deleteReference(dom.window, dom.uid);
	}

	public static void deleteReference(Window window, int uid) {
		DOMHandle.execCommand(window, "JRender.cache.remove", uid);
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

	private static Object getSyncValue(JRenderContext context, DOM owner, String varName, Class<?> cast, String name, JSExecutor.TYPE type, Object... parameters) {
		final ViewSession viewSession = owner.viewSession;
		synchronized (com.jrender.kernel.$JRenderContext.isImmediateSync(context) ? owner : Thread.currentThread()) {
			JSExecutor jsCommand = new JSExecutor(owner, cast, name, type, parameters);

			try {
				com.jrender.kernel.$DOMScanner.setSync(context, owner.uid, varName, jsCommand);
				getDOMSync(viewSession).put(owner.uid, owner);
				Console.log("Synchronizing: [varName=" + varName + ", command={uid=" + owner.uid + ", name=" + name + ", parameters=" + context.gsonInstance.toJson(parameters) + "]");

				if (com.jrender.kernel.$JRenderContext.isImmediateSync(context)) {
					owner.flush();
					owner.wait(120000);
				}
			} catch (Exception e) {
				throw new ConnectionLost(LogMessage.getMessage("0011"));
			}
		}

		return com.jrender.kernel.$JRenderContext.isImmediateSync(context) ? owner.variables.get(varName) : null;
	}

	@SuppressWarnings("unchecked")
	private static <C> C getVariableValue(final DOM owner, final String varName, Class<C> cast, final String _name, JSExecutor.TYPE type, Object... parameters) {
		JRenderContext context = JRenderContext.getInstance();

		if ((ClassUtils.isPrimitiveOrWrapper(cast) || ClassUtils.isParent(cast, JsonElement.class) || cast.equals(Part.class)) && (com.jrender.kernel.$JRenderContext.isForcingSynchronization(context, owner, _name) || !owner.variables.containsKey(varName))) {
			Object v = getSyncValue(context, owner, varName, cast, _name, type, parameters);

			if (com.jrender.kernel.$JRenderContext.isImmediateSync(context)) {
				return setVariableValue(context, owner, varName, cast, v);
			}

			return null;
		}

		return (C) owner.variables.get(varName);
	}

	@SuppressWarnings("unchecked")
	static <C> C setVariableValue(JRenderContext context, final DOM owner, final String varName, Class<C> cast, Object v) {
		if (cast != null && !cast.equals(String.class) && !cast.equals(Part.class)) {
			if (ClassUtils.isPrimitiveOrWrapper(cast)) {
				try {
					v = GenericReflection.getDeclaredMethod(cast, "valueOf", String.class).invoke(null, v);
				} catch (Exception e) {
					throw new JRenderError(e);
				}
			} else
				v = context.gsonInstance.fromJson((String) v, cast);
		}

		// Substitua o antigo valor String para o novo valor com o formato certo.
		owner.variables.put(varName, v);
		return (C) v;
	}

	static Map<Integer, DOM> getDOMSync(ViewSession viewSession) {
		@SuppressWarnings("unchecked")
		Map<Integer, DOM> DOMList = (HashMap<Integer, DOM>) viewSession.getAttribute("DOM_SYNC");
		if (DOMList == null)
			viewSession.setAttribute("DOM_SYNC", DOMList = new HashMap<Integer, DOM>());

		return DOMList;
	}

	public static <C> C getVariableValueByCommand(DOM owner, String varName, Class<C> cast, String commandName, Object... parameters) {
		return getVariableValue(owner, varName, cast, commandName, JSExecutor.TYPE.METHOD, parameters);
	}

	public static <C> C getVariableValueByProperty(DOM owner, String varName, Class<C> cast, String propName) {
		return getVariableValue(owner, varName, cast, propName, JSExecutor.TYPE.PROPERTY);
	}

	public static <C> C getVariableValueByCommandNoCache(DOM owner, String varName, Class<C> cast, String commandName, Object... parameters) {
		C v = getVariableValue(owner, varName, cast, commandName, JSExecutor.TYPE.METHOD, parameters);
		DOMHandle.removeVariable(owner, varName);
		return v;
	}

	public static <C> C getVariableValueByPropertyNoCache(DOM owner, String varName, Class<C> cast, String propName) {
		C v = getVariableValue(owner, varName, cast, propName, JSExecutor.TYPE.PROPERTY);
		DOMHandle.removeVariable(owner, varName);
		return v;
	}

	public static JsonObject getJSONObjectByProperty(DOM owner, String varName, String propertyName, String... propertyNames) {
		return getVariableValue(owner, varName, JsonObject.class, propertyName, JSExecutor.TYPE.PROPERTY, (Object[]) propertyNames);
	}

	public static JsonArray getJSONArrayByProperty(DOM owner, String varName, String propertyName, String... propertyNames) {
		return getVariableValue(owner, varName, JsonArray.class, propertyName, JSExecutor.TYPE.VECTOR, (Object[]) propertyNames);
	}

	public static boolean isForcingSynchronization(JRenderContext context, final DOM dom, String property) {
		return com.jrender.kernel.$JRenderContext.isForcingSynchronization(context, dom, property);
	}
}
