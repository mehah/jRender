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
	
	{ /* Bloco para as tags customizadas antes de processar o JSON */		
		var listRepeat = Greencode.crossbrowser.querySelectorAll.call(document.body, "container");
		for(var i = -1; ++i < listRepeat.length;) {
			var e = listRepeat[i], nameRepeat = e.getAttribute('name');
			
			e.original = e;
			e.clones = new Array();
			
			e.firstClone = function() {
				return this.clones[0];
			};
			
			e.lastClone = function() {
				return this.clones[this.clones.length-1]
			};
			
			e.repeat = function(original) {				
				var clone = (original ? this.original : this).cloneNode(true);
				
				clone.clones = this.clones;				
				clone.firstClone = this.firstClone;				
				clone.lastClone = this.lastClone;
				clone.repeat = this.repeat;
				clone.original = this.original;

				var elementToInsertAfter = (this.lastClone() || this);
				elementToInsertAfter.parentNode.insertBefore(clone, elementToInsertAfter.nextSibling);
				
				this.clones.push(clone);
				
				var containers = Greencode.crossbrowser.querySelectorAll.call(clone, "container");
				for(var i = -1; ++i < containers.length;) {
					var container = containers[i];
					container.clones = new Array();				
					container.firstClone = this.firstClone;				
					container.lastClone = this.lastClone;
					container.repeat = this.repeat;
					container.original = container;
					
					var old = container;
					container.original = old;
					
					container = container.repeat(original);
					old.parentNode.removeChild(old);
				}
				
				if(this.onRepeat)
					this.onRepeat.call(this, clone);
				
				Greencode.executeEvent('containerCloned', {mainElement: clone});
				
				return clone;
			};
		}
	}
	
	Bootstrap.init();
	
	{ /* Bloco para as tags customizadas depois de processar o JSON */	
		for(var i = -1; ++i < listRepeat.length;) {
			var original = listRepeat[i];
			if(i > 0 && listRepeat[i-1] == Greencode.customMethod.getParentByTagName.call(original, 'container'))
				break;
			
			var e = original.repeat(true), repeat = e.getAttribute('repeat') || 0;
			
			original.parentNode.removeChild(original);
			
			if(repeat) {
				e.removeAttribute('repeat');
				for(; --repeat >= 0;)
					e.repeat(true);
			}
		}		
	}

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