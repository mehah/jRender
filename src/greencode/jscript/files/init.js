if(__isIE8orLess)
	Greencode.util.loadScript(Greencode.CONTEXT_PATH + "/jscript/greencode/sizzle.js", false);

Greencode.crossbrowser.registerEvent.call(window, 'load', function() {
	if(window.location.hash.indexOf('#!') === 0) {
		window.location.href = Greencode.CONTEXT_PATH + '/' + window.location.hash.substring(2);
		return;
	}
	
	window.document.write = function(node){
		var temp = document.createElement('div');
		temp.innerHTML = node;
	    var elem;
		while(elem = temp.firstChild) {
			if(elem.tagName === 'SCRIPT') {
				temp.removeChild(elem);
				var script = document.createElement('script'), head = document.getElementsByTagName("head")[0];
				script.setAttribute("type", elem.type ? elem.type : "text/javascript");
				script.setAttribute("src", elem.src);
		        
				var done = false;
				script.onload = script.onreadystatechange = function() {
					if (!done && (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
						done = true;
						Greencode.executeEvent('scriptLoad', {mainElement: document.body});

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
				window.document.body.appendChild(elem);
			elem = null;
		}
		
		temp = null;
	};
	
	Bootstrap.init();

	Greencode.crossbrowser.registerEvent.call(window, 'popstate', function(e) {
		if(e.state != null && e.state.selector != null) {
			Greencode.executeEvent('beforePopstate');
			var o = e.state.selector == 'body' ? document.body : Greencode.crossbrowser.querySelector.call(document.body, e.state.selector);
			Greencode.customMethod.empty.call(o);
			var tags = Greencode.cache.tags[window.location.href];
			for(var i in tags)
				o.appendChild(tags[i]);
			Greencode.executeEvent('afterPopstate');
		}
	});
	
	var _data = {mainElement: document.body};
	Greencode.executeEvent('pageLoad', _data);
});