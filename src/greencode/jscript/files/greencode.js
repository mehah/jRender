var Greencode = {
	cache : {
		MAIN_ELEMENT_ID: 1, WINDOW_ID: 2, DOCUMENT_ID: 3, HEAD_ID: 4, BODY_ID: 5,
		lastUID: 1000,
		references : {},
		generateUID: function() {
			while(Greencode.cache.references[++Greencode.cache.lastUID]);		
			return Greencode.cache.lastUID;
		},
		getById : function(id, mainElement) {
			if (id === Greencode.cache.MAIN_ELEMENT_ID)
				return mainElement;
			else if (id === Greencode.cache.WINDOW_ID)
				return window;
			else if (id === Greencode.cache.DOCUMENT_ID)
				return document;
			else if (id === Greencode.cache.HEAD_ID)
				return document.head;
			else if (id === Greencode.cache.BODY_ID)
				return document.body;
			
			return Greencode.cache.references[id];
		},		
		register: function(id, o) {
			return Greencode.cache.references[id] = o;
		},
		remove: function() {
			delete Greencode.cache.references[uid];
		}
	}
};