package com.jrender.kernel;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import com.jrender.database.annotation.Connection;
import com.jrender.database.implementation.DatabaseConnectionEvent;
import com.jrender.http.Conversation;
import com.jrender.http.HttpAction;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.WindowHandle;
import com.jrender.jscript.dom.window.annotation.ConversationAttribute;
import com.jrender.jscript.dom.window.annotation.In;
import com.jrender.jscript.dom.window.annotation.RequestParameter;
import com.jrender.jscript.dom.window.annotation.SessionAttribute;
import com.jrender.jscript.dom.window.annotation.UserPrincipal;
import com.jrender.jscript.dom.window.annotation.Validate;
import com.jrender.jscript.dom.window.annotation.ViewSessionAttribute;
import com.jrender.util.ClassUtils;
import com.jrender.util.GenericReflection;
import com.jrender.util.GenericReflection.Condition;
import com.jrender.util.StringUtils;
import com.jrender.validator.DataValidation;

final class ActionLoader {
	private ActionLoader() {
	}

	private final static Condition<Field> conditionAnnotationField = new GenericReflection.Condition<Field>() {
		public boolean init(Field f) {
			return f.isAnnotationPresent(RequestParameter.class) || f.isAnnotationPresent(SessionAttribute.class) || f.isAnnotationPresent(ViewSessionAttribute.class) || f.isAnnotationPresent(ConversationAttribute.class) || f.isAnnotationPresent(In.class) || f.isAnnotationPresent(UserPrincipal.class);
		}
	};

	static void process(final JRenderContext context, final HttpAction controller, final Method requestMethod) throws UnsupportedEncodingException, IllegalArgumentException, IllegalAccessException, SecurityException, InvocationTargetException, NoSuchMethodException {
		Field[] fields = GenericReflection.getDeclaredFieldsByConditionId(controller.getClass(), "httpAction:annotations");

		if (fields == null)
			fields = GenericReflection.getDeclaredFieldsByCondition(controller.getClass(), "httpAction:annotations", conditionAnnotationField, true);

		for (Field f : fields) {
			Class<?> fieldType = f.getType();
			if (f.isAnnotationPresent(In.class)) {
				In in = f.getAnnotation(In.class);

				final boolean isDiffConversation = in.conversationId() != Conversation.CURRENT && in.conversationId() != context.getRequest().getConversationId();

				Conversation conversation = isDiffConversation ? com.jrender.http.$Conversation.getInstance(context.getRequest(), in.conversationId()) : context.getRequest().getConversation();

				if (ClassUtils.isParent(fieldType, Window.class)) {
					f.set(controller, in.create() ? WindowHandle.getInstance((Class<Window>) fieldType, conversation) : com.jrender.jscript.dom.$Window.getMap(conversation).get(fieldType));
				}
			} else if (f.isAnnotationPresent(RequestParameter.class)) {
				if (ClassUtils.isPrimitiveOrWrapper(fieldType)) {
					RequestParameter requestParameterAnnotation = f.getAnnotation(RequestParameter.class);
					String parametro = requestParameterAnnotation.value();

					if (parametro.isEmpty())
						parametro = f.getName();

					Object value = context.request.getParameter(parametro);
					if (value != null) {
						value = fieldType.equals(String.class) ? StringUtils.toCharset((String) value, JRenderConfig.Server.View.charset) : GenericReflection.getDeclaredMethod(ClassUtils.toWrapperClass(fieldType), "valueOf", String.class).invoke(null, context.request.getParameter(parametro));

						if (value instanceof String) {
							if (requestParameterAnnotation.trim())
								value = ((String) value).trim();

							if (requestParameterAnnotation.removeMultipleSpaces())
								value = StringUtils.removeMultipleSpaces(((String) value));

							if (((String) value).isEmpty()) {
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

			com.jrender.jscript.dom.$Form.setDataValidation(context.requestedForm, data);
			com.jrender.kernel.Validate.validate(context, requestMethod, context.requestedForm, null, data);
			
			if(!data.getRequester().proceed() && data.hasError()) {
				context.executeAction = false;
			}

			if (Cache.bootAction != null) {
				Console.log("Calling AfterValidation: [" + classNameBootAction + "]");
				Cache.bootAction.afterValidation(context.requestedForm, data);
			}

			data = null;
		}

	}

	public static DatabaseConnectionEvent connection(JRenderContext context, Method requestMethod) throws SQLException, InstantiationException, IllegalAccessException {
		DatabaseConnectionEvent databaseConnectionEvent = null;
		if (requestMethod.isAnnotationPresent(Connection.class)) {
			Connection cA = requestMethod.getAnnotation(Connection.class);
			if (Cache.classDatabaseConnectionEvent == null) {
				Database.startConnection(context, cA);

				context.getDatabaseConnection().setAutoCommit(JRenderConfig.Server.DataBase.autocommit);
				if (!JRenderConfig.Server.DataBase.autocommit) {
					databaseConnectionEvent = new DatabaseConnectionEvent() {
						public void onSuccess(JRenderContext context) {
						}

						public void beforeRequest(JRenderContext context, Connection connection) {
						}

						public void onError(JRenderContext context, Exception e) {
							try {
								context.getDatabaseConnection().rollback();
							} catch (SQLException e1) {
								throw new RuntimeException(e1);
							}
						}

						public void afterRequest(JRenderContext context) {
							try {
								if (com.jrender.database.$DatabaseConnection.hasError(context.getDatabaseConnection())) {
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
