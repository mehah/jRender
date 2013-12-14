package greencode.jscript;

import java.util.HashMap;
import java.util.List;

import greencode.http.ViewSession;
import greencode.kernel.Console;
import greencode.kernel.ElementsScan;
import greencode.kernel.GreenContext;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class DOMHandle {
	private DOMHandle() {}
	
	public static final void replaceReference(DOM dom, DOM newDom)
	{
		dom.uid = newDom.uid;
		dom.variables = newDom.variables;
	}
	
	public static final void registerElementByCommand(DOM owner, Node e, String name, Object... parameters)
	{	
		registerReturnByCommand(owner, e.uid, name, parameters);
	}
	
	public static final void registerReturnByCommand(DOM owner, int uid, String name, Object... parameters)
	{	
		ElementsScan.registerCommand(owner, true, uid+"*ref."+name, parameters);
	}
	
	public static void execCommand(DOM dom, String methodName, Object... args)
	{
		ElementsScan.registerCommand(dom, false, methodName, args);
	}
	
	public static void setProperty(DOM dom, String name, Object value)
	{
		dom.variables.put(name, value);
		ElementsScan.registerCommand(dom, false, "*prop."+name, value);
	}
	
	public static final String getRegisteredReturn(int uid)
	{	
		return uid+"*ref";
	}
	
	public static void removeRegisteredReturn(Window window, int uid)
	{
		DOMHandle.execCommand(window, "removeRegisteredReturn", uid);
	}
	
	public static void setVariableValue(DOM owner, String varName, Object value)
	{
		owner.variables.put(varName, value);
	}
	
	public static void removeVariable(DOM owner, String varName)
	{
		owner.variables.remove(varName);
	}
	
	public static boolean containVariableKey(DOM owner, String key)
	{
		return owner.variables.containsKey(key);
	}
	
	private static final String getSyncValue(DOM owner, String varName, boolean isMethod, String methodOrPropName, Object... parameters)
	{
		synchronized (owner) {
			GreenContext context = GreenContext.getInstance();
			
			JsonObject json = new JsonObject();
			json.addProperty("uid", owner.uid);
			json.addProperty("varName", varName);
			
			if(!isMethod)
				methodOrPropName = '#'+methodOrPropName;
			
			JSCommand jsCommand = new JSCommand(owner, methodOrPropName, parameters);
			
			JsonElement jElement = context.getGsonInstance().toJsonTree(jsCommand);
			
			json.add("command", jElement);
			
			Console.log("Synchronizing: [varName="+varName+", command="+context.getGsonInstance().toJson(jElement)+"]");
					
			try {
				DOM.send2kbInternetExplorer(context);
				
				JsonObject j = new JsonObject();
				
				List<JSCommand> commands = greencode.kernel.$.getElementsScan(owner.viewSession).pComm;
				
				j.add("pComm", context.getGsonInstance().toJsonTree(commands));
				commands.clear();
				
				j.add("sync", json);
				
				Console.log("ehe");
				
				if(context.getRequest().isAjax())
				{
					context.getResponse().getWriter().write(','+context.getGsonInstance().toJson(j));
				}else
				{
					context.getResponse().getWriter().write(
						new StringBuilder("<div id=\"JSON_CONTENT\" style=\"display: none;\">")
							.append(context.getGsonInstance().toJson(j))
							.append("</div>").toString()
					);
				}
				
				context.getResponse().flushBuffer();
				
				getDOMSync(context.getRequest().getViewSession()).put(owner.uid, owner);
				
				owner.wait(120000);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		return (String) owner.variables.get(varName);
	}
	
	@SuppressWarnings("unchecked")
	private static<C> C getVariableValue(DOM owner, String varName, Class<C> cast, boolean isMethod, String _name, Object... parameters)
	{
		GreenContext context = GreenContext.getInstance();
		
		if((greencode.kernel.$.GreenContext$forceSynchronization(context) || !owner.variables.containsKey(varName)))
		{
			Object v = getSyncValue(owner, varName, isMethod, _name, parameters);
			
			if(cast != null && !cast.equals(String.class))
			{
				if(ClassUtils.isPrimitiveOrWrapper(cast))
				{
					try {
						v = GenericReflection.getDeclaredMethod(cast, "valueOf", String.class).invoke(null, v);
					} catch (Exception e) {
						Console.error(e);
					}
				}else
				{
					v = context.getGsonInstance().fromJson((String) v, cast);
				}
			}
			
			return (C) v;
		}
				
		return (C) owner.variables.get(varName);
	}
	
	public static final HashMap<Integer, DOM> getDOMSync(ViewSession viewSession)
	{
		@SuppressWarnings("unchecked")
		HashMap<Integer, DOM> DOMList = (HashMap<Integer, DOM>) viewSession.getAttribute("DOM_SYNC");
		if(DOMList == null)
		{
			DOMList = new HashMap<Integer, DOM>();
			viewSession.setAttribute("DOM_SYNC", DOMList);
		}
		
		return DOMList;
	}
	
	public static<C> C getVariableValueByCommand(DOM owner, String varName, Class<C> cast, String commandName, Object... parameters)
	{
		return getVariableValue(owner, varName, cast, true, commandName, parameters);
	}
	
	public static<C> C getVariableValueByProperty(DOM owner, String varName, Class<C> cast, String propName)
	{
		return getVariableValue(owner, varName, cast, false, propName);
	}
	
	
}
