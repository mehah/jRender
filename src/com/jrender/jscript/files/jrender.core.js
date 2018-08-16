JRender.core = {
	__isFirefox: window.mozInnerScreenX != null,
	analizeJSON: function(mainElement, j, target) {
		if (j && (JRender.jQuery.isPlainObject(j) || JRender.jQuery.isArray(j))) {
			var f = function(p, i) {
				if (p) {
					if (p.url && p.requestMethod) {
						j[i] = function(event) {
							if (event == null) {
								event = {
									type: p.url || 'undefined'
								};
							} else {
								if (event.pd) {
									event.preventDefault();
								}
								
								if (event.sp) {
									event.stopPropagation();
								}
							}
							
							JRender.core.callRequestMethod(mainElement, event.target || target || {}, event, p, arguments);
						};
					} else
						JRender.core.analizeJSON(mainElement, p, target || {});
				}
			};
			
			for(i in j) {
				f(j[i], i);
			}
		}
	},
	analizeParameters: function(parameters, variableName, mainElement) {
		var methodParameters = "";
		if (parameters != null) {
			for(var i = -1; ++i < parameters.length;) {
				if (i != 0)
					methodParameters += ',';
				
				var o = parameters[i];
				methodParameters += typeof o === 'string' && o.indexOf('$:') == 0 ? 'JRender.cache.getById(' + o.substring(2) + ', mainElement)' : variableName + '[' + i + ']';
			}
		}
		
		return methodParameters;
	},
	getObjectByEvent: function(o, name) {
		if (o.events == null)
			o.events = {};
		
		if (o.events[name] == null) {
			o.events[name] = {
				processing: false,
				methods: new Array()
			};
		}
		
		return o.events[name];
	},
	callRequestMethod: function(mainElement, target, event, p, __arguments) {
		var objectEvent = JRender.core.getObjectByEvent(target, event.type);
		
		if (objectEvent.processing === true) {
			objectEvent.methods.push(function() {
				JRender.core.callRequestMethod(mainElement, target, event, p, __arguments);
			});
			
			return false;
		}
		
		if (p.url[0] === '#') {
			var methodParameters = '';
			if (p.parametersRequest != null)
				methodParameters = JRender.core.analizeParameters(p.methodParameters, "p.methodParameters");
			
			if (__arguments != null && __arguments.length > 0) {
				if (methodParameters !== '') {
					for( var i in __arguments) {
						methodParameters += ',__arguments[' + i + ']';
					}
				}
			}
			
			if (JRender.core.__isFirefox)
				new Function('return ' + p.url.substring(1) + '(' + methodParameters + ')')();
			else
				eval(p.url.substring(1) + '(' + methodParameters + ')');
		} else {
			objectEvent.processing = true;
			
			var param = {}, form = null, formName = null;
			
			if (p.requestParameters) {
				for( var i in p.requestParameters) {
					param[i] = p.requestParameters[i];
				}
			}
			if (p.args != null) {
				param._args = [];
				
				for( var i in p.args) {
					var o = p.args[i];
					if (o.className == JRender.className.context) {
						param._args.push(JSON.stringify(o));
					} else if (o.className == JRender.className.containerElement || o.className == JRender.className.containerEventObject) {
						var uid;
						if (target.tagName === 'CONTAINER') {
							for( var i2 in __arguments) {
								if ((uid = __arguments[i2].__containerUID) != null)
									break;
							}
						} else {
							var c = target.getParentByTagName("container");
							uid = c ? c.getAttribute('uid') : null;
						}
						
						param._args.push(JSON.stringify({
							className: o.className,
							uid: uid + ''
						}));
					} else if (o.className == JRender.className.element) {
						param._args.push(JSON.stringify({
							className: o.className,
							castTo: o.castTo,
							uid: JRender.cache.register(target) + ''
						}));
					} else {
						var _arg = {
							className: o.className,
							fields: {}
						};
						
						var arg = __arguments[i - param._args.length];
						
						for(var i = -1; ++i < o.fields.length;) {
							var name = o.fields[i];
							var value = arg[name];
							if (JRender.util.isElement(value))
								_arg.fields[name] = {
									create: false
								};
							else if (value != null && !JRender.jQuery.isFunction(value) && !JRender.util.isElement(value))
								_arg.fields[name] = value;
						}
						
						_arg.fields = JSON.stringify(_arg.fields);
						param._args.push(JSON.stringify(_arg));
					}
				}
			}
			
			if (p.formName != null) {
				form = mainElement.querySelector('form[name="' + p.formName + '"]');
				if (form == null && typeof console != 'undefined')
					console.warn("Could not find the form with name " + p.formName + ".");
				else
					formName = p.formName;
			} else if ((form = target.form) != null || !(target instanceof Window) && (form = target.getParentByTagName("form")) != null) {
				formName = form.getAttribute("name");
			}
			
			if (form != null) {
				var buildParam = function(param, list) {
					for( var i in list) {
						var res = list[i], value = null, name = null;
						
						if (res instanceof Array) {
							var eFirst = res[0];
							
							if (eFirst instanceof Node) {
								name = eFirst.name;
								var isCheckBox = eFirst.type === "checkbox", values = null;
								if (isCheckBox)
									values = new Array();
								
								for(var i2 = -1; ++i2 < res.length;) {
									var e = res[i2];
									if (e.checked) {
										if (isCheckBox)
											values.push(e.value);
										else {
											values = e.value;
											break;
										}
									}
								}
								
								if (values != null) {
									if (values.length > 1)
										value = values;
									else
										value = values[0];
								} else
									value = null;
							} else {
								name = i;
								value = param[name];
								
								var first = false;
								if (!value) {
									value = new Array();
									value.isContainer = true;
									first = true;
								}
								
								for(var i2 = -1; ++i2 < res.length;) {
									var e = res[i2];
									var o = buildParam({
										__uid: e.__container.getAttribute('uid')
									}, e);
									value.push(o);
								}
								
								if (first)
									param[name] = value;
								
								continue;
							}
						} else {
							var eFirst = res;
							name = eFirst.name;
							
							if (eFirst.tagName === "SELECT") {
								if (eFirst.multiple) {
									var values = new Array();
									for(var i2 = -1; ++i2 < eFirst.options.length;) {
										var option = eFirst.options[i2];
										if (option.selected)
											values.push(option.value);
									}
									value = values.length > 0 ? values : null;
								} else
									value = eFirst.selectedIndex === -1 ? "" : eFirst.options[eFirst.selectedIndex].value;
							} else
								value = eFirst.tagName === "INPUT" && eFirst.type === "file" ? eFirst : eFirst.value;
						}
						
						if (value)
							param[name] = value;
					}
					
					return param;
				};
				
				var list = form.getAllDataElements();
				buildParam(param, list);
				
				for( var i in param) {
					if (i === '_args')
						continue;
					
					var v = param[i];
					if (v.isContainer)
						param[i] = JSON.stringify(v);
				}
				
				param.__requestedForm = formName;
			}
			
			param.cid = p.cid;
			param.viewId = p.viewId;
			param.eventType = event.type;
			param._buttonId = target.id || target.name || 'Window';
			
			var _data = {
				mainElement: mainElement,
				args: __arguments,
				paramenters: param
			};
			
			if (JRender.executeEvent('beforeEvent', _data) !== false) {
				var hasFile = false;
				if (JRender.EVENT_REQUEST_TYPE !== 'iframe') {
					for( var i in param) {
						var v = param[i];
						if (v.type && v.type === 'file') {
							hasFile = true;
							break;
						}
					}
				}
				
				var request = new Request(p.url, hasFile ? 'iframe' : JRender.EVENT_REQUEST_TYPE, JRender.isRequestSingleton());
				request.setMethodRequest(p.requestMethod);
				request.setCometType(Request.STREAMING);
				request.reconnect(false);
				
				request.send(param, function(data) {
					JRender.core.processJSON(this, mainElement, data, __arguments);
				}, function(data) {
					JRender.core.processJSON(this, mainElement, data, __arguments);
					
					objectEvent.processing = false;
					
					if (event.onComplete != null) {
						event.onComplete();
					}
					
					if (objectEvent.methods.length > 0) {
						objectEvent.methods[0]();
						objectEvent.methods.splice(0, 1);
					}
					
					_data.serverCallback = data;
					JRender.executeEvent('afterEvent', _data);
				});
				
				request = null;
			}
			
			param = null;
			tagEventObject = null;
		}
	},
	commandToString: function(commandName, parameters) {
		return 'e.' + commandName + '(' + parameters + ')';
	},
	readCommand: function(mainElement) {
		var e = JRender.cache.getById(this.uid, mainElement);
		
		if (e == null)
			return;
		
		var parameters = '';
		
		if (this.parameters != null) {
			JRender.core.analizeJSON(mainElement, this.parameters, e);
			parameters = JRender.core.analizeParameters(this.parameters, "this.parameters", mainElement);
		}
		
		var strEval = null;
		if (this.uidSave) {
			if (JRender.executorType.METHOD === this.type) {
				if (this.uidSave.length > 1) {
					var res = JRender.core.__isFirefox ? new Function('var e = arguments[0]; var mainElement = arguments[1]; return ' + JRender.core.commandToString(this.name, parameters)).call(this,
							e, mainElement) : eval(JRender.core.commandToString(this.name, parameters));
					
					for(var i = -1; ++i < this.uidSave.length;) {
						JRender.cache.register(this.uidSave[i], res[i]);
					}
				} else
					strEval = 'JRender.cache.register(' + this.uidSave[0] + ', ' + JRender.core.commandToString(this.name, parameters) + ');';
			} else if (JRender.executorType.PROPERTY === this.type) {
				strEval = 'JRender.cache.register(' + this.uidSave[0] + ', e.' + this.name + ');';
			} else if (JRender.executorType.VECTOR === this.type) {
				strEval = 'JRender.cache.register(' + this.uidSave[0] + ', e[' + this.name + ']' + ');';
			} else if (JRender.executorType.INSTANCE === this.type) {
				strEval = 'JRender.cache.register(' + this.uidSave[0] + ', new ' + JRender.core.commandToString(this.name, parameters) + ');';
			}
		} else if (JRender.executorType.PROPERTY === this.type) {
			strEval = 'e.' + this.name + '=' + parameters + ';';
		} else {
			strEval = JRender.core.commandToString(this.name, parameters);
		}
		
		if (strEval != null) {
			try {
				var res = JRender.core.__isFirefox ? new Function('var e = arguments[0]; var mainElement = arguments[1]; return ' + strEval).call(this, e, mainElement) : eval(strEval);
				
				if (JRender.DEBUG_LOG) {
					console.warn(e);
					console.warn("Code: " + strEval + "\n[Reference]\n", e, this.parameters);
					console.warn("Result: ", res);
				}
			} catch (ex) {
				if (typeof console != 'undefined') {
					console.warn(ex);
					console.warn("Code: " + strEval + "\n[Reference]\n", e, this.parameters);
				}
			}
		}
	},
	processJSON: function(request, mainElement, __jsonObject, argsEvent) {
		
		if (__jsonObject == null) {
			var jsons = mainElement.getElementsByTagName('JSON');
			if (jsons != null && jsons.length > 0) {
				var jsonDiv;
				while (jsonDiv = jsons[0]) {
					try {
						var txt = jsonDiv.childTextConent();
						if (txt.length > 0) {
							JRender.core.processJSON(request, mainElement, JSON.parse(txt));
							JRender.executeEvent('init');
						}
					} catch (e) {
					}
					
					jsonDiv.parentNode.removeChild(jsonDiv);
				}
			}
			return;
		}
		
		if (!JRender.jQuery.isArray(__jsonObject))
			__jsonObject = [ __jsonObject ];
		
		JRender.tags.process(mainElement);
		
		for( var i in __jsonObject) {
			var jsonObject = __jsonObject[i];
			
			if (JRender.DEBUG_LOG) {
				console.warn('-----------------------');
				console.warn('MainElement: ', mainElement);
				console.warn('JSON Object: ', jsonObject);
			}
			
			var hasArgsEvent = argsEvent != null && argsEvent.length > 0 && jsonObject.args != null && jsonObject.args.length > 0;
			if (hasArgsEvent) {
				for(var i = -1; ++i < jsonObject.args.length;)
					JRender.cache.register(jsonObject.args[i], argsEvent[i]);
			}
			
			if (jsonObject.comm != null && jsonObject.comm.length > 0) {
				for(var i = -1; ++i < jsonObject.comm.length;) {
					JRender.core.readCommand.call(jsonObject.comm[i], mainElement);
				}
				
				if (!JRender.DEBUG_LOG)
					delete jsonObject.comm;
			}
			
			if (hasArgsEvent) {
				for(var i = -1; ++i < jsonObject.args.length;)
					JRender.cache.remove(jsonObject.args[i]);
			}
			
			if (jsonObject.sync != null) {
				var list = {}, __request, url = JRender.CONTEXT_PATH + '/$synchronize';
				
				var file = null;
				for(var sI = -1; ++sI < jsonObject.sync.list.length;) {
					var sync = jsonObject.sync.list[sI], e = JRender.cache.getById(sync.uid, mainElement);
					
					if (e != null) {
						JRender.core.analizeJSON(mainElement, sync.command.parameters, e);
						
						var p = sync.command.parameters, parameters = JRender.core.analizeParameters(p, "p", mainElement), value, strEval = '', filter, forceArray = false;

						if (sync.command.cast === "javax.servlet.http.Part") {
							file = {
								value: e,
								uid: sync.uid,
								varName: sync.varName
							};
							break;
						} else {
							if (!list[sync.uid + ""])
								list[sync.uid + ""] = [];
							
							if (sync.command.name === "#") {
								value = {};
								for( var i in p)
									value[i] = e[p[i]];
							} else {
								if (JRender.executorType.METHOD === sync.command.type) {
									strEval = JRender.core.commandToString(sync.command.name, parameters);
								} else {
									if (JRender.executorType.VECTOR === sync.command.type) {
										filter = p;
										forceArray = true;
									} else if (JRender.executorType.PROPERTY === sync.command.type) {
										filter = p;
									}
									
									strEval = "e." + sync.command.name;
								}
								
								value = JRender.core.__isFirefox ? new Function('var e = arguments[0]; var p = arguments[1]; return ' + strEval)(e, p) : eval(strEval);
							}
							
							if (forceArray || JRender.jQuery.isArray(value))
								value = JRender.util.arrayToString(value, filter);
							else if (typeof value === "object")
								value = JRender.util.objectToString(value, filter);
							
							list[sync.uid + ""].push({
								name: sync.varName,
								'var': value + "",
								cast: sync.command.cast,
							});
						}
					}
				}
				
				var data = {
					viewId: jsonObject.sync.viewId,
					cid: jsonObject.sync.cid,
					set: jsonObject.sync.set
				};
				
				if (file !== null) {
					data.fileUID = file.uid;
					data.value = file.value;
					data.varName = file.varName;
				} else {
					data.list = JSON.stringify(list);
				}
				
				if (!data.set) {
					data.accessCode = jsonObject.sync.accessCode;
				}
				
				if (file !== null && request != null && request.isWebSocket()) {
					__request = request;
					__request.setURL(url)
				} else {
					__request = new Request(url, file ? 'iframe' : JRender.EVENT_REQUEST_TYPE, JRender.isRequestSingleton());
					
					__request.setMethodRequest("post");
					__request.setCometType(Request.LONG_POLLING);
					__request.reconnect(false);
				}
				
				__request.send(data, null, null);
				
				__request = null;
				
				if (!JRender.DEBUG_LOG)
					delete jsonObject.sync;
			}
			
			if (jsonObject.error != null) {
				JRender.modalErro.show(jsonObject.error);
				if (!JRender.DEBUG_LOG)
					delete jsonObject.error;
			}
			
			jsonObject = null;
		}
		
		__jsonObject = null;
		
		var res = mainElement.querySelectorAll('[msg\\:key]');
		for(var i = -1; ++i < res.length;) {
			var e = res[i], msg = JRender.internationalProperty[e.getAttribute('msg:key')];
			
			if (e.hasAttribute('msg:appendText'))
				msg += e.getAttribute('msg:appendText');
			
			if (e.tagName === 'INPUT')
				e.value = msg;
			else
				e.innerHTML = msg;
		}
	}
};

var onLoadedEvent = function(event) {
	if (window.location.hash.indexOf('#!') === 0) {
		window.location.href = JRender.CONTEXT_PATH + '/' + window.location.hash.substring(2);
		return;
	}
	
	window.document.write = function(node) {
		var temp = document.createElement('div');
		temp.innerHTML = node;
		var elem;
		while (elem = temp.firstChild) {
			if (elem.tagName === 'SCRIPT') {
				temp.removeChild(elem);
				var script = document.createElement('script'), head = document.getElementsByTagName("head")[0];
				script.setAttribute("type", elem.type ? elem.type : "text/javascript");
				script.setAttribute("src", elem.src);
				
				var done = false;
				script.onload = script.onreadystatechange = function() {
					if (!done && (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
						done = true;
						JRender.executeEvent('scriptLoad', {
							mainElement: document.body
						});
						
						script.onload = script.onreadystatechange = null;
						if (head && script.parentNode) {
							head.removeChild(script);
						}
						script = null;
					}
				};
				
				head.appendChild(script);
			} else
				window.document.body.appendChild(elem);
			elem = null;
		}
		
		temp = null;
	};
	
	JRender.core.processJSON(null, document.body);
	
	window.registerEvent('popstate', function(e) {
		if (e.state != null && e.state.selector != null) {
			JRender.executeEvent('beforePopstate');
			var o = e.state.selector == 'body' ? document.body : document.body.querySelector(e.state.selector);
			o.empty();
			var tags = JRender.cache.tags[window.location.href];
			for( var i in tags)
				o.appendChild(tags[i]);
			JRender.executeEvent('afterPopstate');
		}
	});
	
	var _data = {
		mainElement: document.body
	};
	
	JRender.executeEvent('pageLoad', _data);
};

/* EVENT: ON LOADED PAGE */
if (document.addEventListener) { /* IE 9+, Edge, firefox... */
	document.registerEvent("DOMContentLoaded", onLoadedEvent);
} else { /* IE8 or less */
	window.registerEvent('load', onLoadedEvent);
}