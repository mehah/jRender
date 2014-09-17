var lastUniqueId = 0,
	listTags = {},
	viewId,
	_intervalId = null;

if(__isIE8orLess)
	Greencode.util.loadScript(CONTEXT_PATH+"/jscript/greencode/sizzle.js", false);

var startInterval = function() {
	_intervalId = setTimeout(function() {
		Bootstrap.init(document);		
		startInterval();
	}, 15);
};

startInterval();

Greencode.crossbrowser.registerEvent.call(window, 'load', function() {
	clearInterval(_intervalId);
	
	if(window.location.hash.indexOf('#!') === 0) {
		window.location.href = CONTEXT_PATH+'/'+window.location.hash.substring(2);
		return;
	}
	
	Bootstrap.init();
	
	Greencode.crossbrowser.registerEvent.call(window, 'popstate', function (e) {
    	if(e.state != null && e.state.content != null) {
    		var o = Greencode.crossbrowser.querySelector.call(document.body, e.state.selector);
    		o.innerHTML = e.state.content;
    		
    		var tags = listTags[window.location.href];    		
    		for (var i = -1; ++i < o.children.length;) {
    			var child = o.children[i];
    			child.parentNode.replaceChild(tags[child.getAttribute('_uid')], child);
			}

    	}
    });
});