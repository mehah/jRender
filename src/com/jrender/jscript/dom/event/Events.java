package com.jrender.jscript.dom.event;

public abstract class Events {	
	public static final String
		UNDEFINED = "undefined",
		BLUR = "blur",
		FOCUS = "focus",
		LOAD = "load",
		RESIZE = "riseze",
		SCROLL = "scroll", UNLOAD = "unload",
		BEFOREUNLOAD = "beforeunload",
		CLICK = "click",
		DOUBLE_CLICK = "dblclick",
		MOUSE_DOWN = "mousedown",
		MOUSE_UP = "mouseup",
		MOUSE_MOVE = "mousemove",
		MOUSE_OVER = "mouseover",
		MOUSE_OUT = "mouseout",
		MOUSE_ENTER = "mouseenter",
		OUSE_LEAVE = "mouseleave",
		CHANGE = "change",
		SELECT = "select",
		SUBMIT = "submit",
		KEY_DOWN = "keydown",
		KEY_PRESS = "keypress",
		KEY_UP = "keyup",
		ERROR = "error",
		READY = "ready",
		CONTEXT_MENU = "contextmenu",
		
		/*CUSTOM*/
		SCROLL_REACH_PERCENT = "scrollreachpercent",
		SCROLL_REACH_TOP = "scrollreachtop",
		SCROLL_REACH_BOTTOM = "scrollreachbottom",
		KEY_UP_TIME = "keyuptime",
		ENTER = "enter";
	
	public static class JRender {
		public static final String
			AFTER_EVENT = "afterEvent",
			BEFORE_EVENT = "beforeEvent",
			BEFORE_PAGE_REQUEST = "beforePageRequest",
			AFTER_PAGE_REQUEST = "afterPageRequest",
			INIT = "init",
			PAGE_LOAD = "pageLoad",
			CONTAINER_CLONED = "containerCloned",
			BEFORE_POPSTATE = "beforePopstate",
			AFTER_POPSTATE = "beforePopstate";
	}
};