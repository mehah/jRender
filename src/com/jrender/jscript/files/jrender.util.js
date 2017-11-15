JRender.util = {
	isArray: function(o) {
		return o && typeof o === 'object' && Object.prototype.toString.call(o) == '[object Array]';
	},
	loadScript: function(src, asyc, charset) {
		var request = new Request(src, Request.XMLHttpRequest, JRender.isRequestSingleton());
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
		var str = "{",
			first = true;
		if (filter != null) {
			for (var p in filter) {
				var i = filter[p],
					val = v[filter[p]];

				if (!first)
					str += ',';

				str += i + ':';

				if (JRender.jQuery.type(val) === "string")
					str += "'" + val + "'";
				else if (JRender.jQuery.type(val) === "object")
					str += JRender.util.objectToString(val, filter);
				else if (JRender.jQuery.isArray(val))
					str += JRender.util.arrayToString(val, filter);
				else
					str += val;

				first = false;
			}
		} else {
			for (var i in v) {
				var val = v[i];
				if (typeof val === "function")
					continue;

				var res;
				if (filter == null)
					res = true;
				else {
					res = false;
					for (var p in filter) {
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

				if (JRender.jQuery.type(val) === "string")
					str += "'" + val + "'";
				else if (JRender.jQuery.type(val) === "object")
					str += JRender.util.objectToString(val, filter);
				else if (JRender.jQuery.isArray(val))
					str += JRender.util.arrayToString(val, filter);
				else
					str += val;

				first = false;
			}
		}
		return str + '}';
	},
	arrayToString: function(v, filter) {
		var str = "[",
			first = true;
		for (var i = -1; ++i < v.length;) {
			var val = v[i];
			if (typeof val === "function")
				continue;

			if (!first)
				str += ',';

			if (JRender.jQuery.type(val) === "string")
				str += "'" + val + "'";
			else if (JRender.jQuery.type(val) === "object")
				str += JRender.util.objectToString(val, filter);
			else if (JRender.jQuery.isArray(val))
				str += JRender.util.arrayToString(val, filter);
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
		var length = obj.length,
			type = JRender.jQuery.type(obj);

		if (JRender.jQuery.isWindow(obj))
			return false;

		if (obj.nodeType === 1 && length)
			return true;

		return type === "array" || type !== "function" && (length === 0 || typeof length === "number" && length > 0 && (length - 1) in obj);
	},
	getQueryStrings: function() {
		var assoc = {};
		var decode = function(s) {
			return decodeURIComponent(s.replace(/\+/g, " "));
		};
		var queryString = location.search.substring(1);
		var keyValues = queryString.split('&');

		for (var i in keyValues) {
			var key = keyValues[i].split('=');
			if (key.length > 1) {
				assoc[decode(key[0])] = decode(key[1]);
			}
		}

		return assoc;
	}
};

String.prototype.replaceAll = function(search, replacement, insensitive) {
	var arg0 = 'g';
	if (insensitive) {
		arg0 += 'i';
	}

	return this.replace(new RegExp(search.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"), arg0), replacement);
};