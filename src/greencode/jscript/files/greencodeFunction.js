Greencode.crossbrowser = {
	registerEvent: function(eventName, func, data) {
		if(Greencode.customEvent[eventName] != null)
			Greencode.customEvent[eventName].add.call(this, func, data);
		else if(this.addEventListener)
		    this.addEventListener(eventName, func, false);
		else
			this.attachEvent('on'+eventName, func);
	},
	removeEvent: function(eventName, func) {
		if(Greencode.customEvent[eventName] != null)
			Greencode.customEvent[eventName].remove.call(this, func);
		else if(this.removeEventListener)
		    this.removeEventListener(eventName, func, false);
		else
			this.detachEven('on'+event, func);
	},
	shootEvent: function(eventName) {
		if (document.createEventObject){
			return this.fireEvent('on'+eventName, document.createEventObject())
		} else {
			var evt = document.createEvent("HTMLEvents");
			evt.initEvent(eventName, true, true);
			return !this.dispatchEvent(evt);
		}
	},
	text: function(v) {
		if(v != null) {
			if(this.innerText != null)
				this.innerText = v;
			else
				this.textContent = v;
			return this;
		}
		return this.innerText != null ? this.innerText : this.textContent;
	},
	content: function() {
		return this.contentDocument || this.contentWindow.document;
	},
	querySelectorAll:function (selector) {
		if(!__isIE8orLess)
			return this.querySelectorAll(selector);
		else
			return Sizzle(selector, this);
	},
	querySelector:function (selector) {
		if(!__isIE8orLess)
			return this.querySelector(selector);
		else
			return Sizzle(selector+":first", this)[0] || null;
	},
	getElementsByClassName: function(className) {
		if(this.getElementsByClassName)
			return this.getElementsByClassName(className);
		else
			return Greencode.crossbrowser.querySelectorAll.call(this, "."+className);
	},
	hasAttribute: function(attrName) {
		return this.hasAttribute ? this.hasAttribute(attrName)
                : this[attrName] !== undefined;
	},
	/*
	 * Ref:
	 * http://stackoverflow.com/questions/2664045/how-to-retrieve-a-styles-value-in-javascript
	 */
	getStyle: function(styleProp) {
		var value, defaultView = (this.ownerDocument || document).defaultView;
		if (defaultView && defaultView.getComputedStyle) {
			styleProp = styleProp.replace(/([A-Z])/g, "-$1").toLowerCase();
			return defaultView.getComputedStyle(this, null).getPropertyValue(
					styleProp);
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
	}
};

Greencode.customMethod = {
	replaceWith: function(e) {
		this.parentNode.replaceChild(e, this);
		return this;
	},
	replaceWithController: function(url, cid) {
		var This = this,
			cometReceber = new Comet(url),
			first = false;
		
		cometReceber.setMethodRequest("POST");
		cometReceber.setCometType(Comet().STREAMING);
		cometReceber.reconnect(false);
		cometReceber.forceConnectType(Comet().IframeHttpRequest);
		cometReceber.jsonContentType(false);
		
		var f = function(data) {
			if(data) {
				if(!first) {
					first = true;
					Greencode.customMethod.empty.call(This);
				}
				This.insertAdjacentHTML('beforeEnd', data);
				Bootstrap.init(This);
			}
		};
		
		cometReceber.send({cid: cid, viewId: viewId}, f, f);
		
		delete cometReceber;
		cometReceber = null;
		return this;
	},
	resetForm: function() {
		try {
			this.reset();
		}catch(e) {
			this.reset.click();
		}
		return this;
	},
	empty: function() {
		for (var ii = -1; ++ii < this.childNodes.length;) {
			var c = this.childNodes[ii];
			c.parentNode.removeChild(c);
			--ii;
		}		
		return this;
	},
	getOrCreateElementByTagName: function(tagName) {
		var list = this.getElementsByTagName(tagName);						
		return list.length == 0 ? this.appendChild(document.createElement(tagName)) : list[0];
	},
	fillForm: function(a) {
		for(var i in a) {
			var o = a[i];
			
			var elements = Greencode.crossbrowser.querySelectorAll.call(this, '[name="'+o.name+'"]');
			if(elements == null)
				continue;
			
			if(!Greencode.jQuery.isArray(o.values))
				o.values = [o.values];
			
			var first = elements[0];
			if(first.tagName == 'TEXTAREA')
				first.value = o.values;
			else if(first.tagName == 'SELECT') {
				for(var i2 in first.options) {
					var option = first.options[i2];
					if(first.multiple) {
						for(var i3 in o.values) {
							if(option.value == o.values[i3]) {
								option.selected = true;
								break;
							}
						}
					}else if(option.value == o.values[0]){
						option.selected = true;
						break;
					}
				}
			}else if(first.tagName == 'INPUT') {
				var isRadio = first.type == 'radio';
				if(isRadio || first.type == 'checkbox') {
					for(var i2 in elements) {
						var e = elements[i2];
						var achou = false;
						for(var i3 in o.values) {
							if(e.value == o.values[i3]) {
								e.checked = true;
								if(isRadio) {
									achou = true;
									break;
								}
							}
							
							if(achou)
								break;
						}
					}					
				}else
					first.value = o.values[0];
			}				
		}
		
		return this;
	},
	querySelector: function(selector, attrs, not) {
		return Greencode.customMethod.querySelectorAll.call(this, selector, attrs, not)[0];
	},
	querySelectorAll: function(selector, attrs, not) {
		var newList = new Array(),
			list = Greencode.crossbrowser.querySelectorAll.call(this, selector),
			attrIsString = typeof attrs === "string",
			validator = function() {
				return eval('(function() {' +attrs+ '}.call(this));');
			};
		
		for(var i = -1; ++i < list.length;) {
			var e = list[i];
			
			if(attrIsString) {
				if(validator.call(e))
					newList.push(e);
			} else {
				for(var a in attrs) {
					var pushed = false;
					var attrValues = attrs[a];
					var cssValue = Greencode.crossbrowser.getStyle.call(e, a);
					for(var v in attrValues) {
						if(attrValues[v] == cssValue) {
							newList.push(e);
							pushed = true;
							break;
						}
					}
					if(pushed)
						break;
					else if(not) {
						newList.push(e);
						break;
					}
				}
			}
		}
		
		return newList;
	}
};

Greencode.util = {
	isArray: function(o) { return o && typeof o === 'object' && Object.prototype.toString.call(o) == '[object Array]'; },
	loadScript: function(src, asyc, charset) {
		var cometReceber = new Comet(src);
		cometReceber.setMethodRequest("GET");
		cometReceber.setCometType(Comet().LONG_POLLING);
		cometReceber.reconnect(false);
		cometReceber.setAsync(asyc);
		cometReceber.jsonContentType(false);
		if(charset)
			cometReceber.setCharset(charset);
		
		cometReceber.send(null, function(data) {
			eval(data);
		});
		
		delete cometReceber;
		cometReceber = null;
	},
	objectToString: function(v, filter) {
		var str = "{",
			first = true;
		if(filter != null) {
			for(var p in filter) {
				var i = filter[p],
					val = v[filter[p]];
				
				if(!first)
					str += ',';
				
				str += i+':';
				
				if(Greencode.jQuery.type(val) === "string")
					str += "'"+val+"'";
				else if(Greencode.jQuery.type(val) === "object")				
					str += Greencode.util.objectToString(val, filter);
				else if(Greencode.jQuery.isArray(val))
					str += Greencode.util.arrayToString(val, filter);
				else
					str += val;
				
				first = false;
			}
		}else
		{
			for(var i in v) {
				var val = v[i];
				if(typeof val === "function")
					continue;
				
				var res;
				if(filter == null)
					res = true;
				else {
					res = false;
					for(var p in filter) {
						if(filter[p] === i) {
							res = true;
							break;
						}
					}
				}
				
				if(!res)
					continue;
				
				if(!first)
					str += ',';
				
				str += i+':';
				
				if(Greencode.jQuery.type(val) === "string")
					str += "'"+val+"'";
				else if(Greencode.jQuery.type(val) === "object")				
					str += Greencode.util.objectToString(val, filter);
				else if(Greencode.jQuery.isArray(val))
					str += Greencode.util.arrayToString(val, filter);
				else
					str += val;
				
				first = false;
			}
		}	
		return str+'}';
	},
	arrayToString: function(v, filter) {
		var str = "[",
			first = true;
		for(var i = -1; ++i < v.length;)
		{
			var val = v[i];
			if(typeof val === "function")
				continue;
			
			if(!first)
				str += ',';
			
			if(Greencode.jQuery.type(val) === "string")
				str += "'"+val+"'";
			else if(Greencode.jQuery.type(val) === "object")				
				str += Greencode.util.objectToString(val, filter);
			else if(Greencode.jQuery.isArray(val))
				str += Greencode.util.arrayToString(val, filter);
			else
				str += val;
			
			first = false;
		}
		
		return str+']';
	}, isElement: function isElement(o){
		return (typeof HTMLElement === "object" ? o instanceof HTMLElement :
			o && typeof o === "object" && o.nodeType === 1 && typeof o.nodeName==="string"
		);
	}, isArraylike: function( obj ) {
		var length = obj.length,
			type = Greencode.jQuery.type( obj );
	
		if ( Greencode.jQuery.isWindow( obj ) ) return false;
	
		if ( obj.nodeType === 1 && length ) return true;
	
		return type === "array" || type !== "function" &&
			( length === 0 ||
			typeof length === "number" && length > 0 && ( length - 1 ) in obj );
	}
};

Greencode.jQuery = {
	class2type: {},
	core_hasOwn: ({}).hasOwnProperty,
	__buildParams: function( prefix, obj, traditional, add ) {
		var rbracket = /\[\]$/;
		var name;
	
		if ( Greencode.jQuery.isArray( obj ) ) {
			Greencode.jQuery.each( obj, function( i, v ) {
				if ( traditional || rbracket.test( prefix ) )
					add( prefix, v );	
				else
					Greencode.jQuery.__buildParams( prefix + "[" + ( typeof v === "object" ? i : "" ) + "]", v, traditional, add );
			});	
		} else if ( !traditional && Greencode.jQuery.type( obj ) === "object" ) {
			for ( name in obj )
				Greencode.jQuery.__buildParams( prefix + "[" + name + "]", obj[ name ], traditional, add );
		} else
			add( prefix, obj );
	},
	each: function( obj, callback, args ) {
		var value,
			i = 0,
			length = obj.length,
			isArray = Greencode.util.isArraylike( obj );
	
		if ( args ) {
			if ( isArray ) {
				for ( ; i < length; i++ ) {
					value = callback.apply( obj[ i ], args );
	
					if ( value === false ) break;
				}
			} else {
				for ( i in obj ) {
					value = callback.apply( obj[ i ], args );
	
					if ( value === false ) break;
				}
			}
		} else {
			if ( isArray ) {
				for ( ; i < length; i++ ) {
					value = callback.call( obj[ i ], i, obj[ i ] );
	
					if ( value === false ) break;
				}
			} else {
				for ( i in obj ) {
					value = callback.call( obj[ i ], i, obj[ i ] );
	
					if ( value === false ) break;
				}
			}
		}
	
		return obj;
	},
	paramObject: function( a, traditional) {
		var prefix,
			s = [],
			add = function( key, value ) {
				value = Greencode.jQuery.isFunction( value ) ? value() : ( value == null ? "" : value );
				s[ s.length ] = {key: key, value: value};
			};
	
		if ( traditional === undefined ) traditional = false;
	
		if ( Greencode.jQuery.isArray( a ) || ( a.jquery && !Greencode.jQuery.isPlainObject( a ) ) ) {
			Greencode.jQuery.each( a, function() {
				add( this.name, this.value );
			});
		} else {
			for ( prefix in a )
				Greencode.jQuery.__buildParams( prefix, a[ prefix ], traditional, add );
		}
	
		return s;
	},
	param: function( a, traditional ) {
		var prefix,
			s = [],
			add = function( key, value ) {
				value = Greencode.jQuery.isFunction( value ) ? value() : ( value == null ? "" : value );
				s[ s.length ] = encodeURIComponent( key ) + "=" + encodeURIComponent( value );
			};
	
		if ( traditional === undefined ) traditional = false;
	
		if ( Greencode.jQuery.isArray( a ) || ( a.jquery && !Greencode.jQuery.isPlainObject( a ) ) ) {
			Greencode.jQuery.each( a, function() {
				add( this.name, this.value );
			});
		} else {
			for ( prefix in a )
				Greencode.jQuery.__buildParams( prefix, a[ prefix ], traditional, add );
		}
		return s.join( "&" ).replace( /%20/g, "+" );
	},
	isFunction: function( obj ) {
		return Greencode.jQuery.type(obj) === "function";
	},
	isArray: Array.isArray || function( obj ) {
		return Greencode.jQuery.type(obj) === "array";
	},
	isWindow: function( obj ) {
		return obj != null && obj == obj.window;
	},
	isNumeric: function( obj ) {
		return !isNaN( parseFloat(obj) ) && isFinite( obj );
	},
	type: function( obj ) {
		if ( obj == null )
			return String( obj );
		return typeof obj === "object" || typeof obj === "function" ?
			Greencode.jQuery.class2type[ Greencode.jQuery.class2type.toString.call(obj) ] || "object" :
			typeof obj;
	},
	isPlainObject: function( obj ) {
		var key;
	
		if ( !obj || Greencode.jQuery.type(obj) !== "object" || obj.nodeType || Greencode.jQuery.isWindow( obj ) )
			return false;
		
		try {
			if ( obj.constructor &&
				!Greencode.jQuery.core_hasOwn.call(obj, "constructor") &&
				!Greencode.jQuery.core_hasOwn.call(obj.constructor.prototype, "isPrototypeOf") ) {
				return false;
			}
		} catch ( e ) {
			return false;
		}
		for ( key in obj ) {}
	
		return key === undefined || Greencode.jQuery.core_hasOwn.call( obj, key );
	},
	isEmptyObject: function( obj ) {
		var name;
		for ( name in obj )
			return false;
		return true;
	},
	extend: function() {
		var src, copyIsArray, copy, name, options, clone,
			target = arguments[0] || {},
			i = 1,
			length = arguments.length,
			deep = false;

		if ( typeof target === "boolean" ) {
			deep = target;
			target = arguments[1] || {};
			i = 2;
		}

		if ( typeof target !== "object" && !Greencode.jQuery.isFunction(target) )
			target = {};
		
		if ( length === i ) {
			target = this;
			--i;
		}

		for ( ; i < length; i++ ) {
			if ( (options = arguments[ i ]) != null ) {
				for ( name in options ) {
					src = target[ name ];
					copy = options[ name ];

					if ( target === copy )
						continue;

					if ( deep && copy && ( Greencode.jQuery.isPlainObject(copy) || (copyIsArray = Greencode.jQuery.isArray(copy)) ) ) {
						if ( copyIsArray ) {
							copyIsArray = false;
							clone = src && Greencode.jQuery.isArray(src) ? src : [];
						} else
							clone = src && Greencode.jQuery.isPlainObject(src) ? src : {};

						target[ name ] = Greencode.jQuery.extend( deep, clone, copy );

					} else if ( copy !== undefined )
						target[ name ] = copy;
				}
			}
		}
		return target;
	}
};