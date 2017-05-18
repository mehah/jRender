package greencode.kernel;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import greencode.database.annotation.Connection;
import greencode.database.implementation.DatabaseConnectionEvent;
import greencode.http.Conversation;
import greencode.http.HttpAction;
import greencode.jscript.dom.Window;
import greencode.jscript.dom.WindowHandle;
import greencode.jscript.dom.window.annotation.ConversationAttribute;
import greencode.jscript.dom.window.annotation.In;
import greencode.jscript.dom.window.annotation.RequestParameter;
import greencode.jscript.dom.window.annotation.SessionAttribute;
import greencode.jscript.dom.window.annotation.UserPrincipal;
import greencode.jscript.dom.window.annotation.Validate;
import greencode.jscript.dom.window.annotation.ViewSessionAttribute;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;
import greencode.util.GenericReflection.Condition;
import greencode.util.StringUtils;
import greencode.validator.DataValidation;

final class ActionLoader {
	private ActionLoader() {}

	private final static Condition<Field> conditionAnnotationField = new GenericReflection.Condition<Field>() {
		public boolean init(Field f) {
			return f.isAnnotationPresent(RequestParameter.class) || f.isAnnotationPresent(SessionAttribute.class) || f.isAnnotationPresent(ViewSessionAttribute.class) || f.isAnnotationPresent(ConversationAttribute.class) || f.isAnnotationPresent(In.class) || f.isAnnotationPresent(UserPrincipal.class);
		}
	};

	static void process(final GreenContext context, final HttpAction controller, final Method requestMethod) throws UnsupportedEncodingException, IllegalArgumentException, IllegalAccessException, SecurityException, InvocationTargetException, NoSuchMethodException {
		Field[] fields = GenericReflection.getDeclaredFieldsByConditionId(controller.getClass(), "httpAction:annotations");

		if (fields == null)
			fields = GenericReflection.getDeclaredFieldsByCondition(controller.getClass(), "httpAction:annotations", conditionAnnotationField, true);

		for (Field f : fields) {
			Class<?> fieldType = f.getType();
			if (f.isAnnotationPresent(In.class)) {
				In in = f.getAnnotation(In.class);

				final boolean isDiffConversation = in.conversationId() != Conversation.CURRENT && in.conversationId() != context.getRequest().getConversationId();

				Conversation conversation = isDiffConversation ? greencode.http.$Conversation.getInstance(context.getRequest(), in.conversationId()) : context.getRequest().getConversation();

				if (ClassUtils.isParent(fieldType, Window.class)) {
					f.set(controller, in.create() ? WindowHandle.getInstance((Class<Window>) fieldType, conversation) : greencode.jscript.dom.$Window.getMap(conversation).get(fieldType));
				}
			} else if (f.isAnnotationPresent(RequestParameter.class)) {
				if (ClassUtils.isPrimitiveOrWrapper(fieldType)) {
					RequestParameter requestParameterAnnotation = f.getAnnotation(RequestParameter.class);
					String parametro = requestParameterAnnotation.value();

					if (parametro.isEmpty())
						parametro = f.getName();

					Object value = context.request.getParameter(parametro);
					if (value != null) {
						value = fieldType.equals(String.class) ? StringUtils.toCharset((String) value, GreenCodeConfig.Server.View.charset) : GenericReflection.getDeclaredMethod(ClassUtils.toWrapperClass(fieldType), "valueOf", String.class).invoke(null, context.request.getParameter(parametro));

						if(value instanceof String) {
							if (requestParameterAnnotation.trim())
								value = ((String) value).trim();

							if (requestParameterAnnotation.removeMultipleSpaces())
								value = StringUtils.removeMultipleSpaces(((String) value));
							
							if(((String) value).isEmpty()) {
								value = null;
							}
						}
						
						f.set(controller, value);
					} else {
						f.set(controller, ClassUtils.getDefaultValue(fieldType));
					}
				} /*
					 * TODO: Verificar se será necessário isso no futuro. else
					 * f.set(controller,
					 * HttpParameter.Context.getObjectRequest(context
					 * .request.getParameter(parametro)));
					 */
			} else if (f.isAnnotationPresent(SessionAttribute.class))
				f.set(controller, context.request.getSession().getAttribute(f.getName()));
			else if (f.isAnnotationPresent(ViewSessionAttribute.class))
				f.set(controller, context.request.getViewSession().getAttribute(f.getName()));
			else if (f.isAnnotationPresent(ConversationAttribute.class))
				f.set(controller, context.request.getConversation().getAttribute(f.getName()));
			else if (f.isAnnotationPresent(UserPrincipal.class))
				f.set(controller, context.request.getUserPrincipal());
		}

		if (requestMethod.isAnnotationPresent(Validate.class)) {
			DataValidation data = new DataValidation(context, requestMethod.getAnnotation(Validate.class));

			String classNameBootAction = null;
			if (Cache.bootAction != null) {
				classNameBootAction = Cache.bootAction.getClass().getSimpleName();
				Console.log("Calling BeforeValidation: [" + classNameBootAction + "]");
				Cache.bootAction.beforeValidation(data);
			}

			greencode.kernel.Validate.validate(context, requestMethod, context.requestedForm, null, data);

			if (Cache.bootAction != null) {
				Console.log("Calling AfterValidation: [" + classNameBootAction + "]");
				Cache.bootAction.afterValidation(context.requestedForm, data);
			}

			data = null;
		}

	}

	public static DatabaseConnectionEvent connection(GreenContext context, Method requestMethod) throws SQLException, InstantiationException, IllegalAccessException {
		DatabaseConnectionEvent databaseConnectionEvent = null;
		if (requestMethod.isAnnotationPresent(Connection.class)) {
			Connection cA = requestMethod.getAnnotation(Connection.class);
			if (Cache.classDatabaseConnectionEvent == null) {
				Database.startConnection(context, cA);

				context.getDatabaseConnection().setAutoCommit(GreenCodeConfig.Server.DataBase.autocommit);
				if (!GreenCodeConfig.Server.DataBase.autocommit) {
					databaseConnectionEvent = new DatabaseConnectionEvent() {
						public void onSuccess(GreenContext context) {
						}

						public void beforeRequest(GreenContext context, Connection connection) {
						}

						public void onError(GreenContext context, Exception e) {
							try {
								context.getDatabaseConnection().rollback();
							} catch (SQLException e1) {
								throw new RuntimeException(e1);
							}
						}

						public void afterRequest(GreenContext context) {
							try {
								if (greencode.database.$DatabaseConnection.hasError(context.getDatabaseConnection())) {
									context.getDatabaseConnection().rollback();
								} else {
									context.getDatabaseConnection().commit();
								}
							} catch (SQLException e) {
								throw new RuntimeException(e);
							}
						}
					};
				}

			} else {
				databaseConnectionEvent = (DatabaseConnectionEvent) Cache.classDatabaseConnectionEvent.newInstance();
				databaseConnectionEvent.beforeRequest(context, cA);
			}
		}

		return databaseConnectionEvent;
	}
}
