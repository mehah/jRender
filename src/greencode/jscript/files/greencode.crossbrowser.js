if (!Object.keys) {
	Object.keys = (function() {
		'use strict';
		var hasOwnProperty = Object.prototype.hasOwnProperty, hasDontEnumBug = !({
			toString: null
		}).propertyIsEnumerable('toString'), dontEnums = [ 'toString', 'toLocaleString', 'valueOf', 'hasOwnProperty', 'isPrototypeOf', 'propertyIsEnumerable', 'constructor' ], dontEnumsLength = dontEnums.length;
		
		return function(obj) {
			if (typeof obj !== 'object' && (typeof obj !== 'function' || obj === null)) {
				throw new TypeError('Object.keys called on non-object');
			}
			
			var result = [], prop, i;
			
			for(prop in obj) {
				if (hasOwnProperty.call(obj, prop)) {
					result.push(prop);
				}
			}
			
			if (hasDontEnumBug) {
				for(i = 0; i < dontEnumsLength; i++) {
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

EventTarget.prototype.registerEvent = function(eventName, func, data) {
	if (Greencode.events[eventName] != null)
		Greencode.registerEvent(eventName, func, this);
	else if (Greencode.customEvent[eventName] != null)
		Greencode.customEvent[eventName].add.call(this, func, data);
	else if (this.addEventListener)
		this.addEventListener(eventName, func, false);
	else
		this.attachEvent('on' + eventName, func);
};

EventTarget.prototype.removeEvent = function(eventName, func, oficialRemove) {
	if (!oficialRemove && Greencode.customEvent[eventName] != null)
		Greencode.customEvent[eventName].remove.call(this, func);
	else if (this.removeEventListener) {
		if (func == null)
			this.removeEventListener(eventName);
		else
			this.removeEventListener(eventName, func, false);
	} else {
		if (func == null)
			this.detachEvent('on' + eventName);
		else
			this.detachEvent('on' + eventName, func);
	}
};

EventTarget.prototype.shootEvent = function(eventName) {
	if (document.createEventObject) {
		return this.fireEvent('on' + eventName, document.createEventObject())
	} else {
		var evt = document.createEvent("HTMLEvents");
		evt.initEvent(eventName, true, true);
		return !this.dispatchEvent(evt);
	}
};

Element.prototype.childTextConent = function(v) {
	if (v != null) {
		if (this.innerText != null)
			this.innerText = v;
		else
			this.textContent = v;
		return this;
	}
	return this.innerText != null ? this.innerText : this.textContent;
};

Element.prototype.content = function() {
	return this.contentDocument || this.contentWindow.document;
};

if (!('querySelectorAll' in Element.prototype)) {
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

/*
 * Ref:
 * http://stackoverflow.com/questions/2664045/how-to-retrieve-a-styles-value-in-javascript
 */
Element.prototype.getStyle = function(styleProp) {
	var value, defaultView = (this.ownerDocument || document).defaultView;
	if (defaultView && defaultView.getComputedStyle) {
		styleProp = styleProp.replace(/([A-Z])/g, "-$1").toLowerCase();
		return defaultView.getComputedStyle(this, null).getPropertyValue(styleProp);
	} else if (this.currentStyle) {
		styleProp = styleProp.replace(/\-(\w)/g, function(str, letter) {
			return letter.toUpperCase();
		});
		value = this.currentStyle[styleProp];
		if (/^\d+(em|pt|%|ex)?$/i.test(value)) {
			return (function(value) {
				var oldLeft = this.style.left, oldRsLeft = this.runtimeStyle.left;
				this.runtimeStyle.left = this.currentStyle.left;
				this.style.left = value || 0;
				value = this.style.pixelLeft + "px";
				this.style.left = oldLeft;
				this.runtimeStyle.left = oldRsLeft;
				return value;
			})(value);
		}
		return value;
	}
};