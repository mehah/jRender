var lastUniqueId = 0,
	listTags = {},
	viewId,
	_intervalId = null;

if(__isIE8orLess)
	Greencode.util.loadScript(CONTEXT_PATH + "/jscript/greencode/sizzle.js", false);

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
		window.location.href = CONTEXT_PATH + '/' + window.location.hash.substring(2);
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

Greencode.registerEvent('pageLoad', function(data) {
	var listRepeat = Greencode.crossbrowser.querySelectorAll.call(data.mainElement, "repeat");
	for(var i = -1; ++i < listRepeat.length;) {
		var e = listRepeat[i];
		var nameRepeat = e.getAttribute('name');
		var list = Greencode.crossbrowser.querySelectorAll.call(e, "input, select, textarea, datalist");
		for(var b = -1; ++b < list.length;) {
			var input = list[b];
			var name = input.getAttribute('name');
			if(name) {
				var pos = name.lastIndexOf('>');
				if(pos > 0)
					name = name.substring(0, pos+1)+nameRepeat+name.substring(pos);
				else
					name = nameRepeat+">"+name;
				input.setAttribute('name', name);
			}
		}
	}
});