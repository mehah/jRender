/*
 - Simulador de Socket Real-Time. Por: Renato Machado dos Santos
 */
var Comet = function(url)
{
	if(url == null)
	{
		return {
			LONG_POLLING: 1,
			STREAMING: 2
		};
	}
	
	var o = this;	
	var ajaxRequest = null;
	
	var data = null;
	var async = true;
	var methodRequest = "GET";
	var reconnectDelay = 500;
	var cometType = Comet().LONG_POLLING;
	var reconnect = true;
	var txtCheckStatus = ',';
	var closed = true;
	var onError = null;
	var onAbort = null;
	var eventReconnect = null;
	var state = 0;
	/*var useXDR = false;*/
	
	this.reconnect = function(b) { if(b == null) return reconnect; reconnect = b; };
	
	this.setCometType = function(c) { cometType = c; };
	this.getCometType = function() { return cometType; };
	
	this.setData = function(d) { data = d; };	
	this.getData = function() { return data; };
	
	this.setMethodRequest = function(m) { methodRequest = m; };	
	this.getMethodRequest = function() { return methodRequest; };
	
	this.setAsync = function(v) { async = v; };	
	this.isAsync = function() { return async; };
	
	this.getURL = function() { return url; };
	this.setURL = function(urll) { url = urll; };
	
	this.getState = function() { return state; };
		
	this.abort = function() { ajaxRequest.abort(); this.onAbort(); };
	
	this.isClosed = function() { return closed; };
	
	this.send = function(p, c1, c2)
	{
		if(ajaxRequest === null)
		{
			if(this.getCometType() === Comet().STREAMING && window.ActiveXObject != null)
			{
				ajaxRequest = new IframeHttpRequest();
			}
			else
			{	
				try {
					ajaxRequest = new XMLHttpRequest();
				} catch (e){
					try{
						ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
					} catch (e){
						alert("Não foi possivel abrir uma instancia de conexão!");
						return false;
					}
				}
			}
		}
		
		if(p != null && data == null)
			data = p;
		
		if(closed === true)
		{
			var hasContent = !(/^(?:GET|HEAD)$/.test(methodRequest));
			
			var parameters = "";
			if(!(ajaxRequest instanceof IframeHttpRequest) && data != null)
			{
				if(data instanceof Object)
				{
					data = Greencode.jQuery.param(data);
					if(!hasContent)
						parameters = (parameters.indexOf('?') === -1 ? '?' : '&')+data;
				}
			}
			var newURL = url+parameters;
			
			ajaxRequest.open(methodRequest, newURL, async);
			if(hasContent)
				ajaxRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			
			ajaxRequest.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			newURL = null;
			closed = false;
		}
		
		if(c1 != null)
			this.onMessage(c1, c2);
		
		ajaxRequest.send(data != null ? data : null);
	};
		
	this.onAbort = function(c) {
		if(c == null)
		{
			clearTimeout(eventReconnect);
			eventReconnect = null;
			
			closed = true;
			if(onAbort != null)
				onAbort.call(o);
		}else
			onAbort = c;
	};
	this.onError = function(c) { onError = c; };
	
	var erroEvent = function()
	{
		clearTimeout(eventReconnect);
		eventReconnect = null;
		
		closed = true;
		if(onError != null)
			onError.call(o);
	};	
	
	/*function getCookie(c_name)
	{
		var i,x,y,ARRcookies=document.cookie.split(";");
		for (i=-1;++i<ARRcookies.length;)
		{
			x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
			y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
			x=x.replace(/^\s+|\s+$/g,"");
			if (x==c_name){return unescape(y);}
		}
	}*/
	
	function isArray(value) {
	    if (value) {
	        if (typeof value === 'object') {
	            return (Object.prototype.toString.call(value) == '[object Array]')
	        }
	    }
	    return false;
	}
	
	this.onMessage = function(c1, c2)
	{
		var ultLength = null;
		ajaxRequest.onreadystatechange = null;
		
		var o = this;
		
		window.addEventListener("unload", function () { o.abort(); }, false);
		
		ajaxRequest.onreadystatechange = function(){
			state = this.readyState;
			
			if(this.status !== 200)
			{
				erroEvent();
			}else if(this.readyState === this.UNSENT)
			{
				o.onAbort();
			}else if(o.getCometType() === Comet().LONG_POLLING && this.readyState === this.DONE)
			{
				var reconnectByReturn = c1.call(o, JSON.parse(ajaxRequest.responseText));
				
				closed = true;
				if(o.reconnect() === true && reconnectByReturn !== false)
				{
					eventReconnect = setTimeout(function() {
						o.send(null, c1);
					}, reconnectDelay);
					return;
				}
			}else if(o.getCometType() === Comet().STREAMING)
			{
				var txt = ajaxRequest.responseText.substring(ultLength);
				
				while (txt.indexOf(txtCheckStatus) === 0) {
					txt = txt.substring(1);	
				}
				
				var data = null;
				
				if(txt !== "" && txt !== txtCheckStatus)
				{
					try {
						data = JSON.parse(txt);
					} catch (e) {
						data = JSON.parse('['+txt+']');
					}
				}
				
				if(this.readyState === this.DONE)
				{
					var reconnectByReturn = true;
					
					if(c2 != null)
					{
						reconnectByReturn = c2.call(o, data);
					}
					
					closed = true;
					if(o.reconnect() === true && reconnectByReturn !== false)
					{						
						eventReconnect = setTimeout(function() {
							o.send(null, c1, c2);
						}, reconnectDelay);
						
						return;
					}
				}else if(this.readyState === this.LOADING || this.readyState === this.HEADERS_RECEIVED)
				{
					if(txt !== "" && txt !== txtCheckStatus)
					{
						if(isArray(data))
						{
							for ( var i in data) {
								c1.call(o, data[i]);
							}
						}else
						{
							c1.call(o, data);
						}							
					}
				}
				
				ultLength = ajaxRequest.responseText.length;
			}
		};
	};
};