var __LAST_IFRAME_ID = 0;
var __REGEXP = new RegExp("([^?=&]+)(=([^&]*))?", "g");
var IframeHttpRequest = function() {
	var o = this,
		intervalId = null,
		aborted = false,
		frame = null,
		head = new Array(),
		fContent = null;

	this.UNSENT = 0;
	this.OPENED = 1;
	this.HEADERS_RECEIVED = 2;
	this.LOADING = 3;
	this.DONE = 4;

	this.url = "";
	this.responseText = "";
	this.readyState = this.UNSENT;
	this.status = 200;
	this.methodRequest = "GET";

	var appendInput = function(form, p) {
		var input = document.createElement("input");
		input.setAttribute('type', 'hidden');
		input.setAttribute('name', p.key);
		input.setAttribute('value', p.value);
		form.appendChild(input);
	};

	var done = function() {
		if (frame != null) {
			try {
				document.body.removeChild(frame);
			} catch (e) {}
			frame = null;
		}
		clearInterval(intervalId);
	};

	this.open = function(method, url) {
		this.url = url;
		this.readyState = this.OPENED;
		this.methodRequest = method;
	};

	this.setRequestHeader = function(key, value) {
		head.push({
			key: key,
			value: value
		});
	};

	var append = function(form, inputs, returnPosition) {
		for (var i in inputs) {
			var o = inputs[i],
				e = o.e;

			if (!o.appended) {
				o.prev = e.previousSibling;
				o.next = e.nextSibling;
				o.parent = e.parentNode;
			}

			if (!returnPosition) {
				form.appendChild(e);
				if (o.varName != null)
					e.setAttribute('name', o.varName);
				o.appended = true;
			} else {
				if (o.prev != null) {
					if (o.prev.nextSibling != null)
						o.parent.insertBefore(e, o.prev.nextSibling);
					else
						o.parent.appendChild(e);
				} else if (o.next != null)
					o.next.parentNode.insertBefore(e, o.next);
				else
					o.parent.appendChild(e);

				if (o.varName != null)
					e.setAttribute('name', o.name);
			}
		}
	};

	this.send = function(data) {
		aborted = false;

		++__LAST_IFRAME_ID;

		var name = 'FORM_AJAX_IFRAME_' + __LAST_IFRAME_ID;

		try {
			/* FIX IE7 PROBLEM: iframe opening new window */
			frame = document.createElement('<iframe name="' + name + '" />');
		} catch (e) {
			frame = document.createElement("iframe");
			frame.setAttribute('name', name);
		}

		frame.style.display = 'none';
		document.body.appendChild(frame);

		var form = document.createElement("form");
		form.style.display = 'none';
		form.setAttribute("action", this.url);
		form.setAttribute("target", name);

		document.body.appendChild(form);

		this.url.replace(__REGEXP, function($0, $1, $2, $3) {
			data[$1] = $3;
		});

		var files = new Array();

		/* var param = JRender.jQuery.paramObject(data); */
		for (var i in data) {
			var p = {
				key: i,
				value: data[i]
			};

			if (p.key.toLowerCase() == "submit")
				p.key = '_submit';

			if (JRender.jQuery.isArray(p.value)) {
				for (var i2 in p.value)
					appendInput(form, {
						key: p.key + "[]",
						value: p.value[i2]
					});
			} else {
				if (p.value && p.value.nodeType && p.value.nodeType === 1 && p.value.tagName == 'INPUT' && p.value.type == 'file')
					files.push({
						prev: null,
						next: null,
						parent: null,
						e: p.value,
						appended: false,
						varName: data.varName,
						name: p.value.name
					});
				else
					appendInput(form, p);
			}
		}
		appendInput(form, {
			key: "isIframe",
			value: true
		});

		if (files.length > 0) {
			append(form, files, false);
			form.setAttribute("method", "POST");
			form.setAttribute("enctype", "multipart/form-data");
			form.setAttribute("encoding", "multipart/form-data");
		} else
			form.setAttribute("method", this.methodRequest);

		form.submit();
		if (files.length > 0)
			append(form, files, true);

		form.parentNode.removeChild(form);

		intervalId = setInterval(function() {
			try {
				fContent = frame.content();

				if (fContent.body == null || fContent.readyState === "uninitialized" || fContent.URL === 'about:blank')
					return;

				o.responseText = fContent.body.innerHTML;

				if (o.responseText != null) {
					if (fContent.readyState === "loading")
						o.readyState = o.HEADERS_RECEIVED;
					else if (fContent.readyState === "interactive")
						o.readyState = o.LOADING;
					else if (fContent.readyState === "complete") {
						o.readyState = o.DONE;
						done();
					}

					if (o.onreadystatechange != null)
						o.onreadystatechange.call(o);
				}
			} catch (e) {
				if (typeof console != 'undefined')
					console.error(e.message, e);

				o.abort();
			}
		}, 15);
	};

	this.clear = function() {
		fContent.body.innerHTML = "";
	};

	this.abort = function() {
		this.status = 200;
		this.readyState = this.UNSENT;
		aborted = true;
		done();
	};
};