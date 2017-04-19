Greencode.util = {
	isArray: function(o) {
		return o && typeof o === 'object' && Object.prototype.toString.call(o) == '[object Array]';
	},
	loadScript: function(src, asyc, charset) {
		var request = new Request(src, Request.XMLHttpRequest, Greencode.isRequestSingleton());
		request.setMethodRequest("GET");
		request.setCometType(Request.LONG_POLLING);
		request.reconnect(false);
		request.jsonContentType(false);
		request.setAsync(asyc);
		
		if (charset)
			request.setCharset(charset);
		
		request.send(null, function(data) {
			eval(data);
		});
		
		request = null;
	},
	objectToString: function(v, filter) {
		var str = "{", first = true;
		if (filter != null) {
			for( var p in filter) {
				var i = filter[p], val = v[filter[p]];
				
				if (!first)
					str += ',';
				
				str += i + ':';
				
				if (Greencode.jQuery.type(val) === "string")
					str += "'" + val + "'";
				else if (Greencode.jQuery.type(val) === "object")
					str += Greencode.util.objectToString(val, filter);
				else if (Greencode.jQuery.isArray(val))
					str += Greencode.util.arrayToString(val, filter);
				else
					str += val;
				
				first = false;
			}
		} else {
			for( var i in v) {
				var val = v[i];
				if (typeof val === "function")
					continue;
				
				var res;
				if (filter == null)
					res = true;
				else {
					res = false;
					for( var p in filter) {
						if (filter[p] === i) {
							res = true;
							break;
						}
					}
				}
				
				if (!res)
					continue;
				
				if (!first)
					str += ',';
				
				str += i + ':';
				
				if (Greencode.jQuery.type(val) === "string")
					str += "'" + val + "'";
				else if (Greencode.jQuery.type(val) === "object")
					str += Greencode.util.objectToString(val, filter);
				else if (Greencode.jQuery.isArray(val))
					str += Greencode.util.arrayToString(val, filter);
				else
					str += val;
				
				first = false;
			}
		}
		return str + '}';
	},
	arrayToString: function(v, filter) {
		var str = "[", first = true;
		for(var i = -1; ++i < v.length;) {
			var val = v[i];
			if (typeof val === "function")
				continue;
			
			if (!first)
				str += ',';
			
			if (Greencode.jQuery.type(val) === "string")
				str += "'" + val + "'";
			else if (Greencode.jQuery.type(val) === "object")
				str += Greencode.util.objectToString(val, filter);
			else if (Greencode.jQuery.isArray(val))
				str += Greencode.util.arrayToString(val, filter);
			else
				str += val;
			
			first = false;
		}
		
		return str + ']';
	},
	isElement: function isElement(o) {
		return (typeof HTMLElement === "object" ? o instanceof HTMLElement : o && typeof o === "object" && o.nodeType === 1 && typeof o.nodeName === "string");
	},
	isArraylike: function(obj) {
		var length = obj.length, type = Greencode.jQuery.type(obj);
		
		if (Greencode.jQuery.isWindow(obj))
			return false;
		
		if (obj.nodeType === 1 && length)
			return true;
		
		return type === "array" || type !== "function" && (length === 0 || typeof length === "number" && length > 0 && (length - 1) in obj);
	}
};

String.prototype.replaceAll = function(search, replacement, insensitive) {
	var arg0 = 'g';
	if(insensitive) {
		arg0 += 'i';
	}
	
    return this.replace(new RegExp(search.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"), arg0), replacement);
};