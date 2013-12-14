if(typeof Window != 'undefined')
{
	Window.prototype.registerEvent = Greencode.tag.registerEvent;
	Window.prototype.removeEvent = Greencode.tag.removeEvent;
	Window.prototype.shootEvent = Greencode.tag.shootEvent;
}else
{
	window.registerEvent = Greencode.tag.registerEvent;
	window.removeEvent = Greencode.tag.removeEvent;
	window.shootEvent = Greencode.tag.shootEvent;
}

if(typeof EventTarget != 'undefined')
{
	EventTarget.prototype.registerEvent = Greencode.tag.registerEvent;
	EventTarget.prototype.removeEvent = Greencode.tag.removeEvent;
	EventTarget.prototype.shootEvent = Greencode.tag.shootEvent;	
}else if(typeof Element != 'undefined')
{
	Element.prototype.registerEvent = Greencode.tag.registerEvent;
	Element.prototype.removeEvent = Greencode.tag.removeEvent;
	Element.prototype.shootEvent = Greencode.tag.shootEvent;
}else
{
	/*Object.registerEvent = Greencode.tag.registerEvent;
	Object.removeEvent = Greencode.tag.removeEvent;
	Object.shootEvent = Greencode.tag.shootEvent;
	
	window.registerEvent('load', function() {		
		Element.prototype.registerEvent = Greencode.tag.registerEvent;
		Element.prototype.removeEvent = Greencode.tag.removeEvent;
		Element.prototype.shootEvent = Greencode.tag.shootEvent;
		
		if(typeof Object.prototype.registerEvent != 'undefined')
		{
			delete Object.prototype.registerEvent;
			delete Object.prototype.removeEvent;
			delete Object.prototype.shootEvent;
		}
	});*/
}