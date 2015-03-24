var MAIN_ELEMENT_ID = 1, WINDOW_ID = 2, DOCUMENT_ID = 3, HEAD_ID = 4, BODY_ID = 5;

var Greencode = {
	cache : {
		lastUID: 0,
		references : {},
		generateUID: function() {
			while(Greencode.cache.references[++Greencode.cache.lastUID]);		
			return Greencode.cache.lastUID;
		},
		getById : function(id, mainElement) {
			if (id === MAIN_ELEMENT_ID)
				return mainElement;
			else if (id === WINDOW_ID)
				return window;
			else if (id === DOCUMENT_ID)
				return document;
			else if (id === HEAD_ID)
				return document.head;
			else if (id === BODY_ID)
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