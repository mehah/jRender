package greencode.kernel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import greencode.database.implementation.DatabaseConnectionEvent;
import greencode.jscript.dom.Form;
import greencode.jscript.dom.Window;
import greencode.kernel.implementation.BootActionImplementation;
import greencode.kernel.implementation.PluginImplementation;
import greencode.util.MergedFile;

class Cache {
	static final File defaultTemplate = null;
	static final Map<String, File> templates = new HashMap<String, File>();
	static final BootActionImplementation bootAction = null;
	static final Class<? super DatabaseConnectionEvent> classDatabaseConnectionEvent = null;
	static final PluginImplementation[] plugins = null;
	static final Map<String, Class<? extends Form>> forms = new HashMap<String, Class<? extends Form>>();
	static final Map<String, Class<? extends Window>> registeredWindows = new HashMap<String, Class<? extends Window>>();
	static final Map<String, MergedFile> mergedFiles = new HashMap<String, MergedFile>();
}
