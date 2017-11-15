package com.jrender.kernel;

import java.io.File;
import java.io.IOException;

import com.jrender.jscript.dom.Window;
import com.jrender.util.LogMessage;

final class Annotation {
	private Annotation() {	}

	static void processWindowAnnotation(Class<? extends Window> c, ClassLoader classLoader, File jrenderFolder) throws IOException {
		if (c.isAnnotationPresent(com.jrender.jscript.dom.window.annotation.Page.class))
			FileWeb.registerPage(classLoader, c, c.getAnnotation(com.jrender.jscript.dom.window.annotation.Page.class), jrenderFolder);
		else if (c.isAnnotationPresent(com.jrender.jscript.dom.window.annotation.RegisterPage.class)) {
			com.jrender.jscript.dom.window.annotation.RegisterPage pages = c.getAnnotation(com.jrender.jscript.dom.window.annotation.RegisterPage.class);
			if (pages.value().length == 0)
				throw new RuntimeException(LogMessage.getMessage("0024", c.getSimpleName()));

			for (com.jrender.jscript.dom.window.annotation.Page page : pages.value())
				FileWeb.registerPage(classLoader, c, page, jrenderFolder);
		} else
			throw new RuntimeException(LogMessage.getMessage("0029", c.getSimpleName()));
	}
}
