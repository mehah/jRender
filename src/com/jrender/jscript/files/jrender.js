var JRender = {
	internationalProperty: {},
	exec: function(f) {
		f();
	},
	cache: {
		lastUID: 1000,
		references: {},
		tags: {},
		generateUID: function() {
			while (this.references[++this.lastUID])
			;
			return this.lastUID;
		},
		getById: function(id, mainElement) {
			if (id === JRender.MAIN_ELEMENT_ID)
				return mainElement;
			else if (id === JRender.WINDOW_ID)
				return window;
			else if (id === JRender.DOCUMENT_ID)
				return document;
			else if (id === JRender.HEAD_ID)
				return document.head;
			else if (id === JRender.BODY_ID)
				return document.body;

			return this.references[id];
		},
		register: function(uid, o) {
			if (uid instanceof Node) {
				o = uid;
				if (uid = o.getAttribute('uid'))
					return parseInt(uid);

				uid = JRender.cache.generateUID();
				o.setAttribute('uid', uid);
			}

			this.references[uid] = o;
			return uid;
		},
		remove: function(uid) {
			delete this.references[uid];
		}
	},
	isWebsocket: function() {
		return this.EVENT_REQUEST_TYPE == 'auto' && window.WebSocket != null || this.EVENT_REQUEST_TYPE == 'websocket'
	},
	isRequestSingleton: function() {
		return this.REQUEST_SINGLETON && this.isWebsocket();
	},
	getRealURLPath: function(url) {
		if (JRender.isWebsocket() && url.indexOf(JRender.CONTEXT_PATH) == -1) {
			var _url = window.location.pathname.substring(JRender.CONTEXT_PATH.length + 1);
			var folders = _url.substring(0, _url.lastIndexOf('/') + 1);
			url = folders + url;

			if (url.indexOf('../') != -1) {
				folders = url.split('/');
				for (var i = -1; ++i < folders.length;) {
					var folder = folders[i];
					if (folder === '..') {
						folders.splice(--i, 2);
						--i;
					}
				}
				url = folders.join('/');
			}
		}

		return url;
	}
};