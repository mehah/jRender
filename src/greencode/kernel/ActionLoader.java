package greencode.kernel;

import greencode.database.annotation.Connection;
import greencode.database.implementation.DatabaseConnectionEvent;
import greencode.http.Conversation;
import greencode.http.HttpAction;
import greencode.jscript.Window;
import greencode.jscript.WindowHandle;
import greencode.jscript.window.annotation.ConversationAttribute;
import greencode.jscript.window.annotation.In;
import greencode.jscript.window.annotation.RequestParameter;
import greencode.jscript.window.annotation.SessionAttribute;
import greencode.jscript.window.annotation.UserPrincipal;
import greencode.jscript.window.annotation.Validate;
import greencode.jscript.window.annotation.ViewSessionAttribute;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;
import greencode.util.GenericReflection.Condition;
import greencode.util.StringUtils;
import greencode.validator.DataValidation;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

final class ActionLoader {
	private ActionLoader() {}

	private final static Condition<Field> conditionAnnotationField = new GenericReflection.Condition<Field>() {
		public boolean init(Field f) {
			return f.isAnnotationPresent(RequestParameter.class) || f.isAnnotationPresent(SessionAttribute.class) || f.isAnnotationPresent(ViewSessionAttribute.class) || f.isAnnotationPresent(ConversationAttribute.class) || f.isAnnotationPresent(In.class) || f.isAnnotationPresent(UserPrincipal.class);
		}
	};

	static void process(final GreenContext context, final HttpAction controller, final Method requestMethod) throws UnsupportedEncodingException, IllegalArgumentException, IllegalAccessException, SecurityException, InvocationTargetException, NoSuchMethodException {
		Field[] fields = GenericReflection.getDeclaredFieldsByConditionId(controller.getClass(), "httpAction:annotations");

		if(fields == null)
			fields = GenericReflection.getDeclaredFieldsByCondition(controller.getClass(), "httpAction:annotations", conditionAnnotationField, true);

		for(Field f: fields) {
			Class<?> fieldType = f.getType();
			if(f.isAnnotationPresent(In.class)) {
				In in = f.getAnnotation(In.class);

				final boolean isDiffConversation = in.conversationId() != Conversation.CURRENT && in.conversationId() != context.getRequest().getConversationId();

				Conversation conversation = isDiffConversation ? greencode.http.$Conversation.getInstance(context.getRequest(), in.conversationId()) : context.getRequest().getConversation();

				if(ClassUtils.isParent(fieldType, Window.class)) {
					f.set(controller, in.create() ? WindowHandle.getInstance((Class<Window>) fieldType, conversation) : greencode.jscript.$Window.getMap(conversation).get(fieldType));
				}
			} else if(f.isAnnotationPresent(RequestParameter.class)) {
				if(ClassUtils.isPrimitiveOrWrapper(fieldType)) {
					String parametro = ((RequestParameter) f.getAnnotation(RequestParameter.class)).value();

					if(parametro.isEmpty())
						parametro = f.getName();

					Object value = context.request.getParameter(parametro);
					if(value != null) {
						value = fieldType.equals(String.class) ? StringUtils.toCharset((String) value, GreenCodeConfig.View.charset) : GenericReflection.getDeclaredMethod(ClassUtils.toWrapperClass(fieldType), "valueOf", String.class).invoke(null, context.request.getParameter(parametro));
						f.set(controller, value);
					} else
						f.set(controller, ClassUtils.getDefaultValue(fieldType));
				}/*
				 * TODO: Verificar se será necessário isso no futuro. else
				 * f.set(controller,
				 * HttpParameter.Context.getObjectRequest(context
				 * .request.getParameter(parametro)));
				 */
			} else if(f.isAnnotationPresent(SessionAttribute.class))
				f.set(controller, context.request.getSession().getAttribute(f.getName()));
			else if(f.isAnnotationPresent(ViewSessionAttribute.class))
				f.set(controller, context.request.getViewSession().getAttribute(f.getName()));
			else if(f.isAnnotationPresent(ConversationAttribute.class))
				f.set(controller, context.request.getConversation().getAttribute(f.getName()));
			else if(f.isAnnotationPresent(UserPrincipal.class))
				f.set(controller, context.request.getUserPrincipal());
		}

		if(requestMethod.isAnnotationPresent(Validate.class)) {
			DataValidation data = new DataValidation(context, requestMethod.getAnnotation(Validate.class));

			String classNameBootAction = null;
			if(Cache.bootAction != null) {
				classNameBootAction = Cache.bootAction.getClass().getSimpleName();
				Console.log("Calling BeforeValidation: [" + classNameBootAction + "]");
				Cache.bootAction.beforeValidation(data);
			}

			greencode.kernel.Validate.validate(context, requestMethod, context.requestedForm, null, data);

			if(Cache.bootAction != null) {
				Console.log("Calling AfterValidation: [" + classNameBootAction + "]");
				Cache.bootAction.afterValidation(context.requestedForm, data);
			}

			data = null;
		}

	}

	public static DatabaseConnectionEvent connection(GreenContext context, Method requestMethod) throws SQLException, InstantiationException, IllegalAccessException {
		DatabaseConnectionEvent databaseConnectionEvent = null;
		if(requestMethod.isAnnotationPresent(Connection.class)) {
			Connection cA = requestMethod.getAnnotation(Connection.class);
			if(Cache.classDatabaseConnectionEvent == null)
				Database.startConnection(context, cA);
			else {
				databaseConnectionEvent = (DatabaseConnectionEvent) Cache.classDatabaseConnectionEvent.newInstance();
				databaseConnectionEvent.beforeRequest(cA);
			}
		}

		return databaseConnectionEvent;
	}
}
