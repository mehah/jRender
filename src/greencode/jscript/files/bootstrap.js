
var internationalization_msg = {},
	Bootstrap = {},
	__isFirefox = window.mozInnerScreenX != null,
	__isIE8orLess = document.addEventListener == null;

Bootstrap.analizeJSON = function(mainElement, j, target) {
	if(j == null)
		return;
	
	if(target == null)
		target = {};
	
	Greencode.jQuery.each(j, function(i) {
		var p = this;
		if (p.isFunction === true) {
			j[i] = function(event) {
				if (event == null)
					event = {type: p.url || 'undefined'};
				
				Bootstrap.callRequestMethod(mainElement, target, event, p, arguments);
			};
		}else if(Greencode.jQuery.isPlainObject(p))
			Bootstrap.analizeJSON(mainElement, p, target);
		else if(Greencode.jQuery.isArray(p)) {
			for (var i = -1; ++i < p.length;)
				Bootstrap.analizeJSON(mainElement, p[i], target);
		}
	});
};

Bootstrap.analizeParameters = function(parameters, variableName, mainElement) {
	var methodParameters = "";
	if(parameters != null) {
		for(var i in parameters) {		
			if(i != 0) methodParameters += ',';
		
			var o = parameters[i];			
			methodParameters += typeof o === 'string' && o.indexOf('*ref') != -1 ? 'Greencode.tag.getById('+o.split('*ref')[0]+', mainElement)' : variableName+'['+i+']';
		}
	}
	
	return methodParameters;
};

Bootstrap.getObjectByEvent = function(o, name) {
	if (o.events == null) 
		o.events = {};

	if(o.events[name] == null) {
		o.events[name] = {
			processing: false,
			methods: new Array()
		};
	}
	
	return o.events[name];
};

Bootstrap.callRequestMethod = function(mainElement, target, event, p, __arguments) {
	var objectEvent = Bootstrap.getObjectByEvent(target, event.type);

	if (objectEvent.processing === true) {
		objectEvent.methods.push(function() {
			Bootstrap.callRequestMethod(mainElement, target, event, p, __arguments);
		});
		return false;
	}
	
	if(p.url[0] === '#')
	{
		var methodParameters = '';
		if(p.parametersRequest != null)
			methodParameters = Bootstrap.analizeParameters(p.methodParameters, "p.methodParameters");
		
		if(__arguments != null && __arguments.length > 0) {
			if(methodParameters !== '') {
				methodParameters += ',';
				for(var i in __arguments) {
					if(i != 0) methodParameters += ',';
					methodParameters += '__arguments['+i+']';
				}
			}				
		}
		
		if(__isFirefox)
			new Function( 'return '+ p.url.substring(1)+'('+methodParameters+')')();
		else
			eval(p.url.substring(1)+'('+methodParameters+')');
	}else
	{
		objectEvent.processing = true;		

		var _args = null;
		if(p.args != null) {
			_args = [];
			for (var i in p.args) {
				var o = p.args[i],
					arg = __arguments[i];
				if(arg != null) {
					var _arg = {className: o.className, fields: {}};
					Greencode.jQuery.each(o.fields, function() {
						var value = arg[this];
						
						if(Greencode.util.isElement(value))
							_arg.fields[this] = {create:false};
						else if(value != null && !Greencode.jQuery.isFunction(value) && !Greencode.util.isElement(value))
							_arg.fields[this] = value;
					});
					
					_arg.fields = JSON.stringify(_arg.fields);					
					_args.push(JSON.stringify(_arg));
				}
			}
		}
		
		var param = {},
			form = null;
		
		if(p.formName != null) {
			form = Greencode.crossbrowser.querySelector.call(mainElement, 'form[name="'+p.formName+'"]');
			if(form == null && typeof console != 'undefined')
				console.warn("Could not find the form with name "+p.formName+".");
		}else if(target.form != null)
			form = target.form;
		
		if(form != null) {
			var names = p.formNameFields;
			if(names == null)
			{
				var elements = Greencode.crossbrowser.querySelectorAll.call(form, 'input, select, textarea'),
					names = new Array();
				for (var i = -1; ++i < elements.length;) {
					var name = elements[i].name;
					if(name)
						names.push(name);
				}
				
				p.formName = target.form.getAttribute('name');
				p.formNameFields = names;
			}
			
			if(p.formNameFields != null && p.formNameFields.length > 0)
			{
				for(var i in p.formNameFields) {
					var name = p.formNameFields[i],
						res = Greencode.crossbrowser.querySelectorAll.call(form, 'input[name="'+name+'"], select[name="'+name+'"], textarea[name="'+name+'"]');
					
					if(res != null && res.length > 0) {
						var value = null;
						if(res.length === 1) {
							var e = res[0],
								isInput = e.tagName === "INPUT";
							if(isInput || e.tagName === "TEXTAREA")
								value = isInput && (e.type === "checkbox" || e.type === "radio") ? e.checked ? e.value : null : e.type === "file" ? e : e.value;
							else if(e.tagName === "SELECT") 	{
								if(e.multiple) {
									var values = new Array();
									for (var i2 = -1; ++i2 < e.options.length;) {
										var option = e.options[i2];
										if(option.selected)
											values.push(option.value);
									}
									value = values.length > 0 ? values : null;
								}else
									value = e.selectedIndex === -1 ? "" : e.options[e.selectedIndex].value;
							}
						}else {
							var eFirst = res[0],
								isInput = eFirst.tagName === "INPUT";
							
							if(isInput) {
								var isCheckBox = eFirst.type === "checkbox",
									values = null;
								if(isCheckBox)
									values = new Array();
								else if(eFirst.type !== "radio")
									continue;
								
								for (var i2 = -1; ++i2 < res.length;) {
									var e = res[i2];
									
									if(isCheckBox && e.checked)
										values.push(e.value);
									else if(e.checked) {
										values = e.value;
										break;
									}
								}
								
								value = values !=null && values.length > 0 ? values : null;
							}
						}						
						
						param[name] = value;
					}
				}
			}
		}
		
		param.cid = p.cid;
		param.viewId = p.viewId;
		if(p.formName)
			param.__requestedForm = p.formName;
				
		if(_args != null)
			param._args = _args;
		
		param._buttonId = target.window == null ? target.id == null ? target.name : target.id : 'Window';
		
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
			
			if(objectEvent.methods.length > 0) {
				objectEvent.methods[0]();
				objectEvent.methods.splice(0, 1);
			}
		});
		
		delete cometReceber;
		cometReceber = null;
		
		if(!DEBUG_MODE) {
			delete param;
			delete tagEventObject;
		}
	}
};

Bootstrap.adaptiveCommand = function(commandName, parameters)
{
	return (commandName.indexOf('@crossbrowser') != -1 || commandName.indexOf('@customMethod') != -1) ?
			'Greencode.' + commandName.substring(1) + '.call(e'+(parameters ? ','+parameters : '')+');'
		  : 'e.' + commandName + '('	+ parameters + ');';
};

Bootstrap.readCommand = function(mainElement)
{
	if(this == null)
		return;
	
	var e = Greencode.tag.getById(this.uid, mainElement);
	
	if(e == null)
		return;
		
	var parameters = '';

	if (this.parameters != null) {
		Bootstrap.analizeJSON(mainElement, this.parameters, e);				
		parameters = Bootstrap.analizeParameters(this.parameters, "this.parameters", mainElement);
	}
	
	var strEval = null;
	if(this.name.indexOf('*ref.') != -1) {
		var split = this.name.split('*ref.');
		if(split[0].indexOf('[') === 0) {
			var uids = JSON.parse(split[0]),
				res = __isFirefox ? new Function('var e = arguments[0]; var mainElement = arguments[1]; return '+ Bootstrap.adaptiveCommand(split[1], parameters)).call(this, e, mainElement)
								  : eval(Bootstrap.adaptiveCommand(split[1], parameters));
			
			for (var i = -1; ++i < res.length;)
				Greencode.tag.references[uids[i]+""] = res[i];
		}else
			strEval = 'Greencode.tag.references['+split[0]+'+""] = '+Bootstrap.adaptiveCommand(split[1], parameters);
	}else if(this.name.indexOf('*prop.') != -1) {
		var split = this.name.split('*prop.');
		strEval = 'Greencode.tag.references['+split[0]+'+""] = e.'+split[1];
	}else if(this.name.indexOf('*vector.') != -1) {
		var split = this.name.split('*vector.');
		strEval = 'Greencode.tag.references['+split[0]+'+""] = e['+split[1]+']';
	}else if(this.name.indexOf('#') === 0)
		strEval = 'e.' + this.name.substring(1) + '='+parameters+';';
	else
		strEval = Bootstrap.adaptiveCommand(this.name, parameters);
	
	if(strEval != null) {
		try {
			var res = __isFirefox ? new Function('var e = arguments[0]; var mainElement = arguments[1]; return '+strEval).call(this, e, mainElement)
								  : eval(strEval);
			
			if(DEBUG_MODE) {
				console.warn(e);
				console.warn("Code: "+strEval+"\n[Reference]\n", e, this.parameters);
				console.warn("Result: ", res);
			}
		} catch (ex) {
			if(typeof console != 'undefined') {
				console.warn(ex);
				console.warn("Code: "+strEval+"\n[Reference]\n", e, this.parameters);
			}
		}

	}
};

Bootstrap.buttons = function(mainElement) {
	var elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'input[type="redirect"]:not([swept]), button[type="redirect"]:not([swept])');
	
	for (var i = -1; ++i < elements.length;) {
		var element = elements[i];
		element.setAttribute('swept', null);
		
		Greencode.crossbrowser.registerEvent.call(element, 'click', function() {
			this.type = "button";
			
			var action = this.getAttribute('action');

			if (action != null)
				window.location.href = action;
		});
	}
	
	elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, '[appendTo]:not([swept])');
	
	for (var i = -1; ++i < elements.length;) {
		var element = elements[i],
			appendTo = element.getAttribute('appendTo').toLowerCase(),
			o = appendTo == "body" ? document.body : Greencode.crossbrowser.querySelector.call(mainElement, appendTo);
		
		element.setAttribute('swept', 'swept');
		
		if(o != null) {			
			Greencode.crossbrowser.registerEvent.call(element, 'click', function(e) {
				e.preventDefault();
				
				var appendTo = this.getAttribute('appendTo'),
					empty = Greencode.crossbrowser.hasAttribute.call(this, 'empty'),
					changeURL = Greencode.crossbrowser.hasAttribute.call(this, 'changeURL'),
					keepViewId = Greencode.crossbrowser.hasAttribute.call(this, 'keepViewId'),
					href = this.getAttribute('href'),
					data = {__contentIsHtml:true},
					first = false,
					cometReceber = new Comet(this.getAttribute('href'));
				
				if(keepViewId)
					data.viewId = viewId;
				
				cometReceber.setMethodRequest('GET');
				cometReceber.setCometType(Comet().STREAMING);
				cometReceber.reconnect(false);
				cometReceber.setAsync(true);
				cometReceber.forceConnectType(Comet().IframeHttpRequest);
				cometReceber.jsonContentType(false);
								
				var f = function(data) {
					if(!first) {
						first = true;
						var _href = window.location.href,
							tags = {};
						
						delete listTags[_href];
						listTags[_href] = tags;
						
						for (var ii = -1; ++ii < o.children.length;) {
							var c = o.children[ii];
							c.setAttribute('_uid', ++lastUniqueId);
							tags[lastUniqueId] = c;
						}
						
						var oldHtml = o.innerHTML;
						
						if(empty)
							Greencode.customMethod.empty.call(o);
						
						o.insertAdjacentHTML('beforeEnd', data);
						Bootstrap.init(o);
						
						if(changeURL)
						{
							if(history.pushState == null)
								window.location.hash="#!"+href;
							else {
								history.replaceState({content: oldHtml, selector: appendTo}, null, location.href);
								history.pushState({content: o.innerHTML, selector: appendTo}, null, CONTEXT_PATH+'/'+href);
								
								_href = window.location.href;
								
								tags = {};
								delete listTags[_href];
								listTags[_href] = tags;
								
								for (var ii = -1; ++ii < o.children.length;) {
									var c = o.children[ii];
									c.setAttribute('_uid', ++lastUniqueId);
									tags[lastUniqueId] = c;
								}
							}
						}
						
						var scripts = Greencode.crossbrowser.querySelectorAll.call(o, 'script');						
						for (var s = -1; ++s < scripts.length;)
							eval(Greencode.crossbrowser.text.call(scripts[s]));
					}else {
						o.insertAdjacentHTML('beforeEnd', data);
						Bootstrap.init(o);
					}
				};
				
				cometReceber.send(data, f, f);
				
				delete cometReceber;
				cometReceber = null;
				
				return false;
			});
		}
	}
		
	elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'input[type="ajax"]:not([swept]), button[type="ajax"]:not([swept])');
	
	for (var i = -1; ++i < elements.length;) {
		var element = elements[i];
		element.setAttribute('swept', null);
		
		this.type = "button";

		Greencode.crossbrowser.registerEvent.call(element, 'click', function() {
			var data = {},
				form = this.form,
				_es = Greencode.crossbrowser.querySelectorAll.call(form, 'input, textarea, select'),
				cometReceber = new Comet(this.getAttribute('action'));
			
			if(_es != null) {
				for ( var e in _es)
					data[this.id || this.name] = this.value;
			}
			
			cometReceber.setMethodRequest(this.getAttribute('method') != null && this.getAttribute('method').toUpperCase() === 'POST' ? 'POST' : 'GET');
			cometReceber.setCometType(Comet().LONG_POLLING);
			cometReceber.reconnect(false);
			cometReceber.setAsync(true);
			cometReceber.forceConnectType(Comet().IframeHttpRequest);
			
			cometReceber.send(data, function(data) {}, function(data) {
				if (element.getAttribute('appendTo') != null) {
					var o = Greencode.crossbrowser.querySelector.call(mainElement, element.getAttribute('appendTo'));

					if (element.getAttribute('empty') != null && element.getAttribute('empty').toLowerCase() === 'true') {
						for (var ii = -1; ++ii < element.children.length;) {
							var c = element.children[ii];
							c.parentNode.removeChild(c);
						}
					}

					element.insertAdjacentHTML('beforeEnd', data);
					Bootstrap.init(element);
				} else if (data != null && data != "")
					Bootstrap.init(form, JSON.parse(data));
			});
			
			delete cometReceber;
			cometReceber = null;
		});
	}

	elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'input[type="submit"]:not([swept]), button[type="submit"]:not([swept])');
	
	for (var i = -1; ++i < elements.length;) {
		var element = elements[i];
		element.setAttribute('swept', null);
		
		if (element.getAttribute('action') != null)
			element.form.setAttribute('action', element.getAttribute('action'));
	}
};

Bootstrap.init = function(mainElement, __jsonObject, argsEvent) {
	if (mainElement == null)
		mainElement = document.body;
		
	if(Greencode.ready != null)
		Greencode.ready.call(mainElement);
	
	Bootstrap.buttons(mainElement);
	
	if(jsonObject == null) {
		var jsons = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'div.JSON_CONTENT');
		if(jsons != null && jsons.length > 0) {
			for (var i = -1; ++i < jsons.length;) {
				var jsonDiv = jsons[i];
				
				if(jsonDiv.parentNode)
					jsonDiv.parentNode.removeChild(jsonDiv);
				
				Bootstrap.init(mainElement, JSON.parse(Greencode.crossbrowser.text.call(jsonDiv)));
			}
			
			return;
		}
	}
	
	if(__jsonObject != null) {
		if(!Greencode.jQuery.isArray(__jsonObject))
			__jsonObject = [__jsonObject];
		
		for(i in __jsonObject) {
			var jsonObject = __jsonObject[i];
			
			if(DEBUG_MODE) {
				console.warn('-----------------------');
				console.warn('MainElement: ', mainElement);
				console.warn('JSON Object: ', jsonObject);
			}
			
			if(argsEvent != null && argsEvent.length > 0 && jsonObject.args != null && jsonObject.args.length > 0) {
				for (var i = -1; ++i < jsonObject.args.length;)
					Greencode.tag.references[jsonObject.args[i]] = argsEvent[i];
			}
			
			if(jsonObject.comm != null && jsonObject.comm.length > 0) {
				for(var i in jsonObject.comm) {
					var _this = jsonObject.comm[i];
					Bootstrap.readCommand.call(_this, mainElement);
				}
				
				if(!DEBUG_MODE)
					delete jsonObject.comm;
			}
			
			if(jsonObject.sync != null) {
				var sync = jsonObject.sync,
					e = Greencode.tag.getById(sync.uid, mainElement),
					cometReceber = new Comet(CONTEXT_PATH+'/$synchronize');
				
				cometReceber.setMethodRequest("post");
				cometReceber.setCometType(Comet().LONG_POLLING);
				cometReceber.reconnect(false);
				
				if(e != null) {
					Bootstrap.analizeJSON(mainElement, sync.command.parameters, e);
					
					var p = sync.command.parameters,
						parameters = Bootstrap.analizeParameters(p, "p", mainElement),
						value,
						strEval,
						filter,
						forceArray = false;
					
					if(sync.command.name.indexOf('__partFile') > -1) {
						value = e;
						cometReceber.forceConnectType(Comet().IframeHttpRequest);
					} else{
						if(sync.command.name === "#") {
							value = {};
							for(var i in p)
								value[i] = e[p[i]];
						}else {
							if(sync.command.name.indexOf('#') === 0) {	
								if(sync.command.name.indexOf('##[]') === 0) {
									filter = p;
									strEval = "e."+sync.command.name.substring(4);
									forceArray = true;
								}else if(sync.command.name.indexOf('##') === 0) {
									filter = p;
									strEval = "e."+sync.command.name.substring(2);
								}else
									strEval = "e."+sync.command.name.substring(1);
							} else
								strEval = Bootstrap.adaptiveCommand(sync.command.name, parameters);
										
							value = __isFirefox ? new Function( 'var e = arguments[0]; var p = arguments[1]; return '+strEval)(e, p) : eval(strEval);
						}
						
						if(forceArray || Greencode.jQuery.isArray(value))
							value = Greencode.util.arrayToString(value, filter);
						else if(typeof value === "object")
							value = Greencode.util.objectToString(value, filter);
					}
					cometReceber.send({viewId: viewId, uid: sync.uid, varName: sync.varName, 'var': value});
				}else
					cometReceber.send({viewId: viewId, uid: sync.uid});
				
				delete cometReceber;
				cometReceber = null;
				
				if(!DEBUG_MODE)
					jsonObject.sync
			}
			
			if(jsonObject.errors != null) {					
				var divGreenCodeModalErro = document.createElement("div"),
					spanTitulo = document.createElement("span"),
					spanBotaoFechar = document.createElement("span"),
					topBar = document.createElement("div"),
					contentModalError = document.createElement("div");
				
				divGreenCodeModalErro.setAttribute('id', 'GreenCodemodalErro');			
				for(i in GreencodeStyle.modalErro.style)
					divGreenCodeModalErro.style[i] = GreencodeStyle.modalErro.style[i];
				
				spanTitulo.appendChild(document.createTextNode('Exception:'));
				for(i in GreencodeStyle.modalErro.topBar.title.style)
					spanTitulo.style[i] = GreencodeStyle.modalErro.topBar.title.style[i];
				
				spanBotaoFechar.appendChild(document.createTextNode('X'));
				for(i in GreencodeStyle.modalErro.topBar.closeButton.style)
					spanBotaoFechar.style[i] = GreencodeStyle.modalErro.topBar.closeButton.style[i];
				
				Greencode.crossbrowser.registerEvent.call(spanBotaoFechar, 'click', function(){
					divGreenCodeModalErro.parentNode.removeChild(divGreenCodeModalErro);
				});
				
				for(i in GreencodeStyle.modalErro.topBar.style)
					topBar.style[i] = GreencodeStyle.modalErro.topBar.style[i];
				
				topBar.appendChild(spanTitulo);
				topBar.appendChild(spanBotaoFechar);
				
				divGreenCodeModalErro.appendChild(topBar);
				
				contentModalError.setAttribute('class', 'content');
				for(i in GreencodeStyle.modalErro.content.style)
					contentModalError.style[i] = GreencodeStyle.modalErro.content.style[i];
				
				divGreenCodeModalErro.appendChild(contentModalError);
				
				document.body.appendChild(divGreenCodeModalErro);
				
				for ( var i in jsonObject.errors) {
					var error = jsonObject.errors[i],
						title = error.className+": "+error.message,
						divTitle = document.createElement("div");
					
					for(i in GreencodeStyle.modalErro.content.title.style)
						divTitle.style[i] = GreencodeStyle.modalErro.content.title.style[i];
					
					divTitle.appendChild(document.createTextNode(title));
					
					contentModalError.appendChild(divTitle);
					
					if(error.stackTrace != null) {
						for (var i2 in error.stackTrace) {
							var st = error.stackTrace[i2],
								msg = st.className+'.'+st.methodName+'(',
								lineDiv = document.createElement("div");
							
							msg += (st.lineNumber < 0 ? 'Unknown Source' :  st.fileName+':'+st.lineNumber)+')';
							
							lineDiv.appendChild(document.createTextNode(msg));
							
							if(st.possibleError) {
								for(var i3 in GreencodeStyle.modalErro.content.possibleErro.style)
									lineDiv.style[i3] = GreencodeStyle.modalErro.content.possibleErro.style[i3];
							}else{
								for(var i3 in GreencodeStyle.modalErro.content.lineClass.style)
									lineDiv.style[i3] = GreencodeStyle.modalErro.content.lineClass.style[i3];
							}
							
							contentModalError.appendChild(lineDiv);			
						}			
					}
				};
				
				if(!DEBUG_MODE)
					delete jsonObject.errors;
			}
			
			if(!DEBUG_MODE)
				delete jsonObject;
			
			jsonObject = null;
		}
		
		__jsonObject = null;
	}
	
	var res = Greencode.crossbrowser.querySelectorAll.call(mainElement, '[msg\\:key]');
	for (var i = -1; ++i < res.length;) {
		var e = res[i],
			msg = internationalization_msg[e.getAttribute('msg:key')];
		
		if(Greencode.crossbrowser.hasAttribute.call(e, 'msg:appendText'))
			msg += e.getAttribute('msg:appendText');
		
		if (e.tagName === 'INPUT')
			e.value = msg;
		else
			e.innerHTML = msg;
	}
};