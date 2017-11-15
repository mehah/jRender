EventTarget.prototype.registerEvent = function(eventName, func, data) {
	if (JRender.events[eventName] != null)
		JRender.registerEvent(eventName, func, this);
	else if (JRender.customEvent[eventName] != null)
		JRender.customEvent[eventName].add.call(this, func, data);
	else if (this.addEventListener)
		this.addEventListener(eventName, func, false);
	else
		this.attachEvent('on' + eventName, func);
};

EventTarget.prototype.removeEvent = function(eventName, func, oficialRemove) {
	if (!oficialRemove && JRender.customEvent[eventName] != null)
		JRender.customEvent[eventName].remove.call(this, func);
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

	return this.innerText || this.textContent;
};

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
				var oldLeft = this.style.left,
					oldRsLeft = this.runtimeStyle.left;
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

Element.prototype.hasClass = function(className) {
	return this.className.match(new RegExp('(\\s|^)' + className + '(\\s|$)'));
};

Element.prototype.addClass = function(className) {
	if (!this.hasClass(className))
		this.className += " " + className;
	return this;
};

Element.prototype.removeClass = function(className) {
	if (this.hasClass(className))
		this.className = this.className.replace(new RegExp('(\\s|^)' + className + '(\\s|$)'), ' ');
	return this;
};

Element.prototype.replaceWith = function(e) {
	this.parentNode.replaceChild(e, this);
	return this;
};

Element.prototype.replaceWithPageURL = function(url, viewId, cid) {
	var This = this,
		request = new Request(JRender.getRealURLPath(url), JRender.EVENT_REQUEST_TYPE, JRender.isRequestSingleton()),
		first = false;

	request.setMethodRequest("POST");
	request.setCometType(Request.STREAMING);
	request.reconnect(false);
	request.jsonContentType(false);

	var f = function(data) {
		if (data) {
			if (!first) {
				first = true;
				This.empty();
			}
			This.insertAdjacentHTML('beforeEnd', data);
			JRender.core.processJSON(this, This);
		}
	};

	request.send({
		cid: cid,
		viewId: viewId
	}, f, function(data) {
		f(data);
		var _data = {
			mainElement: This
		};
		JRender.executeEvent('pageLoad', _data);
	});

	request = null;
	return this;
};

Element.prototype.resetForm = function() {
	try {
		this.reset();
	} catch (e) {
		this.reset.click();
	}
	return this;
};

Element.prototype.getElementOrCreateByTagName = function(tagName) {
	var list = this.querySelector(tagName);
	return list.length == 0 ? this.appendChild(document.createElement(tagName)) : list[0];
};

Element.prototype.getParentByTagName = function(tagName) {
	var parent = this;
	while ((parent = parent.parentNode) && parent != document) {
		if (parent.tagName == tagName.toUpperCase())
			return parent;
	}

	return null;
};

Element.prototype.fillForm = function(a) {
	for (var i in a) {
		var o = a[i];

		var elements = this.querySelectorAll('[name="' + o.name + '"]');
		if (elements.length == 0)
			continue;

		if (!JRender.jQuery.isArray(o.values))
			o.values = [o.values];

		var first = elements[0];

		var container = first.getParentByTagName('container');
		if (container != null && container != this)
			continue;

		if (first.tagName == 'TEXTAREA')
			first.value = o.values;
		else if (first.tagName == 'SELECT') {
			for (var i2 = -1; ++i2 < first.options.length;) {
				var option = first.options[i2];
				if (first.multiple) {
					for (var i3 in o.values) {
						if (option.value == o.values[i3]) {
							option.selected = true;
							break;
						}
					}
				} else if (option.value == o.values[0]) {
					option.selected = true;
					break;
				}
			}
		} else if (first.tagName == 'INPUT') {
			var isRadio = first.type == 'radio';
			if (isRadio || first.type == 'checkbox') {
				for (var i2 = -1; ++i2 < elements.length;) {
					var e = elements[i2];
					container = e.getParentByTagName('container');
					if (container != null && container != this)
						continue;

					var achou = false;
					for (var i3 in o.values) {
						if (e.value == o.values[i3] + "") {
							e.checked = true;
							if (isRadio) {
								achou = true;
								break;
							}
						}

						if (achou)
							break;
					}
				}
			} else
				first.value = o.values[0];
		}
	}

	return this;
};

Element.prototype.getClosestChildrenByTagName = function(tagName, attrFilter) {
	var list = new Array();
	var search = function(e) {
		for (var i = -1; ++i < e.children.length;) {
			var child = e.children[i];
			if (child.tagName == tagName.toUpperCase()) {
				var push = true;
				if (attrFilter != null) {
					for (var attr in attrFilter) {
						var value = attrFilter[attr];
						if (!((value === true || value === false) && child.hasAttribute(attr) == value || child.getAttribute(attr) == value)) {
							push = false;
							break;
						}
					}
				}
				if (push) {
					list.push(child);
					continue;
				}
			}

			search(child);
		}
	};
	search(this);
	return list;
};

Element.prototype.getAllDataElements = function(param) {
	if (!param)
		param = {};
	for (var i = -1; ++i < this.children.length;) {
		var tag = this.children[i],
			lastParam = param;
		if (['INPUT', 'SELECT', 'TEXTAREA'].indexOf(tag.tagName) > -1) {
			if (tag.tagName === 'INPUT' && tag.type === 'radio' || tag.type === 'checkbox') {
				var list = param[tag.name];
				if (!list) {
					list = new Array();
					param[tag.name] = list;
				}
				list.push(tag);
			} else
				param[tag.name] = tag;
		} else if (tag.tagName === 'CONTAINER') {
			var p = param[tag.getAttribute('name')];
			if (!p) {
				p = new Array();
				param[tag.getAttribute('name')] = p;
			}
			p.push(lastParam = {
				__container: tag
			});
		}
		tag.getAllDataElements(lastParam);
	}

	return param;
};

Node.prototype.empty = function() {
	for (var ii = -1; ++ii < this.childNodes.length;) {
		var c = this.childNodes[ii];
		c.parentNode.removeChild(c);
		--ii;
	}
	return this;
};

Node.prototype.prependChild = function(node) {
	this.insertBefore(node, this.firstChild);
	return node;
};

Node.prototype.appendChildAfter = function(node) {
	if (this.parentNode) {
		this.parentNode.insertBefore(node, this.nextSibling);
	}
	return node;
};

Node.prototype.appendChildBefore = function(node) {
	if (this.parentNode) {
		this.parentNode.insertBefore(node, this);
	}
	return node;
};

Element.prototype.selectOptionByValue = function(v) {
	var e = this.querySelector('option[value="' + v + '"]');
	if (e != null)
		e.selected = true;
	return this;
};

{
	var _insertAdjacentHTML = Element.prototype.insertAdjacentHTML;
	Element.prototype.insertAdjacentHTML = function(position, text, args) {
		if(args != null) {
			for(var i = -1; ++i < args.length;) {				
				var arg = args[i];
				text = text.replace("\{"+i+"\}", arg);
			}
		}
		
		_insertAdjacentHTML.call(this, position, text);
	};
}