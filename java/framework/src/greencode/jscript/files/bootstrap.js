var cleanObject = false;
var internationalization_msg = {};
var Bootstrap = {};

Bootstrap.analizeJSON = function(mainElement, j, target) {
	if(j == null)
		return;
	
	if(target == null)
		target = {};
	
	Greencode.jQuery.each(j, function(i) {
		var p = this;
		if (p.isFunction === true) {
			j[i] = function(event) {
				if (event == null) {
					event = {};
					event.type = 'undefined';
				}
				
				Bootstrap.callRequestMethod(mainElement, target, event, p, arguments);
			};
		}
	});
};

Bootstrap.analizeParameters = function(parameters, variableName, mainElement)
{
	var methodParameters = "";
	if(parameters != null)
	{
		for(var i in parameters)
		{		
			if(i != 0)
				methodParameters += ',';
		
			var o = parameters[i];
			
			if(typeof o === 'string' && o.indexOf('*ref') != -1)
			{
				methodParameters += 'Greencode.tag.getById('+o.split('*ref')[0]+', mainElement)';
			}else							
				methodParameters += variableName+'['+i+']';
		}
	}
	
	return methodParameters;
};

Bootstrap.getObjectByEvent = function(o, name) {
	if (o.events == null) {
		o.events = {};
	}

	if(o.events[name] == null)
	{
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
		{
			methodParameters = Bootstrap.analizeParameters(p.parametersRequest.methodParameters, "p.parametersRequest.methodParameters");
		}
		
		if(__arguments != null && __arguments.length > 0)
		{
			if(methodParameters !== '')
			{
				methodParameters += ',';
				for(var i in __arguments)
				{
					if(i != 0)
						methodParameters += ',';
					methodParameters += '__arguments['+i+']';
				}
			}				
		}
		
		eval('target.'+p.url.substring(1)+'('+methodParameters+');');
	}else
	{
		objectEvent.processing = true;		
		var bckParameter = p.parametersRequest;
		if(bckParameter == null)
			bckParameter = {};

		var _args = null;
		if(p.args != null)
		{
			_args = [];
			for (var i in p.args) {
				var o = p.args[i];
				var arg = __arguments[i];
				if(arg != null)
				{
					var _arg = {className: o.className, fields: {}};
					$.each(o.fields, function() {
						var value = arg[this];
						
						if(isElement(value))
							_arg.fields[this] = {create:false};
						else if(value != null && !jQuery.isFunction(value) && !isElement(value))
							_arg.fields[this] = value;
					});
					
					_arg.fields = JSON.stringify(_arg.fields);
					
					_args.push(JSON.stringify(_arg));
				}
			}
		}
		
		/*bckParameter.cid = p.cid;*/
		bckParameter.viewId = viewId;
		if(_args != null)
			bckParameter._args = _args;
		
		if(target.window == null)
			bckParameter._buttonId = target.id == null ? target.name : target.id;
		else
			bckParameter._buttonId = 'Window';
		
		var onComplete = function(data) {
			Bootstrap.init(mainElement, data);
			
			objectEvent.processing = false;
			
			if(objectEvent.methods.length > 0)
			{
				objectEvent.methods[0]();
				objectEvent.methods.splice(0, 1);
			}
		};
		
		var cometReceber = new Comet(p.url);
		cometReceber.setMethodRequest(p.requestMethod);
		cometReceber.setCometType(Comet().STREAMING);
		cometReceber.reconnect(false);
		cometReceber.setAsync(p.async);
		
		cometReceber.send(bckParameter, function(data) {
			Bootstrap.init(mainElement, data);
		}, onComplete);
		
		delete cometReceber;
		cometReceber = null;
		
		if(cleanObject)
		{
			delete bckParameter;
			delete tagEventObject;
		}
	}
};

Bootstrap.crossbrowserCommand = function(commandName, parameters)
{	
	if(commandName.indexOf('crossbrowser') != -1)
		return 'Greencode.' + commandName + '.call(e, '+ parameters + ');'
	else
		return 'e.' + commandName + '('	+ parameters + ');'
};

Bootstrap.readCommand = function(mainElement)
{
	if(this == null)
		return;
	
	var e = Greencode.tag.getById(this.uid, mainElement);
	
	var parameters = '';

	if (this.parameters != null) {
		Bootstrap.analizeJSON(mainElement, this.parameters, e);				
		parameters = Bootstrap.analizeParameters(this.parameters, "this.parameters", mainElement);
	}
	
	if(this.name.indexOf('*ref.') != -1)
	{
		var split = this.name.split('*ref.');
		eval('Greencode.tag.references['+split[0]+'+""] = '+crossbrowserCommand(split[1], parameters));
	}else if(this.name.indexOf('*prop.') != -1)
	{
		var split = this.name.split('*prop.');
		eval('e.' + split[1] + '='+parameters+';');
	}else
	{
		eval(crossbrowserCommand(this.name, parameters));
	}
};

Bootstrap.init = function(mainElement, jsonObject, args) {
	if (mainElement == null)
		mainElement = document.body;
	
	if(Greencode.ready != null)
		Greencode.ready.call(mainElement);
	
	if(jsonObject == null)
	{
		var div = document.getElementById('JSON_CONTENT');
		if(div != null)
		{
			jsonObject = JSON.parse(Greencode.crossbrowser.text.call(div));
			div.parentNode.removeChild(div);
		}
	}
	
	if(jsonObject != null)
	{
		if(jsonObject.pComm != null && jsonObject.pComm.length > 0)
		{
			for(var i in jsonObject.pComm)
			{
				var _this = jsonObject.pComm[i];
				Bootstrap.readCommand.call(_this, mainElement);
			}
		}
		
		if(jsonObject.comm != null && jsonObject.comm.length > 0)
		{
			for(var i in jsonObject.comm)
			{
				var _this = jsonObject.comm[i];
				Bootstrap.readCommand.call(_this, mainElement);
			}
		}
		
		if(jsonObject.sync != null)
		{
			var sync = jsonObject.sync;
			var e = Greencode.tag.getById(sync.uid, mainElement);

			var cometReceber = new Comet(CONTEXT_PATH+'/$synchronize');
			cometReceber.setMethodRequest("post");
			cometReceber.setCometType(Comet().LONG_POLLING);
			cometReceber.reconnect(false);
			
			if(e != null)
			{					
				var p = sync.command.parameters;
				
				Bootstrap.analizeJSON(mainElement, sync.command.parameters, e);					
				var parameters = Bootstrap.analizeParameters(p, "p", mainElement);
				
				var value;
								
				if(sync.command.name.indexOf('#') === 0)
					value = eval("e."+sync.command.name.substring(1));
				else
					value = eval(crossbrowserCommand(sync.command.name, parameters));
				
				cometReceber.send({viewId: viewId, uid: sync.uid, varName: sync.varName, 'var': (Greencode.jQuery.isPlainObject(value) || Greencode.jQuery.isArray(value)) ? JSON.stringify(value) : value});
			}else
			{
				cometReceber.send({viewId: viewId, uid: sync.uid});
			}
			
			delete cometReceber;
			cometReceber = null;
		}
		
		if(jsonObject.errors != null)
		{					
			var divGreenCodeModalErro = document.createElement("div");
			divGreenCodeModalErro.setAttribute('id', 'GreenCodemodalErro');			
			for(i in GreencodeStyle.modalErro.style)
				divGreenCodeModalErro.style[i] = GreencodeStyle.modalErro.style[i];
			
			var spanTitulo = document.createElement("span");
			spanTitulo.appendChild(document.createTextNode('Exception:'));
			for(i in GreencodeStyle.modalErro.topBar.title.style)
				spanTitulo.style[i] = GreencodeStyle.modalErro.topBar.title.style[i];
			
			var spanBotaoFechar = document.createElement("span");
			spanBotaoFechar.appendChild(document.createTextNode('X'));
			for(i in GreencodeStyle.modalErro.topBar.closeButton.style)
				spanBotaoFechar.style[i] = GreencodeStyle.modalErro.topBar.closeButton.style[i];
			
			Greencode.crossbrowser.registerEvent.call(spanBotaoFechar, 'click', function(){
				divGreenCodeModalErro.parentNode.removeChild(divGreenCodeModalErro);
			});
			
			var topBar = document.createElement("div");
			for(i in GreencodeStyle.modalErro.topBar.style)
				topBar.style[i] = GreencodeStyle.modalErro.topBar.style[i];
			
			topBar.appendChild(spanTitulo);
			topBar.appendChild(spanBotaoFechar);
			
			divGreenCodeModalErro.appendChild(topBar);
			
			var contentModalError = document.createElement("div");
			contentModalError.setAttribute('class', 'content');
			for(i in GreencodeStyle.modalErro.content.style)
				contentModalError.style[i] = GreencodeStyle.modalErro.content.style[i];
			
			divGreenCodeModalErro.appendChild(contentModalError);
			
			document.body.appendChild(divGreenCodeModalErro);
			
			for ( var i in jsonObject.errors) {
				var error = jsonObject.errors[i];
				
				var title = error.className+": "+error.message;
				
				var divTitle = document.createElement("div");
				for(i in GreencodeStyle.modalErro.content.title.style)
					divTitle.style[i] = GreencodeStyle.modalErro.content.title.style[i];
				
				divTitle.appendChild(document.createTextNode(title));
				
				contentModalError.appendChild(divTitle);
				
				if(error.stackTrace != null)
				{
					for ( var i2 in error.stackTrace) {
						var st = error.stackTrace[i2];
						
						var msg = st.className+'.'+st.methodName+'(';
						
						if(st.lineNumber < 0)
							msg += 'Unknown Source';
						else
							msg += st.fileName+':'+st.lineNumber;
						msg += ')';
						
						var lineDiv = document.createElement("div");
						lineDiv.appendChild(document.createTextNode(msg));
						
						if(st.possibleError)
						{
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
			
			if(cleanObject)
				delete jsonObject.errors;
		}
		
		if(cleanObject)
			delete jsonObject;
		
		jsonObject = null;
	}
};