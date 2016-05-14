var Greencode = {
	MAIN_ELEMENT_ID: 1, WINDOW_ID: 2, DOCUMENT_ID: 3, HEAD_ID: 4, BODY_ID: 5,
	cache : {
		lastUID: 1000,
		references : {},
		generateUID: function() {
			while(Greencode.cache.references[++Greencode.cache.lastUID]);		
			return Greencode.cache.lastUID;
		},
		getById : function(id, mainElement) {
			if (id === Greencode.MAIN_ELEMENT_ID)
				return mainElement;
			else if (id === Greencode.WINDOW_ID)
				return window;
			else if (id === Greencode.DOCUMENT_ID)
				return document;
			else if (id === Greencode.HEAD_ID)
				return document.head;
			else if (id === Greencode.BODY_ID)
				return document.body;
			
			return Greencode.cache.references[id];
		},		
		register: function(uid, o) {
			if(uid instanceof Node) {
				o = uid
				if(uid = o.getAttribute('uid'))
					return parseInt(uid)
				
				uid = Greencode.cache.generateUID();
				o.setAttribute('uid', uid)
			}
			
			Greencode.cache.references[uid] = o;			
			return uid;
		},
		remove: function(uid) {
			delete Greencode.cache.references[uid];
		}
	},
	isRequestSingleton: function() {
		return Greencode.REQUEST_SINGLETON && (Greencode.EVENT_REQUEST_TYPE == 'auto' && window.WebSocket != null || Greencode.EVENT_REQUEST_TYPE == 'websocket');
	}
};