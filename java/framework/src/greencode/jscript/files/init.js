var lastUniqueId = 0;
var listTags = {};
var viewId;

var _intervalId = setInterval(function() {
	Bootstrap.init();
}, 15);

Greencode.crossbrowser.registerEvent.call(window, 'load', function() {
	clearInterval(_intervalId);
	
	if(window.location.hash.indexOf('#!') === 0)
	{
		window.location.href = CONTEXT_PATH+'/'+window.location.hash.substring(2);
		return;
	}
	
	Bootstrap.init();
	
	/*window.registerEvent('popstate', function (e) {
    	if(e.state != null && e.state.content != null)
    	{
    		var o = $(e.state.selector).html(e.state.content);
    		
    		var tags = listTags[window.location.href];
    		
    		o.children().each(function() {
    			var $this = $(this);
    			$this.replaceWith(tags[$this.attr('_uid')]);
    		});
    	}
    });*/
});