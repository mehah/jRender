Greencode.tags = {
	process : function(ref) {
		var listRepeat = Greencode.customMethod.getClosestChildrenByTagName.call(ref, "container", {uid : false});
		for (var i = -1; ++i < listRepeat.length;) {
			var e = listRepeat[i], nameRepeat = e.getAttribute('name');

			e.original = e;
			e.clones = new Array();

			e.firstClone = function() {
				return this.clones[0];
			};

			e.lastClone = function() {
				return this.clones[this.clones.length - 1]
			};

			e.remove = function() {
				var index = this.clones.indexOf(this);
				if (index > -1) {
					this.clones.splice(index, 1);
					this.parentNode.removeChild(this);
					this.form = null;
				}
			};

			e.form = Greencode.customMethod.getParentByTagName.call(e, "form");

			e.repeat = function(useOriginal, notExecuteEvent) {
				var clone = (useOriginal ? this.original : this).cloneNode(true);

				clone.clones = this.clones;
				clone.firstClone = this.firstClone;
				clone.lastClone = this.lastClone;
				clone.repeat = this.repeat;
				clone.original = this.original;
				clone.remove = this.remove;

				var uid = Greencode.cache.generateUID();
				clone.setAttribute('uid', uid)
				Greencode.cache.register(uid, clone);

				var elementToInsertAfter = (this.lastClone() || this);
				elementToInsertAfter.parentNode.insertBefore(clone, elementToInsertAfter.nextSibling);
				clone.form = Greencode.customMethod.getParentByTagName.call(clone, "form");

				this.clones.push(clone);

				var containers = Greencode.customMethod.getClosestChildrenByTagName.call(clone, "container");
				for (var i = -1; ++i < containers.length;) {
					var container = containers[i];
					container.clones = new Array();
					container.firstClone = this.firstClone;
					container.lastClone = this.lastClone;
					container.repeat = this.repeat;
					container.remove = this.remove;
					container.original = container;

					var old = container;
					container.original = old;

					var repeat = container.getAttribute('repeat');
					if (repeat) {
						container.removeAttribute('repeat');
						for (; --repeat >= 0;)
							container.repeat(useOriginal, true);
					} else
						container.repeat(useOriginal, true);

					old.parentNode.removeChild(old);
				}

				if (this.onRepeat)
					this.onRepeat.call(this, clone);

				if (notExecuteEvent !== true)
					Greencode.executeEvent('containerCloned', {__containerUID : uid, mainElement : clone}, [ window, clone.original, clone, this ]);

				return clone;
			};
		}

		for (var i = -1; ++i < listRepeat.length;) {
			var original = listRepeat[i];

			var e = original.repeat(true, true), repeat = e.getAttribute('repeat') || 0;

			original.parentNode.removeChild(original);

			if (repeat) {
				e.removeAttribute('repeat');
				for (; --repeat >= 0;)
					e.repeat(true, true);
			}
		}

		Greencode.tags.buttons(ref);
	},
	buttons : function(mainElement) {
		var elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'input[type="redirect"]:not([swept]), button[type="redirect"]:not([swept])');

		for (var i = -1; ++i < elements.length;) {
			var element = elements[i];
			element.setAttribute('swept', null);

			Greencode.crossbrowser.registerEvent.call(element, 'click', function() {
				this.type = "button";

				var action = this.getAttribute('action');

				if (action != null)
					window.location.href = action;
			});
		}

		elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, '[appendTo]:not([swept])');

		for (var i = -1; ++i < elements.length;) {
			var element = elements[i], appendTo = element.getAttribute('appendTo').toLowerCase(), o = appendTo == "body" ? document.body : Greencode.crossbrowser.querySelector.call(mainElement, appendTo);

			element.setAttribute('swept', 'swept');

			if (o != null) {
				Greencode.crossbrowser.registerEvent.call(element, 'click', function(e) {
					e.preventDefault();

					var appendTo = this.getAttribute('appendTo'),
						empty = Greencode.crossbrowser.hasAttribute.call(this, 'empty'),
						changeURL = Greencode.crossbrowser.hasAttribute.call(this, 'changeURL'),
						keepViewId = Greencode.crossbrowser.hasAttribute.call(this, 'keepViewId'),
						href = this.getAttribute('href'),
						data = {__contentIsHtml : true},
						cometReceber = new Comet(this.getAttribute('href'));

					if (keepViewId)
						data.viewId = viewId;

					cometReceber.setMethodRequest('GET');
					cometReceber.setCometType(Comet().STREAMING);
					cometReceber.reconnect(false);
					cometReceber.setAsync(true);
					cometReceber.jsonContentType(false);
					//cometReceber.forceConnectType(Comet().IframeHttpRequest);

					var _data = {
						mainElement : o,
						target : this,
						appendToSelector : appendTo,
						appendToElement : o,
						empty : empty,
						changeURL : changeURL,
						keepViewId : keepViewId,
						href : href
					};

					if (Greencode.executeEvent('beforePageRequest', _data) !== false) {					
						var dataComplete = "";					
						cometReceber.send(data, function(data) {
							dataComplete += data;
						}, function(data) {
							var tags = new Array();
	
							delete listTags[window.location.href];
							listTags[window.location.href] = tags;
	
							for (var ii = -1; ++ii < o.childNodes.length;)
								tags.push(o.childNodes[ii]);
	
							if (empty)
								Greencode.customMethod.empty.call(o);
	
							dataComplete += data;
							o.insertAdjacentHTML('beforeEnd', dataComplete);
							var scripts = Greencode.crossbrowser.querySelectorAll.call(o, 'script');
							for (var s = -1; ++s < scripts.length;) {
								var scriptElement = scripts[s];
								if(scriptElement.src) {
									var script = document.createElement('script'), head = document.getElementsByTagName("head")[0];
									script.setAttribute("type", scriptElement.type ? scriptElement.type : "text/javascript");
									script.setAttribute("src", scriptElement.src);
	
									var done = false;
									script.onload = script.onreadystatechange = function() {
										if (!done && (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
											done = true;
											Greencode.executeEvent('scriptLoad', _data);
	
											script.onload = script.onreadystatechange = null;
											if (head && script.parentNode) {
												head.removeChild(script);
											}
											script = null;
										}
									};
									
							        head.appendChild(script);
								}
								else
									window.eval(Greencode.crossbrowser.text.call(scriptElement));
							}
	
							Bootstrap.init(o);
	
							if (changeURL) {
								if (history.pushState == null)
									window.location.hash = "#!" + href;
								else {
									history.replaceState({selector : appendTo}, null, location.href);
									history.pushState({selector : appendTo}, null, href);
	
									tags = new Array();
									delete listTags[window.location.href];
									listTags[window.location.href] = tags;
	
									for (var ii = -1; ++ii < o.childNodes.length;)
										tags.push(o.childNodes[ii]);
								}
							}
	
							Greencode.executeEvent('afterPageRequest', _data);
							Greencode.executeEvent('pageLoad', _data);
						});
						cometReceber = null;
					}					

					return false;
				});
			}
		}

		elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'input[type="ajax"]:not([swept]), button[type="ajax"]:not([swept])');

		for (var i = -1; ++i < elements.length;) {
			var element = elements[i];
			element.setAttribute('swept', null);

			this.type = "button";

			Greencode.crossbrowser.registerEvent.call(element, 'click', function() {
				var data = {}, form = this.form, _es = Greencode.crossbrowser.querySelectorAll.call(form, 'input, textarea, select'), cometReceber = new Comet(this.getAttribute('action'));

				if (_es != null) {
					for ( var e in _es)
						data[this.id || this.name] = this.value;
				}

				cometReceber.setMethodRequest(this.getAttribute('method') != null && this.getAttribute('method').toUpperCase() === 'POST' ? 'POST' : 'GET');
				cometReceber.setCometType(Comet().LONG_POLLING);
				cometReceber.reconnect(false);
				cometReceber.setAsync(true);
				cometReceber.forceConnectType(Comet().IframeHttpRequest);

				cometReceber.send(data, function(data) {
				}, function(data) {
					if (element.getAttribute('appendTo') != null) {
						var o = Greencode.crossbrowser.querySelector.call(mainElement, element.getAttribute('appendTo'));

						if (element.getAttribute('empty') != null && element.getAttribute('empty').toLowerCase() === 'true') {
							for (var ii = -1; ++ii < element.children.length;) {
								var c = element.children[ii];
								c.parentNode.removeChild(c);
							}
						}

						element.insertAdjacentHTML('beforeEnd', data);
						Bootstrap.init(element);
					} else if (data != null && data != "")
						Bootstrap.init(form, JSON.parse(data));
				});

				cometReceber = null;
			});
		}

		elements = Greencode.crossbrowser.querySelectorAll.call(mainElement, 'input[type="submit"]:not([swept]), button[type="submit"]:not([swept])');

		for (var i = -1; ++i < elements.length;) {
			var element = elements[i];
			element.setAttribute('swept', null);

			if (element.getAttribute('action') != null)
				element.form.setAttribute('action', element.getAttribute('action'));
		}
	}
};