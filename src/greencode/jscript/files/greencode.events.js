Greencode.events = {
	afterEvent: [],
	beforeEvent: [],
	beforePageRequest: [],
	afterPageRequest: [],
	init: [],
	pageLoad: [],
	containerCloned: [],
	beforePopstate: [],
	afterPopstate: [],
	scriptLoad: [],
};

Greencode.registerEvent = function(name, callback, ref) {
	Greencode.events[name].push({ref: ref || window, callback: callback});
};

Greencode.executeEvent = function(name, data, ref) {
	var obj = Greencode.events[name], ref = ref || window;
	if(obj && obj.length > 0) {
		for(var i in obj) {
			var o = obj[i];
			if((ref instanceof Array && ref.indexOf(o.ref) > -1 || o.ref == ref) && o.callback(data) === false)
				return false;
		}
	}
	
	return true;
};

Greencode.customEvent = {};

Greencode.customEvent.scrollreachpercent = {
	add: function(callback, data) {
		var handleObj = {
			lastCcrollPercent: ((this.scrollTop + document.body.scrollTop) / (this.scrollHeight - this.clientHeight) * 100),
			data: {}
		};
		
		handleObj.data.percent = parseInt(data[0]);
		handleObj.data.beginDown = handleObj.lastCcrollPercent > handleObj.data.percent;
		handleObj.data.beginUp = handleObj.lastCcrollPercent < handleObj.data.percent;
		
		this.registerEvent('scroll', function(event) {
			Greencode.customEvent.scrollreachpercent.handle.call(this, callback, event, handleObj);
		});
	},
	remove: function() {
		this.removeEvent('scroll', Greencode.customEvent.scrollreachpercent.handle, true);
	},
	handle: function(callback, event, handleObj) {
		var scrollPercent=100*this.scrollTop/this.scrollHeight/(1-this.clientHeight/this.scrollHeight);
    	scrollPercent = scrollPercent.toFixed(2);
    	
		if(handleObj.data.beginDown && scrollPercent <= handleObj.data.percent ||
		   handleObj.data.beginUp   && scrollPercent >= handleObj.data.percent
		) {
			handleObj.data.beginDown = !handleObj.data.beginDown;
			handleObj.data.beginUp = !handleObj.data.beginUp;
			callback.call(this, event, handleObj);
		}
    }
};

Greencode.customEvent.scrollreachtop = {
	add: function(callback) {
		this.registerEvent('scroll', function(event) {
			Greencode.customEvent.scrollreachtop.handle.call(this, callback, event);				
		});
	},
	remove: function() {
		this.removeEvent('scroll', Greencode.customEvent.scrollreachtop.handle, true);
	},
	handle: function(callback, event) {
		if(0 == (100*this.scrollTop/this.scrollHeight/(1-this.clientHeight/this.scrollHeight)).toFixed(0))
			callback.call(this, event, handleObj);
    }
};

Greencode.customEvent.scrollreachbottom = {
	add: function(callback) {
		this.registerEvent('scroll', function(event) {
			Greencode.customEvent.scrollreachbottom.handle.call(this, callback, event);				
		});
	},
	remove: function() {
		this.removeEvent('scroll', Greencode.customEvent.scrollreachbottom.handle, true);
	},
	handle: function(callback, event) {
		if(100 == (100*this.scrollTop/this.scrollHeight/(1-this.clientHeight/this.scrollHeight)).toFixed(0))
			callback.call(this, event);
    }
};

Greencode.customEvent.keyuptime = {
	add: function(callback, data) {
		var handleObj = {
			time: data && data[0] ? data[0] : 500
		};			
		
		this.registerEvent('keyup', function(event) {
			Greencode.customEvent.keyuptime.handle.call(this, callback, event, handleObj);				
		});
	},
	remove: function() {
		this.removeEvent('keyup', Greencode.customEvent.keyuptime.handle, true);
	},
	handle: function(callback, event, handleObj) {
		var _this = this;
    	var value = this.value;
    	
		clearTimeout(handleObj.eventClick);		
		handleObj.eventClick = setTimeout(function() {
	    	if(value === handleObj.lastTxt)
				return false;

	    	callback.call(_this, event);
			
			handleObj.lastTxt = value;
			handleObj.eventClick = 0;
		}, handleObj.time);	
    }
};

Greencode.customEvent.enter = {
	add: function(callback) {
		this.registerEvent('keyup', function(event) {
			if(event.keyCode === 13)
				Greencode.customEvent.enter.handle.call(this, callback, event);				
		});
	},
	remove: function() {
		this.removeEvent('keyup', Greencode.customEvent.enter.handle, true);
	},
	handle: function(callback, event) {
		callback.call(this, event);
    }
};