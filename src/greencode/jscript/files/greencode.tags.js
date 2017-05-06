Greencode.tags = {
	process: function(ref) {
		var listRepeat = ref.getClosestChildrenByTagName("container", {
			uid: false
		});
		for (var i = -1; ++i < listRepeat.length;) {
			var e = listRepeat[i],
				nameRepeat = e.getAttribute('name');

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

			e.form = e.getParentByTagName("form");

			e.repeat = function(useOriginal, notExecuteEvent) {
				var clone = (useOriginal ? this.original : this).cloneNode(true);

				clone.clones = this.clones;
				clone.firstClone = this.firstClone;
				clone.lastClone = this.lastClone;
				clone.repeat = this.repeat;
				clone.original = this.original;
				clone.remove = this.remove;

				var uid = Greencode.cache.generateUID();
				clone.setAttribute('uid', uid);
				Greencode.cache.register(uid, clone);

				var elementToInsertAfter = (this.lastClone() || this);
				elementToInsertAfter.parentNode.insertBefore(clone, elementToInsertAfter.nextSibling);
				clone.form = clone.getParentByTagName("form");

				this.clones.push(clone);

				var containers = clone.getClosestChildrenByTagName("container");
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
					Greencode.executeEvent('containerCloned', {
						__containerUID: uid,
						mainElement: clone
					}, [window, clone.original, clone, this]);

				return clone;
			};
		}

		for (var i = -1; ++i < listRepeat.length;) {
			var original = listRepeat[i];

			var e = original.repeat(true, true),
				repeat = e.getAttribute('repeat') || 0;

			original.parentNode.removeChild(original);

			if (repeat) {
				e.removeAttribute('repeat');
				for (; --repeat >= 0;)
					e.repeat(true, true);
			}
		}

		Greencode.tags.buttons(ref);
	},
	buttons: function(mainElement) {
		var elements = mainElement.querySelectorAll('input[type="redirect"]:not([swept]), button[type="redirect"]:not([swept])');

		for (var i = -1; ++i < elements.length;) {
			var element = elements[i];
			element.setAttribute('swept', null);

			element.registerEvent('click', function() {
				this.type = "button";

				var action = this.getAttribute('action');

				if (action != null)
					window.location.href = action;
			});
		}

		elements = mainElement.querySelectorAll('[appendTo]:not([swept])');

		for (var i = -1; ++i < elements.length;) {
			var element = elements[i],
				appendTo = element.getAttribute('appendTo').toLowerCase(),
				o = appendTo == "body" ? document.body : mainElement.querySelector(appendTo);

			element.setAttribute('swept', 'swept');

			if (o == null) {
				o = document.body.querySelector(appendTo);
			}

			if (o != null) {
				element.registerEvent('click', function(e) {
					e.preventDefault();

					var hrefOriginal = this.getAttribute('href');
					var appendTo = this.getAttribute('appendTo'),
						empty = this.hasAttribute('empty'),
						changeURL = this.hasAttribute('changeURL'),
						keepViewId = this.hasAttribute('keepViewId'),
						href = Greencode.getRealURLPath(hrefOriginal),
						data = {
							__contentIsHtml: true
						};

					if (keepViewId)
						data.viewId = viewId;

					request = new Request(href, Greencode.EVENT_REQUEST_TYPE, Greencode.isRequestSingleton());

					request.setMethodRequest('GET');
					request.setCometType(Request.STREAMING);
					request.reconnect(false);
					request.jsonContentType(false);

					var _data = {
						mainElement: o,
						target: this,
						appendToSelector: appendTo,
						appendToElement: o,
						empty: empty,
						changeURL: changeURL,
						keepViewId: keepViewId,
						href: href
					};

					if (Greencode.executeEvent('beforePageRequest', _data) !== false) {
						var dataComplete = "";

						request.send(data, function(data) {
							dataComplete += data;
						}, function(data) {
							var tags = new Array();

							delete Greencode.cache.tags[window.location.href];
							Greencode.cache.tags[window.location.href] = tags;

							for (var ii = -1; ++ii < o.childNodes.length;)
								tags.push(o.childNodes[ii]);

							if (empty)
								o.empty();

							dataComplete += data;

							o.insertAdjacentHTML('beforeEnd', dataComplete);
							var scripts = o.querySelectorAll('script');
							for (var s = -1; ++s < scripts.length;) {
								var scriptElement = scripts[s];
								if (scriptElement.src) {
									var script = document.createElement('script'),
										head = document.getElementsByTagName("head")[0];
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
								} else
									window.eval(scriptElement.childTextConent());
							}

							Greencode.core.processJSON(this, o);

							if (changeURL) {
								if (history.pushState == null)
									window.location.hash = "#!" + href;
								else {
									history.replaceState({
										selector: appendTo
									}, null, location.href);
									history.pushState({
										selector: appendTo
									}, null, hrefOriginal);

									tags = new Array();
									delete Greencode.cache.tags[window.location.href];
									Greencode.cache.tags[window.location.href] = tags;

									for (var ii = -1; ++ii < o.childNodes.length;)
										tags.push(o.childNodes[ii]);
								}
							}

							Greencode.executeEvent('afterPageRequest', _data);
							Greencode.executeEvent('pageLoad', _data);
						});
						request = null;
					}

					return false;
				});
			}
		}

		elements = mainElement.querySelectorAll('input[type="ajax"]:not([swept]), button[type="ajax"]:not([swept])');

		for (var i = -1; ++i < elements.length;) {
			var element = elements[i];
			element.setAttribute('swept', null);

			this.type = "button";

			element.registerEvent('click', function() {
				var data = {},
					form = this.form,
					_es = form.querySelectorAll('input, textarea, select'),
					request = new Request(this.getAttribute('action'), Greencode.EVENT_REQUEST_TYPE, Greencode
						.isRequestSingleton());

				if (_es != null) {
					for (var e in _es)
						data[this.id || this.name] = this.value;
				}

				request.setMethodRequest(this.getAttribute('method') != null && this.getAttribute('method').toUpperCase() === 'POST' ? 'POST' : 'GET');
				request.setCometType(Request.LONG_POLLING);
				request.reconnect(false);

				request.send(data, function(data) {}, function(data) {
					if (element.getAttribute('appendTo') != null) {
						var o = mainElement.querySelector(element.getAttribute('appendTo'));

						if (element.getAttribute('empty') != null && element.getAttribute('empty').toLowerCase() === 'true') {
							for (var ii = -1; ++ii < element.children.length;) {
								var c = element.children[ii];
								c.parentNode.removeChild(c);
							}
						}

						element.insertAdjacentHTML('beforeEnd', data);
						Greencode.core.processJSON(this, element);
					} else if (data != null && data != "")
						Greencode.core.processJSON(this, form, JSON.parse(data));
				});

				request = null;
			});
		}

		elements = mainElement.querySelectorAll('input[type="submit"]:not([swept]), button[type="submit"]:not([swept])');

		for (var i = -1; ++i < elements.length;) {
			var element = elements[i];
			element.setAttribute('swept', null);

			if (element.getAttribute('action') != null)
				element.form.setAttribute('action', element.getAttribute('action'));
		}
	}
};