var listTags = {},
	viewId;

if(__isIE8orLess)
	Greencode.util.loadScript(Greencode.CONTEXT_PATH + "/jscript/greencode/sizzle.js", false);

var startInterval = function() {
	setTimeout(function() {
		Bootstrap.init(document);
		if(startInterval)
			startInterval();
	}, 15);
}();

Greencode.crossbrowser.registerEvent.call(window, 'load', function() {
	delete window.startInterval;

	if(window.location.hash.indexOf('#!') === 0) {
		window.location.href = Greencode.CONTEXT_PATH + '/' + window.location.hash.substring(2);
		return;
	}
	
	Bootstrap.init();

	Greencode.crossbrowser.registerEvent.call(window, 'popstate', function(e) {
		if(e.state != null && e.state.selector != null) {
			var o = e.state.selector == 'body' ? document.body : Greencode.crossbrowser.querySelector.call(document.body, e.state.selector);
			Greencode.customMethod.empty.call(o);
			
			var tags = listTags[window.location.href];
			for(var i in tags)
				o.appendChild(tags[i]);
		}
	});
	
	var _data = {mainElement: document.body}
	Greencode.executeEvent('pageLoad', _data);
});