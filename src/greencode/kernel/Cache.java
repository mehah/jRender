package greencode.kernel;

import greencode.database.implementation.DatabaseConnectionEvent;
import greencode.jscript.Form;
import greencode.jscript.Window;
import greencode.kernel.implementation.BootActionImplementation;
import greencode.kernel.implementation.PluginImplementation;

import java.io.File;
import java.util.HashMap;

class Cache {
	static final File defaultTemplate = null;	
	static final BootActionImplementation bootAction = null;
	static final Class<? super DatabaseConnectionEvent> classDatabaseConnectionEvent = null;
	static final PluginImplementation[] plugins = null;
	static final HashMap<String, Class<? extends Form>> forms = new HashMap<String, Class<? extends Form>>();
	static final HashMap<String, Class<? extends Window>> registeredWindows = new HashMap<String, Class<? extends Window>>();
}
