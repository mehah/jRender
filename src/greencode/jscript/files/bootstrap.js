var internationalization_msg = {},
	Bootstrap = {},
	__isFirefox = window.mozInnerScreenX != null,
	__isIE8orLess = document.addEventListener == null,
	__isChrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;

Bootstrap.analizeJSON = function(mainElement, j, target) {
	if(j == null || !Greencode.jQuery.isPlainObject(j) && !Greencode.jQuery.isArray(j))
		return;

	if(target == null)
		target = {};

	Greencode.jQuery.each(j, function(i) {
		var p = this;
		if(p.isFunction === true) {
			j[i] = function(event) {
				if(event == null)
					event = {
						type : p.url || 'undefined'
					};

				Bootstrap.callRequestMethod(mainElement, target, event, p, arguments);
			};
		} else
			Bootstrap.analizeJSON(mainElement, p, target);
	});
};

Bootstrap.analizeParameters = function(parameters, variableName, mainElement) {
	var methodParameters = "";
	if(parameters != null) {
		for( var i in parameters) {
			if(i != 0)
				methodParameters += ',';

			var o = parameters[i];
			methodParameters += typeof o === 'string' && o.indexOf('*ref') != -1 ? 'Greencode.cache.getById(' + o.split('*ref')[0] + ', mainElement)' : variableName + '[' + i + ']';
		}
	}

	return methodParameters;
};

Bootstrap.getObjectByEvent = function(o, name) {
	if(o.events == null)
		o.events = {};

	if(o.events[name] == null) {
		o.events[name] = {
			processing : false,
			methods : new Array()
		};
	}

	return o.events[name];
};

Bootstrap.callRequestMethod = function(mainElement, target, event, p, __arguments) {
	var objectEvent = Bootstrap.getObjectByEvent(target, event.type);

	if(objectEvent.processing === true) {
		objectEvent.methods.push(function() {
			Bootstrap.callRequestMethod(mainElement, target, event, p, __arguments);
		});
		return false;
	}

	if(p.url[0] === '#') {
		var methodParameters = '';
		if(p.parametersRequest != null)
			methodParameters = Bootstrap.analizeParameters(p.methodParameters, "p.methodParameters");

		if(__arguments != null && __arguments.length > 0) {
			if(methodParameters !== '') {
				methodParameters += ',';
				for( var i in __arguments) {
					if(i != 0)
						methodParameters += ',';
					methodParameters += '__arguments[' + i + ']';
				}
			}
		}

		if(__isFirefox)
			new Function('return ' + p.url.substring(1) + '(' + methodParameters + ')')();
		else
			eval(p.url.substring(1) + '(' + methodParameters + ')');
	} else {
		objectEvent.processing = true;

		var param = {}, form = null, formName = null;
		
		if(p.args != null) {
			param._args = [];
			
			for( var i in p.args) {
				var o = p.args[i];
				if(o.className == Greencode.className.greenContext) {
					param._args.push(JSON.stringify(o));
				}else if(o.className == Greencode.className.containerElement || o.className == Greencode.className.containerEventObject) {
					var uid;
					if(target.tagName === 'CONTAINER') {
						for(var i2 in __arguments) {
							if((uid = __arguments[i2].__containerUID) != null)
								break;
						}
					} else {
						var c = Greencode.customMethod.getParentByTagName.call(target, "container");
						uid = c ? c.getAttribute('uid') : null;
					}
					
					param._args.push(JSON.stringify({
						className : o.className,
						uid : uid
					}));
				}else if(o.className == Greencode.className.element) {
					param._args.push(JSON.stringify({
						className : o.className,
						castTo : o.castTo,
						uid : Greencode.cache.register(target)+''
					}));
				} else {
					var _arg = {
						className : o.className,
						fields : {}
					};
					
					var arg = __arguments[i-param._args.length];
					Greencode.jQuery.each(o.fields, function() {
						var value = arg[this];
						if(Greencode.util.isElement(value))
							_arg.fields[this] = {
								create : false
							};
						else if(value != null && !Greencode.jQuery.isFunction(value) && !Greencode.util.isElement(value))
							_arg.fields[this] = value;
					});

					_arg.fields = JSON.stringify(_arg.fields);
					param._args.push(JSON.stringify(_arg));
				}		
			}
		}
		
		if(p.formName != null) {
			form = Greencode.crossbrowser.querySelector.call(mainElement, 'form[name="' + p.formName + '"]');
			if(form == null && typeof console != 'undefined')
				console.warn("Could not find the form with name " + p.formName + ".");
			else
				formName = p.formName;
		} else if((form = target.form) != null || (form = Greencode.customMethod.getParentByTagName.call(target, "form")) != null) {
			formName = form.getAttribute("name");
		}
		
		if(form != null) {
			var buildParam = function(param, list) {
				for(var i in list) {
					var res = list[i], value = null, name = null;
					
					if(res instanceof Array) {
						var eFirst = res[0];
						
						if(eFirst instanceof Node) {
							name = eFirst.name;
							var isCheckBox = eFirst.type === "checkbox", values = null;
							if(isCheckBox)
								values = new Array();

							for(var i2 = -1; ++i2 < res.length;) {
								var e = res[i2];
								if(e.checked) {
									if(isCheckBox)
										values.push(e.value);
									else {
										values = e.value;
										break;
									}									
								}
							}

							value = values != null && values.length > 0 ? values : null;
						} else {
							name = i;
							value = param[name];
							
							var first = false;
							if(!value) {
								value = new Array();
								first = true;
							}
							
							for(var i2 = -1; ++i2 < res.length;) {
								var e = res[i2];								
								var o = buildParam({__uid: e.__container.getAttribute('uid')}, e);
								value.push(o);
							}							
							
							if(first)
								param[name] = value;
							
							continue;
						}
					} else {
						var eFirst = res;
						name = eFirst.name;
						
						if(eFirst.tagName === "SELECT") {
							if(eFirst.multiple) {
								var values = new Array();
								for(var i2 = -1; ++i2 < eFirst.options.length;) {
									var option = eFirst.options[i2];
									if(option.selected)
										values.push(option.value);
								}
								value = values.length > 0 ? values : null;
							} else
								value = eFirst.selectedIndex === -1 ? "" : eFirst.options[eFirst.selectedIndex].value;
						} else
							value = eFirst.tagName === "INPUT" && eFirst.type === "file" ? eFirst : eFirst.value;
					}

					if(value)
						param[name] = value;
				}
				
				return param;
			}
			
			var list = Greencode.customMethod.getAllDataElements.call(form);
			buildParam(param, list);
			
			for(var i in param) {	
				if(i === '_args')
					continue;
				
				var v = param[i];
				if(v instanceof Object)
					param[i] = JSON.stringify(v);
			}
			
			param.__requestedForm = formName;
		}

		param.cid = p.cid;
		param.viewId = p.viewId;

		param._buttonId = target.window == null ? target.id == null ? target.name : target.id : 'Window';

		var _data = {
			mainElement: mainElement,
			args: __arguments,
			paramenters: param
		};
				
		if(Greencode.executeEvent('beforeEvent', _data)) {
			var cometReceber = new Comet(p.url);
			cometReceber.setMethodRequest(p.requestMethod);
			cometReceber.setCometType(Comet().STREAMING);
			cometReceber.reconnect(false);
			cometReceber.setAsync(p.async);
			cometReceber.forceConnectType(Comet().IframeHttpRequest);
	
			cometReceber.send(param, function(data) {
				Bootstrap.init(mainElement, data, __arguments);
			}, function(data) {
				Bootstrap.init(mainElement, data, __arguments);
	
				objectEvent.processing = false;
	
				if(event.onComplete != null) {
					event.onComplete();
				}
	
				if(objectEvent.methods.length > 0) {
					objectEvent.methods[0]();
					objectEvent.methods.splice(0, 1);
				}
				
				_data.serverCallback = data;
				Greencode.executeEvent('afterEvent', _data);
			});
	
			delete cometReceber;
			cometReceber = null;
		}

		if(!Greencode.DEBUG_MODE) {
			delete param;
			delete tagEventObject;
		}
	}
};

Bootstrap.isGreencodeCommand = function(commandName) {
	return (commandName.indexOf('@crossbrowser') != -1 || commandName.indexOf('@customMethod') != -1);
}

Bootstrap.toGreencodeCommand = function(commandName) {
	var s;
	if(commandName.indexOf(s = '@crossbrowser') != -1 || commandName.indexOf(s = '@customMethod') != -1)
		commandName = commandName.replace(s, 'Greencode.'+s.substring(1));
	return commandName;
}

Bootstrap.adaptiveCommand = function(commandName, parameters) {
	return Bootstrap.isGreencodeCommand(commandName) ?
			'Greencode.' + commandName.substring(1) + '.call(e'+(parameters ? ','+parameters : '')+')'
		  : 'e.' + commandName + '('	+ parameters + ')';
};

Bootstrap.readCommand = function(mainElement) {
	if(this == null)
		return;

	var e = Greencode.cache.getById(this.uid, mainElement);

	if(e == null)
		return;

	var parameters = '';

	if(this.parameters != null) {
		Bootstrap.analizeJSON(mainElement, this.parameters, e);
		parameters = Bootstrap.analizeParameters(this.parameters, "this.parameters", mainElement);
	}

	var strEval = null;
	if(this.name.indexOf('*ref.') != -1) {
		var split = this.name.split('*ref.');
		if(split[0].indexOf('[') === 0) {
			var uids = JSON.parse(split[0]), res = __isFirefox ? new Function('var e = arguments[0]; var mainElement = arguments[1]; return ' + Bootstrap.adaptiveCommand(split[1], parameters)).call(this, e, mainElement) : eval(Bootstrap.adaptiveCommand(split[1], parameters));

			for(var i = -1; ++i < res.length;)
				Greencode.cache.register(uids[i], res[i]);
		} else
			strEval = 'Greencode.cache.register(' + split[0] + ', ' + Bootstrap.adaptiveCommand(split[1], parameters)+');';
	} else if(this.name.indexOf('*prop.') != -1) {
		var split = this.name.split('*prop.');
		strEval = 'Greencode.cache.register(' + split[0] + ', e.' + split[1]+');';
	} else if(this.name.indexOf('*vector.') != -1) {
		var split = this.name.split('*vector.');
		strEval = 'Greencode.cache.register(' + split[0] + ', e[' + split[1] + ']'+');';
	} else if(this.name.indexOf('#') === 0)
		strEval = 'e.' + this.name.substring(1) + '=' + parameters + ';';
	else
		strEval = Bootstrap.adaptiveCommand(this.name, parameters);

	if(strEval != null) {
		try {
			var res = __isFirefox ? new Function('var e = arguments[0]; var mainElement = arguments[1]; return ' + strEval).call(this, e, mainElement) : eval(strEval);

			if(Greencode.DEBUG_MODE) {
				console.warn(e);
				console.warn("Code: " + strEval + "\n[Reference]\n", e, this.parameters);
				console.warn("Result: ", res);
			}
		} catch(ex) {
			if(typeof console != 'undefined') {
				console.warn(ex);
				console.warn("Code: " + strEval + "\n[Reference]\n", e, this.parameters);
			}
		}
	}
};

Bootstrap.buttons = function(mainElement) {
	var elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'input[type="redirect"]:not([swept]), button[type="redirect"]:not([swept])');

	for(var i = -1; ++i < elements.length;) {
		var element = elements[i];
		element.setAttribute('swept', null);

		Greencode.crossbrowser.registerEvent.call(element, 'click', function() {
			this.type = "button";

			var action = this.getAttribute('action');

			if(action != null)
				window.location.href = action;
		});
	}

	elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, '[appendTo]:not([swept])');

	for(var i = -1; ++i < elements.length;) {
		var element = elements[i], appendTo = element.getAttribute('appendTo').toLowerCase(), o = appendTo == "body" ? document.body : Greencode.crossbrowser.querySelector.call(mainElement, appendTo);

		element.setAttribute('swept', 'swept');

		if(o != null) {
			Greencode.crossbrowser.registerEvent.call(element, 'click', function(e) {
				e.preventDefault();

				var appendTo = this.getAttribute('appendTo'), empty = Greencode.crossbrowser.hasAttribute.call(this, 'empty'), changeURL = Greencode.crossbrowser.hasAttribute.call(this, 'changeURL'), keepViewId = Greencode.crossbrowser.hasAttribute.call(this, 'keepViewId'), href = this
						.getAttribute('href'), data = {
					__contentIsHtml : true
				}, first = false, cometReceber = new Comet(this.getAttribute('href'));

				if(keepViewId)
					data.viewId = viewId;

				cometReceber.setMethodRequest('GET');
				cometReceber.setCometType(Comet().STREAMING);
				cometReceber.reconnect(false);
				cometReceber.setAsync(true);
				cometReceber.jsonContentType(false);

				var _data = {
					mainElement: o,
					target: this,
					appendToSelector: appendTo,
					appendToElement: o,
					empty: empty,
					changeURL: changeURL,
					keepViewId: keepViewId,
					href: href
				};
				
				var f = function(data) {
					if(!first) {
						first = true;
						var _href = window.location.href, tags = new Array();

						delete listTags[_href];
						listTags[_href] = tags;

						for(var ii = -1; ++ii < o.childNodes.length;)
							tags.push(o.childNodes[ii]);

						if(empty)
							Greencode.customMethod.empty.call(o);

						o.insertAdjacentHTML('beforeEnd', data);
						Bootstrap.init(o);

						if(changeURL) {
							if(history.pushState == null)
								window.location.hash = "#!" + href;
							else {
								history.replaceState({
									selector : appendTo
								}, null, location.href);
								history.pushState({
									selector : appendTo
								}, null, Greencode.CONTEXT_PATH + '/' + href);

								_href = window.location.href;

								tags = new Array();
								delete listTags[_href];
								listTags[_href] = tags;

								for(var ii = -1; ++ii < o.childNodes.length;)
									tags.push(o.childNodes[ii]);
							}
						}

						var scripts = Greencode.crossbrowser.querySelectorAll.call(o, 'script');
						for(var s = -1; ++s < scripts.length;)
							eval(Greencode.crossbrowser.text.call(scripts[s]));
					} else {
						o.insertAdjacentHTML('beforeEnd', data);
						Bootstrap.init(o);
					}
				};

				if(Greencode.executeEvent('beforePageRequest', _data))
					cometReceber.send(data, f, function(data) {
						f(data);
						Greencode.executeEvent('afterPageRequest', _data);
						Greencode.executeEvent('pageLoad', _data);
					});

				delete cometReceber;
				cometReceber = null;

				return false;
			});
		}
	}

	elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'input[type="ajax"]:not([swept]), button[type="ajax"]:not([swept])');

	for(var i = -1; ++i < elements.length;) {
		var element = elements[i];
		element.setAttribute('swept', null);

		this.type = "button";

		Greencode.crossbrowser.registerEvent.call(element, 'click', function() {
			var data = {}, form = this.form, _es = Greencode.crossbrowser.querySelectorAll.call(form, 'input, textarea, select'), cometReceber = new Comet(this.getAttribute('action'));

			if(_es != null) {
				for( var e in _es)
					data[this.id || this.name] = this.value;
			}

			cometReceber.setMethodRequest(this.getAttribute('method') != null && this.getAttribute('method').toUpperCase() === 'POST' ? 'POST' : 'GET');
			cometReceber.setCometType(Comet().LONG_POLLING);
			cometReceber.reconnect(false);
			cometReceber.setAsync(true);
			cometReceber.forceConnectType(Comet().IframeHttpRequest);

			cometReceber.send(data, function(data) {
			}, function(data) {
				if(element.getAttribute('appendTo') != null) {
					var o = Greencode.crossbrowser.querySelector.call(mainElement, element.getAttribute('appendTo'));

					if(element.getAttribute('empty') != null && element.getAttribute('empty').toLowerCase() === 'true') {
						for(var ii = -1; ++ii < element.children.length;) {
							var c = element.children[ii];
							c.parentNode.removeChild(c);
						}
					}

					element.insertAdjacentHTML('beforeEnd', data);
					Bootstrap.init(element);
				} else if(data != null && data != "")
					Bootstrap.init(form, JSON.parse(data));
			});

			delete cometReceber;
			cometReceber = null;
		});
	}

	elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'input[type="submit"]:not([swept]), button[type="submit"]:not([swept])');

	for(var i = -1; ++i < elements.length;) {
		var element = elements[i];
		element.setAttribute('swept', null);

		if(element.getAttribute('action') != null)
			element.form.setAttribute('action', element.getAttribute('action'));
	}
};

Bootstrap.init = function(mainElement, __jsonObject, argsEvent) {
	if(mainElement == null)
		mainElement = document.body;

	Greencode.tags.process();
	Bootstrap.buttons(mainElement);

	if(jsonObject == null) {
		var jsons = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'div.JSON_CONTENT');
		if(jsons != null && jsons.length > 0) {
			for(var i = -1; ++i < jsons.length;) {
				var jsonDiv = jsons[i];

				if(jsonDiv.parentNode)
					jsonDiv.parentNode.removeChild(jsonDiv);				
				
				try {
					Bootstrap.init(mainElement, JSON.parse(Greencode.crossbrowser.text.call(jsonDiv)));
					Greencode.executeEvent('init');
				} catch (e) {
				}
			}

			return;
		}
	}

	if(__jsonObject != null) {
		if(!Greencode.jQuery.isArray(__jsonObject))
			__jsonObject = [ __jsonObject ];

		for(var i in __jsonObject) {
			var jsonObject = __jsonObject[i];

			if(Greencode.DEBUG_MODE) {
				console.warn('-----------------------');
				console.warn('MainElement: ', mainElement);
				console.warn('JSON Object: ', jsonObject);
			}

			var hasArgsEvent = argsEvent != null && argsEvent.length > 0 && jsonObject.args != null && jsonObject.args.length > 0;
			if(hasArgsEvent) {
				for(var i = -1; ++i < jsonObject.args.length;)
					Greencode.cache.register(jsonObject.args[i], argsEvent[i]);
			}

			if(jsonObject.comm != null && jsonObject.comm.length > 0) {
				for( var i in jsonObject.comm) {
					var _this = jsonObject.comm[i];
					Bootstrap.readCommand.call(_this, mainElement);
				}

				if(!Greencode.DEBUG_MODE)
					delete jsonObject.comm;
			}
			
			if(hasArgsEvent) {
				for(var i = -1; ++i < jsonObject.args.length;)
					Greencode.cache.remove(jsonObject.args[i]);
			}

			if(jsonObject.sync != null) {
				var sync = jsonObject.sync, e = Greencode.cache.getById(sync.uid, mainElement), cometReceber = new Comet(Greencode.CONTEXT_PATH + '/$synchronize');

				cometReceber.setMethodRequest("post");
				cometReceber.setCometType(Comet().LONG_POLLING);
				cometReceber.reconnect(false);

				if(e != null) {
					Bootstrap.analizeJSON(mainElement, sync.command.parameters, e);

					var p = sync.command.parameters,
					parameters = Bootstrap.analizeParameters(p, "p", mainElement),
					value, strEval = '', filter, forceArray = false;

					if(sync.command.name.indexOf('__partFile') > -1) {
						value = e;
						cometReceber.forceConnectType(Comet().IframeHttpRequest);
					} else {
						if(sync.command.name === "#") {
							value = {};
							for( var i in p)
								value[i] = e[p[i]];
						} else {
							if(sync.command.name.indexOf('#') === 0) {
								var isGreencodeCommand = Bootstrap.isGreencodeCommand(sync.command.name);
								if(!isGreencodeCommand)
									strEval = "e.";
								else {
									sync.command.name = Bootstrap.toGreencodeCommand(sync.command.name);
									var l = sync.command.name.indexOf('(');
									sync.command.name = (sync.command.name.substring(0, l)+'.call(e,'+sync.command.name.substring(l+1));
								}
								
								if(sync.command.name.indexOf('##[]') === 0) {
									filter = p;
									strEval += sync.command.name.substring(4);
									forceArray = true;
								}else if(sync.command.name.indexOf('##') === 0) {
									filter = p;
									strEval += sync.command.name.substring(2);
								}else
									strEval += sync.command.name.substring(1);
							} else
								strEval = Bootstrap.adaptiveCommand(sync.command.name, parameters);
										
							value = __isFirefox ? new Function( 'var e = arguments[0]; var p = arguments[1]; return '+strEval)(e, p) : eval(strEval);
						}

						if(forceArray || Greencode.jQuery.isArray(value))
							value = Greencode.util.arrayToString(value, filter);
						else if(typeof value === "object")
							value = Greencode.util.objectToString(value, filter);
					}
					cometReceber.send({
						viewId : viewId,
						uid : sync.uid,
						varName : sync.varName,
						'var' : value
					});
				} else
					cometReceber.send({
						viewId : viewId,
						uid : sync.uid
					});

				delete cometReceber;
				cometReceber = null;

				if(!Greencode.DEBUG_MODE)
					jsonObject.sync
			}

			if(jsonObject.errors != null) {
				var divGreenCodeModalErro = document.createElement("div"), spanTitulo = document.createElement("span"), spanBotaoFechar = document.createElement("span"), topBar = document.createElement("div"), contentModalError = document.createElement("div");

				divGreenCodeModalErro.setAttribute('id', 'GreenCodemodalErro');
				for(var i in Greencode.modalErro.style)
					divGreenCodeModalErro.style[i] = Greencode.modalErro.style[i];

				spanTitulo.appendChild(document.createTextNode('Exception:'));
				for(var i in Greencode.modalErro.topBar.title.style)
					spanTitulo.style[i] = Greencode.modalErro.topBar.title.style[i];

				spanBotaoFechar.appendChild(document.createTextNode('X'));
				for(var i in Greencode.modalErro.topBar.closeButton.style)
					spanBotaoFechar.style[i] = Greencode.modalErro.topBar.closeButton.style[i];

				Greencode.crossbrowser.registerEvent.call(spanBotaoFechar, 'click', function() {
					divGreenCodeModalErro.parentNode.removeChild(divGreenCodeModalErro);
				});

				for(var i in Greencode.modalErro.topBar.style)
					topBar.style[i] = Greencode.modalErro.topBar.style[i];

				topBar.appendChild(spanTitulo);
				topBar.appendChild(spanBotaoFechar);

				divGreenCodeModalErro.appendChild(topBar);

				contentModalError.setAttribute('class', 'content');
				for(var i in Greencode.modalErro.content.style)
					contentModalError.style[i] = Greencode.modalErro.content.style[i];

				divGreenCodeModalErro.appendChild(contentModalError);

				document.body.appendChild(divGreenCodeModalErro);

				for( var i in jsonObject.errors) {
					var error = jsonObject.errors[i], title = error.className + ": " + error.message, divTitle = document.createElement("div");

					for(i in Greencode.modalErro.content.title.style)
						divTitle.style[i] = Greencode.modalErro.content.title.style[i];

					divTitle.appendChild(document.createTextNode(title));

					contentModalError.appendChild(divTitle);

					if(error.stackTrace != null) {
						for( var i2 in error.stackTrace) {
							var st = error.stackTrace[i2], msg = st.className + '.' + st.methodName + '(', lineDiv = document.createElement("div");

							msg += (st.lineNumber < 0 ? 'Unknown Source' : st.fileName + ':' + st.lineNumber) + ')';

							lineDiv.appendChild(document.createTextNode(msg));

							if(st.possibleError) {
								for( var i3 in Greencode.modalErro.content.possibleErro.style)
									lineDiv.style[i3] = Greencode.modalErro.content.possibleErro.style[i3];
							} else {
								for( var i3 in Greencode.modalErro.content.lineClass.style)
									lineDiv.style[i3] = Greencode.modalErro.content.lineClass.style[i3];
							}

							contentModalError.appendChild(lineDiv);
						}
					}
				}
				;

				if(!Greencode.DEBUG_MODE)
					delete jsonObject.errors;
			}

			if(!Greencode.DEBUG_MODE)
				delete jsonObject;

			jsonObject = null;
		}

		__jsonObject = null;
	}

	var res = Greencode.crossbrowser.querySelectorAll.call(mainElement, '[msg\\:key]');
	for(var i = -1; ++i < res.length;) {
		var e = res[i], msg = internationalization_msg[e.getAttribute('msg:key')];

		if(Greencode.crossbrowser.hasAttribute.call(e, 'msg:appendText'))
			msg += e.getAttribute('msg:appendText');

		if(e.tagName === 'INPUT')
			e.value = msg;
		else
			e.innerHTML = msg;
	}
};