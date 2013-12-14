package greencode.kernel;

import greencode.jscript.Window;

final class Annotation {	
	private Annotation() {}
	
	static void processWindowAnnotation(Class<? extends Window> c)
	{
		if(c.isAnnotationPresent(greencode.jscript.window.annotation.Page.class))
		{			
			greencode.jscript.window.annotation.Page page = c.getAnnotation(greencode.jscript.window.annotation.Page.class);
							
			Page.registerPage(c, page);
			
		}else if(c.isAnnotationPresent(greencode.jscript.window.annotation.RegisterPage.class))
		{
			greencode.jscript.window.annotation.RegisterPage pages = c.getAnnotation(greencode.jscript.window.annotation.RegisterPage.class);
			if(pages.value().length == 0)
			{
				Console.warning(
					LogMessage.getMessage("green-0024", c.getSimpleName())
				);
			}else
			{
				for (int i = -1, s = pages.value().length; ++i < s;)
				{
					greencode.jscript.window.annotation.Page page = pages.value()[i];
					Page.registerPage(c, page);
				}
			}
		}
	}
	
	/*@SuppressWarnings("unchecked")
	static void processRunTimeAnnotation(GreenContext context, HttpControllerRequest controller)
	{
		for (Field f : GenericReflection.getDeclaredFields(controller.getClass()))
		{
			Class<?> fieldType = f.getType();				
			try {
				if(f.isAnnotationPresent(In.class))
				{
					In in = f.getAnnotation(In.class);
											
					Conversation originalConversation = context.getConversation();
					
					boolean isDiffConversation = in.conversationId() != Conversation.Context.CURRENT && in.conversationId() != originalConversation.getId();
					
					if(isDiffConversation)
					{
						context.conversation = Conversation.Context.getInstance(in.conversationId());
					}
					
					if(ClassUtils.isParent(fieldType, HttpAction.class))
					{	
						if(in.create())
						{
							f.set(controller, HttpAction.Context.getInstance((Class<HttpAction>)fieldType, context.getConversation()));
						}else
						{
							f.set(controller, context.getListHTTPActions().get(fieldType));
						}
					}else if(ClassUtils.isParent(fieldType, HttpForm.class))
					{
						HttpForm _form = (HttpForm) HttpForm.Context.getInstance((Class<HttpForm>)fieldType, context.getConversation());
						
						if(_form == null && in.create())
						{
							throw new UnsupportedOperationException(LogMessage.getMessage("green-0005"));
						}
						
						f.set(controller, _form);
					}
					
					if(isDiffConversation)
					{
						context.conversation = originalConversation;
					}
				}else if(f.isAnnotationPresent(RequestParameter.class))
				{
					String parametro = ((RequestParameter)f.getAnnotation(RequestParameter.class)).value();
					
					if(parametro.isEmpty())
					{
						parametro = f.getName();
					}
					
					Object value = null;
					
					if(ClassUtils.isPrimitiveOrWrapper(fieldType))
					{
						value = context.request.getParameter(parametro);
						if(value != null)
						{
							if(fieldType.equals(String.class))
								value = StringUtils.toCharset((String) value, GreenContext.DEFAULT_CHARSET);
							else							
								value = GenericReflection.getDeclaredMethod(ClassUtils.toWrapperClass(fieldType), "valueOf", String.class).invoke(null, context.request.getParameter(parametro));
							
							f.set(controller, value);
						}else
							f.set(controller, ClassUtils.getDefaultValue(fieldType));
					}else
						f.set(controller, HttpParameter.Context.getObjectRequest(context.request.getParameter(parametro)));
				}
				else if(f.isAnnotationPresent(SessionAttribute.class))
					f.set(controller, context.session.getAttribute(f.getName()));
			} catch (Exception e) {
				Console.error(e);
			}
		}
	}*/
}
