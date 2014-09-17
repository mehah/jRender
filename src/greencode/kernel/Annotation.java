package greencode.kernel;

import java.io.File;
import java.io.IOException;

import greencode.jscript.Window;
final class Annotation {	
	private Annotation() {}
	
	static void processWindowAnnotation(Class<? extends Window> c, ClassLoader classLoader, File greencodeFolder) throws IOException
	{
		if(c.isAnnotationPresent(greencode.jscript.window.annotation.Page.class))
		{			
			greencode.jscript.window.annotation.Page page = c.getAnnotation(greencode.jscript.window.annotation.Page.class);
							
			Page.registerPage(classLoader, c, page, greencodeFolder);
		}else if(c.isAnnotationPresent(greencode.jscript.window.annotation.RegisterPage.class))
		{
			greencode.jscript.window.annotation.RegisterPage pages = c.getAnnotation(greencode.jscript.window.annotation.RegisterPage.class);
			if(pages.value().length == 0)
				throw new RuntimeException(LogMessage.getMessage("green-0024", c.getSimpleName()));
			else
			{
				for (greencode.jscript.window.annotation.Page page : pages.value())
					Page.registerPage(classLoader, c, page, greencodeFolder);
			}
		}else
			throw new RuntimeException(LogMessage.getMessage("green-0029", c.getSimpleName()));
	}
}
