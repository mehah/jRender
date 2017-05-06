if (!JSON || !JSON.stringify) {
	Greencode.util.loadScript(Greencode.CONTEXT_PATH + "/jscript/greencode/json3.js", false);
}

if (!Object.keys) {
	Object.keys = (function() {
		'use strict';
		var hasOwnProperty = Object.prototype.hasOwnProperty,
			hasDontEnumBug = !({
				toString: null
			}).propertyIsEnumerable('toString'),
			dontEnums = ['toString', 'toLocaleString', 'valueOf', 'hasOwnProperty', 'isPrototypeOf', 'propertyIsEnumerable', 'constructor'],
			dontEnumsLength = dontEnums.length;

		return function(obj) {
			if (typeof obj !== 'object' && (typeof obj !== 'function' || obj === null)) {
				throw new TypeError('Object.keys called on non-object');
			}

			var result = [],
				prop, i;

			for (prop in obj) {
				if (hasOwnProperty.call(obj, prop)) {
					result.push(prop);
				}
			}

			if (hasDontEnumBug) {
				for (i = 0; i < dontEnumsLength; i++) {
					if (hasOwnProperty.call(obj, dontEnums[i])) {
						result.push(dontEnums[i]);
					}
				}
			}
			return result;
		};
	}());
}

if (!('remove' in Element.prototype)) {
	Element.prototype.remove = function() {
		if (this.parentNode) {
			this.parentNode.removeChild(this);
		}
	};
}

Element.prototype.content = function() {
	return this.contentDocument || this.contentWindow.document;
};

if (!('querySelectorAll' in Element.prototype)) {
	Greencode.util.loadScript(Greencode.CONTEXT_PATH + "/jscript/greencode/sizzle.js", false);

	Element.prototype.querySelectorAll = function(selector) {
		return Sizzle(selector, this);
	};

	Element.prototype.querySelector = function(selector) {
		return Sizzle(selector + ":first", this)[0] || null;
	};
}

if (!('getElementsByClassName' in Element.prototype)) {
	Element.prototype.getElementsByClassName = function(className) {
		return this.querySelectorAll("." + className);
	};
}

if (!('hasAttribute' in Element.prototype)) {
	Element.prototype.hasAttribute = function(attrName) {
		return this[attrName] !== undefined;
	};
}