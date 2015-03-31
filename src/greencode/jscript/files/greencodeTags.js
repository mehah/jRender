Greencode.tags = {
	process: function() {
		if(!document.body)
			return;
		
		var listRepeat = Greencode.customMethod.getClosestChildrenByTagName.call(document.body, "container", {uid: false});
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
			
			e.remove = function() {			
				var index = this.clones.indexOf(this);
				if(index > -1) {
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
				for(var i = -1; ++i < containers.length;) {
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
					if(repeat) {
						container.removeAttribute('repeat');
						for(; --repeat >= 0;)
							container.repeat(useOriginal, true);
					}else
						container.repeat(useOriginal, true);
					
					old.parentNode.removeChild(old);
				}
				
				if(this.onRepeat)
					this.onRepeat.call(this, clone);
				
				if(notExecuteEvent !== true)
					Greencode.executeEvent('containerCloned', {__containerUID: uid, mainElement: clone}, [window, clone.original, clone, this]);
				
				return clone;
			};
		}
		
		for(var i = -1; ++i < listRepeat.length;) {
			var original = listRepeat[i];
			
			var e = original.repeat(true, true), repeat = e.getAttribute('repeat') || 0;
			
			original.parentNode.removeChild(original);
			
			if(repeat) {
				e.removeAttribute('repeat');
				for(; --repeat >= 0;)
					e.repeat(true, true);
			}
		}
	}
};