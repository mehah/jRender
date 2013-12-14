Greencode.crossbrowser = {
	registerEvent: function(eventName, func)
	{
		if(this.addEventListener)
		    this.addEventListener(eventName, func, false);
		else
			this.attachEvent('on'+eventName, func);
	},
	removeEvent: function(eventName, func)
	{
		if(this.removeEventListener)
		    this.removeEventListener(eventName, func, false);
		else
			this.detachEven('on'+event, func);
	},
	shootEvent: function(eventName)
	{
		if (document.createEventObject){
			var evt = document.createEventObject();
			return this.fireEvent('on'+eventName, evt)
		}
		else{
			var evt = document.createEvent("HTMLEvents");
			evt.initEvent(event, true, true);
			return !this.dispatchEvent(evt);
		}
	},
	text: function(e)
	{
		return this.innerText ? this.innerText : this.textContent;
	}
};


function isArraylike( obj ) {
	var length = obj.length,
		type = Greencode.jQuery.type( obj );

	if ( Greencode.jQuery.isWindow( obj ) ) {
		return false;
	}

	if ( obj.nodeType === 1 && length ) {
		return true;
	}

	return type === "array" || type !== "function" &&
		( length === 0 ||
		typeof length === "number" && length > 0 && ( length - 1 ) in obj );
}

Greencode.jQuery = {
	class2type: {},
	__buildParams: function( prefix, obj, traditional, add ) {
		var rbracket = /\[\]$/;
		var name;
	
		if ( Greencode.jQuery.isArray( obj ) ) {
			Greencode.jQuery.each( obj, function( i, v ) {
				if ( traditional || rbracket.test( prefix ) ) {
					add( prefix, v );
	
				} else {
					Greencode.jQuery.__buildParams( prefix + "[" + ( typeof v === "object" ? i : "" ) + "]", v, traditional, add );
				}
			});
	
		} else if ( !traditional && Greencode.jQuery.type( obj ) === "object" ) {
			for ( name in obj ) {
				Greencode.jQuery.__buildParams( prefix + "[" + name + "]", obj[ name ], traditional, add );
			}
	
		} else {
			add( prefix, obj );
		}
	},
	each: function( obj, callback, args ) {
		var value,
			i = 0,
			length = obj.length,
			isArray = isArraylike( obj );
	
		if ( args ) {
			if ( isArray ) {
				for ( ; i < length; i++ ) {
					value = callback.apply( obj[ i ], args );
	
					if ( value === false ) {
						break;
					}
				}
			} else {
				for ( i in obj ) {
					value = callback.apply( obj[ i ], args );
	
					if ( value === false ) {
						break;
					}
				}
			}
		} else {
			if ( isArray ) {
				for ( ; i < length; i++ ) {
					value = callback.call( obj[ i ], i, obj[ i ] );
	
					if ( value === false ) {
						break;
					}
				}
			} else {
				for ( i in obj ) {
					value = callback.call( obj[ i ], i, obj[ i ] );
	
					if ( value === false ) {
						break;
					}
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
	
		if ( traditional === undefined ) {
			traditional = false;
		}
	
		if ( Greencode.jQuery.isArray( a ) || ( a.jquery && !Greencode.jQuery.isPlainObject( a ) ) ) {
			Greencode.jQuery.each( a, function() {
				add( this.name, this.value );
			});
	
		} else {
			for ( prefix in a ) {
				Greencode.jQuery.__buildParams( prefix, a[ prefix ], traditional, add );
			}
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
	
		if ( traditional === undefined ) {
			traditional = false;
		}
	
		if ( Greencode.jQuery.isArray( a ) || ( a.jquery && !Greencode.jQuery.isPlainObject( a ) ) ) {
			Greencode.jQuery.each( a, function() {
				add( this.name, this.value );
			});
	
		} else {
			for ( prefix in a ) {
				Greencode.jQuery.__buildParams( prefix, a[ prefix ], traditional, add );
			}
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
		/* jshint eqeqeq: false */
		return obj != null && obj == obj.window;
	},
	isNumeric: function( obj ) {
		return !isNaN( parseFloat(obj) ) && isFinite( obj );
	},
	type: function( obj ) {
		if ( obj == null ) {
			return String( obj );
		}
		return typeof obj === "object" || typeof obj === "function" ?
			Greencode.jQuery.class2type[ Greencode.jQuery.class2type.toString.call(obj) ] || "object" :
			typeof obj;
	},
	isPlainObject: function( obj ) {
		var key;
	
		if ( !obj || Greencode.jQuery.type(obj) !== "object" || obj.nodeType || Greencode.jQuery.isWindow( obj ) ) {
			return false;
		}
	
		try {
			if ( obj.constructor &&
				!core_hasOwn.call(obj, "constructor") &&
				!core_hasOwn.call(obj.constructor.prototype, "isPrototypeOf") ) {
				return false;
			}
		} catch ( e ) {
			return false;
		}
	
		/*// Support: IE<9
		// Handle iteration over inherited properties before own properties.
		if ( jQuery.support.ownLast ) {
			for ( key in obj ) {
				return core_hasOwn.call( obj, key );
			}
		}
	
		// Own properties are enumerated firstly, so to speed up,
		// if last one is own, then all properties are own.
		*/
		for ( key in obj ) {}
	
		return key === undefined || core_hasOwn.call( obj, key );
	},
	isEmptyObject: function( obj ) {
		var name;
		for ( name in obj ) {
			return false;
		}
		return true;
	}
};