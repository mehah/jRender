JRender.jQuery = {
	class2type: {},
	core_hasOwn: ({}).hasOwnProperty,
	__buildParams: function(prefix, obj, traditional, add) {
		var rbracket = /\[\]$/;
		var name;

		if (JRender.jQuery.isArray(obj)) {
			JRender.jQuery.each(obj, function(i, v) {
				if (traditional || rbracket.test(prefix))
					add(prefix, v);
				else
					JRender.jQuery.__buildParams(prefix + "[" + (typeof v === "object" ? i : "") + "]", v, traditional, add);
			});
		} else if (!traditional && JRender.jQuery.type(obj) === "object") {
			for (name in obj)
				JRender.jQuery.__buildParams(prefix + "[" + name + "]", obj[name], traditional, add);
		} else
			add(prefix, obj);
	},
	each: function(obj, callback, args) {
		var value, i = 0,
			length = obj.length,
			isArray = JRender.util.isArraylike(obj);

		if (args) {
			if (isArray) {
				for (; i < length; i++) {
					value = callback.apply(obj[i], args);

					if (value === false)
						break;
				}
			} else {
				for (i in obj) {
					value = callback.apply(obj[i], args);

					if (value === false)
						break;
				}
			}
		} else {
			if (isArray) {
				for (; i < length; i++) {
					value = callback.call(obj[i], i, obj[i]);

					if (value === false)
						break;
				}
			} else {
				for (i in obj) {
					value = callback.call(obj[i], i, obj[i]);

					if (value === false)
						break;
				}
			}
		}

		return obj;
	},
	paramObject: function(a, traditional) {
		var prefix, s = [],
			add = function(key, value) {
				value = JRender.jQuery.isFunction(value) ? value() : value || "";
				s[s.length] = {
					key: key,
					value: value
				};
			};

		if (traditional === undefined)
			traditional = false;

		if (JRender.jQuery.isArray(a) || (a.jquery && !JRender.jQuery.isPlainObject(a))) {
			JRender.jQuery.each(a, function() {
				add(this.name, this.value);
			});
		} else {
			for (prefix in a)
				JRender.jQuery.__buildParams(prefix, a[prefix], traditional, add);
		}

		return s;
	},
	param: function(a, traditional) {
		var prefix, s = [],
			add = function(key, value) {
				value = JRender.jQuery.isFunction(value) ? value() : value || "";
				s[s.length] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
			};

		if (traditional === undefined)
			traditional = false;

		if (JRender.jQuery.isArray(a) || (a.jquery && !JRender.jQuery.isPlainObject(a))) {
			JRender.jQuery.each(a, function() {
				add(this.name, this.value);
			});
		} else {
			for (prefix in a)
				JRender.jQuery.__buildParams(prefix, a[prefix], traditional, add);
		}
		return s.join("&").replace(/%20/g, "+");
	},
	isFunction: function(obj) {
		return JRender.jQuery.type(obj) === "function";
	},
	isArray: Array.isArray || function(obj) {
		return JRender.jQuery.type(obj) === "array";
	},
	isWindow: function(obj) {
		return obj != null && obj == obj.window;
	},
	isNumeric: function(obj) {
		return !isNaN(parseFloat(obj)) && isFinite(obj);
	},
	type: function(obj) {
		if (obj == null)
			return String(obj);
		return typeof obj === "object" || typeof obj === "function" ? JRender.jQuery.class2type[JRender.jQuery.class2type.toString.call(obj)] || "object" : typeof obj;
	},
	isPlainObject: function(obj) {
		var key;

		if (!obj || JRender.jQuery.type(obj) !== "object" || obj.nodeType || JRender.jQuery.isWindow(obj))
			return false;

		try {
			if (obj.constructor && !JRender.jQuery.core_hasOwn.call(obj, "constructor") && !JRender.jQuery.core_hasOwn.call(obj.constructor.prototype, "isPrototypeOf")) {
				return false;
			}
		} catch (e) {
			return false;
		}
		for (key in obj) {}

		return key === undefined || JRender.jQuery.core_hasOwn.call(obj, key);
	},
	isEmptyObject: function(obj) {
		var name;
		for (name in obj)
			return false;
		return true;
	},
	extend: function() {
		var src, copyIsArray, copy, name, options, clone, target = arguments[0] || {},
			i = 1,
			length = arguments.length,
			deep = false;

		if (typeof target === "boolean") {
			deep = target;
			target = arguments[1] || {};
			i = 2;
		}

		if (typeof target !== "object" && !JRender.jQuery.isFunction(target))
			target = {};

		if (length === i) {
			target = this;
			--i;
		}

		for (; i < length; i++) {
			if ((options = arguments[i]) != null) {
				for (name in options) {
					src = target[name];
					copy = options[name];

					if (target === copy)
						continue;

					if (deep && copy && (JRender.jQuery.isPlainObject(copy) || (copyIsArray = JRender.jQuery.isArray(copy)))) {
						if (copyIsArray) {
							copyIsArray = false;
							clone = src && JRender.jQuery.isArray(src) ? src : [];
						} else
							clone = src && JRender.jQuery.isPlainObject(src) ? src : {};

						target[name] = JRender.jQuery.extend(deep, clone, copy);

					} else if (copy !== undefined)
						target[name] = copy;
				}
			}
		}
		return target;
	}
};