Greencode.core = {
	__isFirefox: window.mozInnerScreenX != null,
	analizeJSON: function(mainElement, j, target) {
		if (j && (Greencode.jQuery.isPlainObject(j) || Greencode.jQuery.isArray(j))) {
			
			var f = function(p) {
				if (p) {
					if (p.url && p.requestMethod) {
						j[i] = function(event) {
							if (event == null)
								event = {
									type: p.url || 'undefined'
								};
							
							Greencode.core.callRequestMethod(mainElement, event.target || target || {}, event, p, arguments);
						};
					} else
						Greencode.core.analizeJSON(mainElement, p, target || {});
				}
			};
			
			for(var i = -1; ++i < j.length;) {
				f(j[i]);
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
				methodParameters += typeof o === 'string' && o.indexOf('$:') == 0 ? 'Greencode.cache.getById(' + o.substring(2) + ', mainElement)' : variableName + '[' + i + ']';
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
		var objectEvent = Greencode.core.getObjectByEvent(target, event.type);
		
		if (objectEvent.processing === true) {
			objectEvent.methods.push(function() {
				Greencode.core.callRequestMethod(mainElement, target, event, p, __arguments);
			});
			
			return false;
		}
		
		if (p.url[0] === '#') {
			var methodParameters = '';
			if (p.parametersRequest != null)
				methodParameters = Greencode.core.analizeParameters(p.methodParameters, "p.methodParameters");
			
			if (__arguments != null && __arguments.length > 0) {
				if (methodParameters !== '') {
					for( var i in __arguments) {
						methodParameters += ',__arguments[' + i + ']';
					}
				}
			}
			
			if (Greencode.core.__isFirefox)
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
					if (o.className == Greencode.className.greenContext) {
						param._args.push(JSON.stringify(o));
					} else if (o.className == Greencode.className.containerElement || o.className == Greencode.className.containerEventObject) {
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
					} else if (o.className == Greencode.className.element) {
						param._args.push(JSON.stringify({
							className: o.className,
							castTo: o.castTo,
							uid: Greencode.cache.register(target) + ''
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
							if (Greencode.util.isElement(value))
								_arg.fields[name] = {
									create: false
								};
							else if (value != null && !Greencode.jQuery.isFunction(value) && !Greencode.util.isElement(value))
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
			
			if (Greencode.executeEvent('beforeEvent', _data) !== false) {
				var request = new Request(p.url, Greencode.EVENT_REQUEST_TYPE, Greencode.isRequestSingleton());
				request.setMethodRequest(p.requestMethod);
				request.setCometType(Request.STREAMING);
				request.reconnect(false);
				
				request.send(param, function(data) {
					Greencode.core.processJSON(this, mainElement, data, __arguments);
				}, function(data) {
					Greencode.core.processJSON(this, mainElement, data, __arguments);
					
					objectEvent.processing = false;
					
					if (event.onComplete != null) {
						event.onComplete();
					}
					
					if (objectEvent.methods.length > 0) {
						objectEvent.methods[0]();
						objectEvent.methods.splice(0, 1);
					}
					
					_data.serverCallback = data;
					Greencode.executeEvent('afterEvent', _data);
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
		var e = Greencode.cache.getById(this.uid, mainElement);
		
		if (e == null)
			return;
		
		var parameters = '';
		
		if (this.parameters != null) {
			Greencode.core.analizeJSON(mainElement, this.parameters, e);
			parameters = Greencode.core.analizeParameters(this.parameters, "this.parameters", mainElement);
		}
		
		var strEval = null;
		if (this.uidSave) {
			if (Greencode.executorType.METHOD === this.type) {
				if (this.uidSave.length > 1) {
					var res = Greencode.core.__isFirefox ?
						new Function('var e = arguments[0]; var mainElement = arguments[1]; return '
							+ Greencode.core.commandToString(this.name, parameters)).call(this, e, mainElement)
						:
						eval(Greencode.core.commandToString(this.name, parameters));
					
					for(var i = -1; ++i < this.uidSave.length;) {
						Greencode.cache.register(this.uidSave[i], res[i]);
					}
				} else
					strEval = 'Greencode.cache.register(' + this.uidSave[0] + ', ' + Greencode.core.commandToString(this.name, parameters) + ');';
			} else if (Greencode.executorType.PROPERTY === this.type) {
				strEval = 'Greencode.cache.register(' + this.uidSave[0] + ', e.' + this.name + ');';
			} else if (Greencode.executorType.VECTOR === this.type) {
				strEval = 'Greencode.cache.register(' + this.uidSave[0] + ', e[' + this.name + ']' + ');';
			} else if (Greencode.executorType.INSTANCE === this.type) {
				strEval = 'Greencode.cache.register(' + this.uidSave[0] + ', new ' + Greencode.core.commandToString(this.name, parameters) + ');';
			}
		} else if (Greencode.executorType.PROPERTY === this.type) {
			strEval = 'e.' + this.name + '=' + parameters + ';';
		} else {
			strEval = Greencode.core.commandToString(this.name, parameters);
		}
		
		if (strEval != null) {
			try {
				var res = Greencode.core.__isFirefox ? new Function('var e = arguments[0]; var mainElement = arguments[1]; return ' + strEval).call(this, e, mainElement) : eval(strEval);
				
				if (Greencode.DEBUG_LOG) {
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
							Greencode.core.processJSON(request, mainElement, JSON.parse(txt));
							Greencode.executeEvent('init');
						}
					} catch (e) {
					}
					
					jsonDiv.parentNode.removeChild(jsonDiv);
				}
			}
			return;
		}
		
		if (!Greencode.jQuery.isArray(__jsonObject))
			__jsonObject = [ __jsonObject ];
		
		for( var i in __jsonObject) {
			var jsonObject = __jsonObject[i];
			
			if (Greencode.DEBUG_LOG) {
				console.warn('-----------------------');
				console.warn('MainElement: ', mainElement);
				console.warn('JSON Object: ', jsonObject);
			}
			
			var hasArgsEvent = argsEvent != null && argsEvent.length > 0 && jsonObject.args != null && jsonObject.args.length > 0;
			if (hasArgsEvent) {
				for(var i = -1; ++i < jsonObject.args.length;)
					Greencode.cache.register(jsonObject.args[i], argsEvent[i]);
			}
			
			if (jsonObject.comm != null && jsonObject.comm.length > 0) {
				for(var i = -1; ++i < jsonObject.comm.length;) {
					Greencode.core.readCommand.call(jsonObject.comm[i], mainElement);
				}
				
				if (!Greencode.DEBUG_LOG)
					delete jsonObject.comm;
			}
			
			if (hasArgsEvent) {
				for(var i = -1; ++i < jsonObject.args.length;)
					Greencode.cache.remove(jsonObject.args[i]);
			}
			
			if (jsonObject.sync != null) {
				var list = {}, __request, url = Greencode.CONTEXT_PATH + '/$synchronize';
				
				if (request != null && request.isWebSocket()) {
					__request = request;
					__request.setURL(url)
				} else {
					__request = new Request(url, Greencode.EVENT_REQUEST_TYPE, Greencode.isRequestSingleton());
					
					__request.setMethodRequest("post");
					__request.setCometType(Request.LONG_POLLING);
					__request.reconnect(false);
				}
				
				for(var sI = -1; ++sI < jsonObject.sync.list.length;) {
					var sync = jsonObject.sync.list[sI], e = Greencode.cache.getById(sync.uid, mainElement);
					
					if (e != null) {
						Greencode.core.analizeJSON(mainElement, sync.command.parameters, e);
						
						var p = sync.command.parameters, parameters = Greencode.core.analizeParameters(p, "p", mainElement), value, strEval = '', filter, forceArray = false;
						
						if (!list[sync.uid + ""])
							list[sync.uid + ""] = [];
						
						/*
						 * TODO: Refazer sincronismo para arquivos
						 */
						if (sync.command.name.indexOf('__partFile') > -1) {
							value = e;
						} else {
							if (sync.command.name === "#") {
								value = {};
								for( var i in p)
									value[i] = e[p[i]];
							} else {
								if (Greencode.executorType.METHOD === sync.command.type) {
									strEval = Greencode.core.commandToString(sync.command.name, parameters);
								} else {
									if (Greencode.executorType.VECTOR === sync.command.type) {
										filter = p;
										forceArray = true;
									} else if (Greencode.executorType.PROPERTY === sync.command.type) {
										filter = p;
									}
									
									strEval = "e." + sync.command.name;
								}
								
								value = Greencode.core.__isFirefox ? new Function('var e = arguments[0]; var p = arguments[1]; return ' + strEval)(e, p) : eval(strEval);
							}
							
							if (forceArray || Greencode.jQuery.isArray(value))
								value = Greencode.util.arrayToString(value, filter);
							else if (typeof value === "object")
								value = Greencode.util.objectToString(value, filter);
						}
						
						list[sync.uid + ""].push({
							name: sync.varName,
							'var': value + "",
							cast: sync.command.cast,
						});
					}
				}
				
				var data = {
					viewId: jsonObject.sync.viewId,
					cid: jsonObject.sync.cid,
					set: jsonObject.sync.set,
					list: JSON.stringify(list)
				};
				
				if (!data.set) {
					data.accessCode = jsonObject.sync.accessCode;
				}
				
				__request.send(data, null, null);
				
				__request = null;
				
				if (!Greencode.DEBUG_LOG)
					delete jsonObject.sync;
			}
			
			if (jsonObject.error != null) {
				Greencode.modalErro.show(jsonObject.error);
				if (!Greencode.DEBUG_LOG)
					delete jsonObject.error;
			}
			
			jsonObject = null;
		}
		
		__jsonObject = null;
		
		Greencode.tags.process(mainElement);
		
		var res = mainElement.querySelectorAll('[msg\\:key]');
		for(var i = -1; ++i < res.length;) {
			var e = res[i], msg = Greencode.internationalProperty[e.getAttribute('msg:key')];
			
			if (e.hasAttribute('msg:appendText'))
				msg += e.getAttribute('msg:appendText');
			
			if (e.tagName === 'INPUT')
				e.value = msg;
			else
				e.innerHTML = msg;
		}
	}
};

window.registerEvent('load', function() {
	if (window.location.hash.indexOf('#!') === 0) {
		window.location.href = Greencode.CONTEXT_PATH + '/' + window.location.hash.substring(2);
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
						Greencode.executeEvent('scriptLoad', {
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
	
	Greencode.core.processJSON(null, document.body);
	
	window.registerEvent('popstate', function(e) {
		if (e.state != null && e.state.selector != null) {
			Greencode.executeEvent('beforePopstate');
			var o = e.state.selector == 'body' ? document.body : document.body.querySelector(e.state.selector);
			o.empty();
			var tags = Greencode.cache.tags[window.location.href];
			for( var i in tags)
				o.appendChild(tags[i]);
			Greencode.executeEvent('afterPopstate');
		}
	});
	
	var _data = {
		mainElement: document.body
	};
	
	Greencode.executeEvent('pageLoad', _data);
});