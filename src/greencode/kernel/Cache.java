package greencode.kernel;

import greencode.database.implementation.DatabaseConnectionEvent;
import greencode.jscript.Form;
import greencode.jscript.Window;
import greencode.kernel.implementation.BootActionImplementation;

import java.io.File;
import java.util.HashMap;

class Cache {
	static File defaultTemplate;	
	static BootActionImplementation bootAction;
	static Class<? super DatabaseConnectionEvent> classDatabaseConnectionEvent;
	static final HashMap<String, Class<? extends Form>> forms = new HashMap<String, Class<? extends Form>>();
	static final HashMap<String, Class<? extends Window>> registeredWindows = new HashMap<String, Class<? extends Window>>();
}
