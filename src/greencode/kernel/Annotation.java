package greencode.kernel;

import java.io.File;
import java.io.IOException;

import greencode.jscript.dom.Window;
import greencode.util.LogMessage;

final class Annotation {
	private Annotation() {	}

	static void processWindowAnnotation(Class<? extends Window> c, ClassLoader classLoader, File greencodeFolder) throws IOException {
		if (c.isAnnotationPresent(greencode.jscript.dom.window.annotation.Page.class))
			FileWeb.registerPage(classLoader, c, c.getAnnotation(greencode.jscript.dom.window.annotation.Page.class), greencodeFolder);
		else if (c.isAnnotationPresent(greencode.jscript.dom.window.annotation.RegisterPage.class)) {
			greencode.jscript.dom.window.annotation.RegisterPage pages = c.getAnnotation(greencode.jscript.dom.window.annotation.RegisterPage.class);
			if (pages.value().length == 0)
				throw new RuntimeException(LogMessage.getMessage("green-0024", c.getSimpleName()));

			for (greencode.jscript.dom.window.annotation.Page page : pages.value())
				FileWeb.registerPage(classLoader, c, page, greencodeFolder);
		} else
			throw new RuntimeException(LogMessage.getMessage("green-0029", c.getSimpleName()));
	}
}
