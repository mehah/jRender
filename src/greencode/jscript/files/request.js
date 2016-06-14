/*
 * - Simulador de Socket Real-Time. Por: Renato Machado dos Santos
 */

var Request = function(url, type, isSingleton) {
	var o = this,
	_request = null,
	eventId = null,
	data = null,
	methodRequest = "GET",
	reconnectDelay = 500,
	cometType = Request.LONG_POLLING,
	reconnect = true,
	txtCheckStatus = ',',
	closed = true,
	onError = null,
	onAbort = null,
	eventReconnect = null,
	state = 0,
	jsonContentType = true,
	charset = 'UTF-8',
	UNSENT = 0,
	OPENED = 1,
	HEADERS_RECEIVED = 2,
	LOADING = 3,
	DONE = 4;
	
	this.reconnect = function(b) {
		if (b == null)
			return reconnect;
		reconnect = b;
	};
	
	this.jsonContentType = function(b) {
		if (b == null)
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
		_request.abort();
		this.onAbort();
	};
	
	this.isClosed = function() {
		return closed;
	};
	
	this.isWebSocket = function() {
		return _request instanceof WebSocket;
	};
	
	var sendRequestWebsocket = function(data, eventId) {
		setTimeout(function() {
			if (_request.readyState == WebSocket.CONNECTING || _request.listEvents && !(Object.keys(_request.listEvents)[0] == eventId) && !data.params.set) {
				sendRequestWebsocket(data, eventId);
			} else if (_request.readyState == WebSocket.OPEN) {
				_request.send(data ? JSON.stringify(data) : null);
			}
		}, 1);
	};
	
	var websocket_url = "ws://" + window.location.host + Greencode.CONTEXT_PATH + "/coreWebSocket";
	
	this.send = function(p, c1, c2) {
		data = p != null ? p : {};
		data.__contentIsHtml = !jsonContentType;
		
		var isAutoDetection = type == 'auto';
		
		if (window.WebSocket != null && (isAutoDetection || type == 'websocket')) {
			if (isSingleton) {
				if ((_request = Request.instance) == null) {
					Request.instance = _request = new WebSocket(websocket_url);
					
					_request.listEvents = {};
					
					var _strRequestClose = "{-websocket-close-:";
					var _strRequestMsg = "{-websocket-msg-:";
					
					_request.onmessage = function(_) {
						var closed, eventId, data;
						if (closed = (_.data.indexOf(_strRequestClose) != -1)) {
							eventId = _.data.substring(_strRequestClose.length);
							eventId = parseInt(eventId.substring(0, eventId.indexOf('}')));
							data = "";
						} else {
							eventId = _.data.substring(_strRequestMsg.length);
							eventId = parseInt(eventId.substring(0, eventId.indexOf('}')));
							data = _.data.substring(_.data.indexOf('}') + 1);
						}
						
						try {
							data = JSON.parse(data);
						} catch (e) {
						}
						
						var event = _request.listEvents[eventId];
						
						if (closed) {
							if (event.close) {
								event.close.call(o, data);
							}
							
							delete _request.listEvents[eventId];
							event.request.closed = true;
						} else if (event.msg) {
							event.msg.call(o, data);
						}
					};
				}
				
				_request.listEvents[eventId = ++Request.lastEventId] = {
					request: o,
					msg: c1,
					close: c2
				};
			} else {
				_request = new WebSocket(websocket_url);
				
				if (c1 != null) {
					_request.onmessage = function(_) {
						var data = _.data ? (jsonContentType ? JSON.parse(_.data) : _.data) : "";
						c1.call(o, data);
					};
				}
				
				if (c2 != null) {
					_request.onclose = function(_) {
						var data = _.data ? (jsonContentType ? JSON.parse(_.data) : _.data) : "";
						c2.call(o, data);
					};
				}
			}
			
			for( var i in data) {
				var v = data[i];
				if (!isArray(v)) {
					data[i] = [ v ]
				}
			}
			
			sendRequestWebsocket({
				params: data,
				url: url,
				eventId: eventId
			}, eventId);
			return;
		}
		
		if (_request === null) {
			if (isAutoDetection || type == 'iframe') {
				_request = new IframeHttpRequest();
			} else {
				try {
					_request = new XMLHttpRequest();
				} catch (e) {
					try {
						_request = new ActiveXObject("Microsoft.XMLHTTP");
					} catch (e) {
						alert("Não foi possivel abrir uma instancia de conexão!");
						return false;
					}
				}
			}
		}
		
		if (closed === true) {
			if (!this.isWebSocket()) {
				var hasContent = !(/^(?:GET|HEAD)$/.test(methodRequest)), parameters = "";
				if (!(_request instanceof IframeHttpRequest)) {
					if (data instanceof Object) {
						data = Greencode.jQuery.param(data);
						if (!hasContent)
							parameters = (parameters.indexOf('?') === -1 ? '?' : '&') + data;
					}
				}
				
				_request.open(methodRequest, url + parameters, true);
				if (hasContent)
					_request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + charset);
				
				_request.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			}
			
			closed = false;
		}
		
		if (c1 != null) {
			this.onMessage(c1, c2);
		}
		
		_request.send(data);
	};
	
	this.onAbort = function(c) {
		if (this.isWebSocket()) {
			_request.onclose = c;
		} else {
			if (c == null) {
				clearTimeout(eventReconnect);
				eventReconnect = null;
				
				closed = true;
				if (onAbort != null)
					onAbort.call(o);
			} else
				onAbort = c;
		}
		
	};
	
	this.onError = function(c) {
		if (this.isWebSocket()) {
			_request.onerror = c;
		} else
			onError = c;
	};
	
	var erroEvent = function() {
		clearTimeout(eventReconnect);
		eventReconnect = null;
		
		closed = true;
		if (onError != null)
			onError.call(o);
	};
	
	function isArray(value) {
		return value && typeof value === 'object' && Object.prototype.toString.call(value) == '[object Array]';
	}
	
	this.onMessage = function(c1, c2) {
		_request.onreadystatechange = null;
		var ultLength = 0, o = this;
		
		window.registerEvent('unload', function() {
			o.abort();
		});
		
		_request.onreadystatechange = function() {
			state = this.readyState;
			
			if (this.readyState === UNSENT)
				o.onAbort();
			else if (o.getCometType() === Request.LONG_POLLING && this.readyState === DONE) {
				if (this.status !== 200)
					erroEvent();
				else {
					var data = jsonContentType ? JSON.parse(_request.responseText) : _request.responseText;
					c1.call(o, data);
					
					var reconnectByReturn = c2.call(o, data);
					
					closed = true;
					if (o.reconnect() === true && reconnectByReturn !== false) {
						eventReconnect = setTimeout(function() {
							o.send(null, c1);
						}, reconnectDelay);
						return;
					}
				}
			} else if (o.getCometType() === Request.STREAMING) {
				var txt = _request.responseText, isIframe = _request instanceof IframeHttpRequest, data = null, isArray = false;
				
				if (!isIframe && ultLength) {
					txt = txt.substring(ultLength);
				}
				
				if (!jsonContentType) {
					if ((txt.split(/<ajaxcontent>|<\/ajaxcontent>/gi).length - 1) % 2 != 0)
						return;
				}
				
				/*
				 * Performance test: http://jsperf.com/count-number-of-matches/4
				 */
				if ((txt.split(/<json|<\/json>/gi).length - 1) % 2 != 0)
					return;
				
				if (!isIframe) {
					ultLength = _request.responseText.length;
				}
				
				/*
				 * Remover todas as ',' desnecessárias.
				 */
				txt = txt.replace(/^\,+|\,+$/g, "");
				
				if (jsonContentType) {
					isArray = true;
					data = [];
					
					if (txt.length > 0) {
						try {
							var div = document.createElement('div');
							div.innerHTML = txt;
							
							var es = div.getElementsByTagName('json');
							for(var i = -1; ++i < es.length;) {
								var jsonTxt = es[i].childTextConent();
								if (jsonTxt.length > 0) {
									data.push(JSON.parse(jsonTxt));
								}
							}
							div = null;
						} catch (e) {
							if (typeof console != 'undefined') {
								console.log(e);
								console.log(txt);
							}
							_request.abort();
							return;
						}
					}
				} else {
					data = txt.replace(/<ajaxcontent>|<\/ajaxcontent>/gi, '');
				}
				
				if (this.readyState === DONE) {
					if (this.status !== 200)
						erroEvent();
					else {
						var reconnectByReturn = true;
						
						if (c2 != null)
							reconnectByReturn = c2.call(o, data);
						
						closed = true;
						if (o.reconnect() === true && reconnectByReturn !== false) {
							eventReconnect = setTimeout(function() {
								o.send(null, c1, c2);
							}, reconnectDelay);
							
							return;
						}
					}
				} else if (this.readyState === LOADING || this.readyState === HEADERS_RECEIVED) {
					if (data.length > 0) {
						if (isIframe) {
							_request.clear();
						}
						
						if (isArray) {
							for(var i = -1; ++i < data.length;) {
								c1.call(o, data[i]);
							}
						} else
							c1.call(o, data);
					}
				}
			}
		};
	};
};

Request.lastEventId = 0;
Request.instance = null;

Request.LONG_POLLING = 1;
Request.STREAMING = 2;

Request.IframeHttpRequest = 1;
Request.XMLHttpRequest = 2;
Request.WEBSOCKET = 3;