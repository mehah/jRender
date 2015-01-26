/*
 * - Simulador de Socket Real-Time. Por: Renato Machado dos Santos
 */
var Comet = function(url) {
	if(url == null) {
		return {
			LONG_POLLING : 1,
			STREAMING : 2,

			IframeHttpRequest : 1,
			XMLHttpRequest : 2
		};
	}

	var o = this,
		ajaxRequest = null,
		data = null,
		async = true,
		methodRequest = "GET",
		reconnectDelay = 500,
		cometType = Comet().LONG_POLLING,
		reconnect = true,
		txtCheckStatus = ',',
		closed = true,
		onError = null,
		onAbort = null,
		eventReconnect = null,
		state = 0,
		forceConnectType = null,
		jsonContentType = true,
		charset = 'UTF-8',
		UNSENT = 0,
		OPENED = 1,
		HEADERS_RECEIVED = 2,
		LOADING = 3,
		DONE = 4;

	/* var useXDR = false; */

	this.reconnect = function(b) {
		if(b == null)
			return reconnect;
		reconnect = b;
	};

	this.jsonContentType = function(b) {
		if(b == null)
			return jsonContentType;
		jsonContentType = b;
	};

	this.setCometType = function(c) {
		cometType = c;
	};
	this.getCometType = function() {
		return cometType;
	};

	this.setCharset = function(d) {
		charset = d;
	};
	this.getCharset = function() {
		return charset;
	};

	this.setMethodRequest = function(m) {
		methodRequest = m;
	};
	this.getMethodRequest = function() {
		return methodRequest;
	};

	this.setAsync = function(v) {
		async = v;
	};
	this.isAsync = function() {
		return async;
	};

	this.getURL = function() {
		return url;
	};
	this.setURL = function(urll) {
		url = urll;
	};

	this.getState = function() {
		return state;
	};

	this.abort = function() {
		ajaxRequest.abort();
		this.onAbort();
	};

	this.isClosed = function() {
		return closed;
	};

	this.forceConnectType = function(type) {
		forceConnectType = type;
	};

	this.send = function(p, c1, c2) {
		if(ajaxRequest === null) {
			var useIframe = false;
			if(forceConnectType != null) {
				if(forceConnectType === Comet().IframeHttpRequest)
					useIframe = true;
			} else if(this.getCometType() === Comet().STREAMING && window.ActiveXObject != null)
				useIframe = true;

			if(useIframe)
				ajaxRequest = new IframeHttpRequest();
			else {
				try {
					ajaxRequest = new XMLHttpRequest();
				} catch(e) {
					try {
						ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
					} catch(e) {
						alert("Não foi possivel abrir uma instancia de conexão!");
						return false;
					}
				}
			}
		}

		data = p != null && data == null ? p : {};

		data.__contentIsHtml = !jsonContentType;

		if(closed === true) {
			var hasContent = !(/^(?:GET|HEAD)$/.test(methodRequest)), parameters = "";
			if(!(ajaxRequest instanceof IframeHttpRequest) && data != null) {
				if(data instanceof Object) {
					data = Greencode.jQuery.param(data);
					if(!hasContent)
						parameters = (parameters.indexOf('?') === -1 ? '?' : '&') + data;
				}
			}
			var newURL = url + parameters;

			ajaxRequest.open(methodRequest, newURL, async);
			if(hasContent)
				ajaxRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + charset);

			ajaxRequest.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			newURL = null;
			closed = false;
		}

		if(c1 != null)
			this.onMessage(c1, c2);

		ajaxRequest.send(data != null ? data : null);
	};

	this.onAbort = function(c) {
		if(c == null) {
			clearTimeout(eventReconnect);
			eventReconnect = null;

			closed = true;
			if(onAbort != null)
				onAbort.call(o);
		} else
			onAbort = c;
	};
	this.onError = function(c) {
		onError = c;
	};

	var erroEvent = function() {
		clearTimeout(eventReconnect);
		eventReconnect = null;

		closed = true;
		if(onError != null)
			onError.call(o);
	};

	function isArray(value) {
		return value && typeof value === 'object' && Object.prototype.toString.call(value) == '[object Array]';
	}

	this.onMessage = function(c1, c2) {
		var ultLength = null;
		ajaxRequest.onreadystatechange = null;

		var o = this;

		Greencode.crossbrowser.registerEvent.call(window, 'unload', function() {
			o.abort();
		});

		ajaxRequest.onreadystatechange = function() {
			state = this.readyState;

			if(this.readyState === UNSENT)
				o.onAbort();
			else if(o.getCometType() === Comet().LONG_POLLING && this.readyState === DONE) {
				if(this.status !== 200)
					erroEvent();
				else {
					var reconnectByReturn = c1.call(o, jsonContentType ? JSON.parse(ajaxRequest.responseText)
							: ajaxRequest.responseText);

					closed = true;
					if(o.reconnect() === true && reconnectByReturn !== false) {
						eventReconnect = setTimeout(function() {
							o.send(null, c1);
						}, reconnectDelay);
						return;
					}
				}
			} else if(o.getCometType() === Comet().STREAMING) {
				var txt = ajaxRequest.responseText, isIframe = ajaxRequest instanceof IframeHttpRequest, data = null;
				
				if(!isIframe) {
					// Fix for Chrome
					if(__isChrome) {
						var posJsonContent = txt.indexOf('class="JSON_CONTENT"');
						if(posJsonContent != -1 && txt.indexOf('</div j>', posJsonContent) == -1)
							return;
					}
					
					txt = txt.substring(ultLength);
				}
				
				if(jsonContentType) {
					/*
					 * Remover todas as ',' desnecessárias.
					 */
					txt = txt.replace(/^\,+|\,+$/g, "");

					var pos = 0;
					while((pos = txt.indexOf('},,')) != -1) {
						var t = txt.substring(0, pos + 2);
						txt = t + txt.substring(t.length + 1, txt.length);
					}

					var isArray = false;

					if(txt.length !== 0) {
						try {
							data = JSON.parse(txt);
						} catch(e) {
							try {
								data = JSON.parse('[' + txt + ']');
								isArray = true;
							} catch(e) {
								if(typeof console != 'undefined') {
									console.log(e);
									console.log('[' + txt + ']');
								}
								ajaxRequest.abort();
								return;
							}
						}
					}
				} else
					data = txt;
				
				if(this.readyState === DONE) {
					if(this.status !== 200)
						erroEvent();
					else {
						var reconnectByReturn = true;

						if(c2 != null)
							reconnectByReturn = c2.call(o, data);

						closed = true;
						if(o.reconnect() === true && reconnectByReturn !== false) {
							eventReconnect = setTimeout(function() {
								o.send(null, c1, c2);
							}, reconnectDelay);

							return;
						}
					}
				} else if(this.readyState === LOADING || this.readyState === HEADERS_RECEIVED) {
					if(txt.length !== 0) {
						if(isArray) {
							for( var i in data)
								c1.call(o, data[i]);
						} else
							c1.call(o, data);
					}
				}

				if(!isIframe)
					ultLength = ajaxRequest.responseText.length;
			}
		};
	};
};