package com.jrender.jscript.dom;

import com.jrender.http.Conversation;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.window.annotation.Page;
import com.jrender.kernel.JRenderContext;

public class Element extends ElementCustom {
	protected Element(Window window) {
		super(window);
	}

	protected Element(Window window, String tagName) {
		super(window);
		DOMHandle.setVariableValue(this, "tagName", tagName);
	}

	public Boolean hasAttribute(String name) {
		return DOMHandle.getVariableValueByCommand(this, "hasAttribute", Boolean.class, "hasAttribute", name);
	}

	public String getAttribute(String name) {
		return DOMHandle.getVariableValueByCommand(this, "attr." + name, String.class, "getAttribute", name);
	}

	public void setAttribute(String name, String value) {
		DOMHandle.setVariableValue(this, "attr." + name, value);
		DOMHandle.execCommand(this, "setAttribute", name, value);
	}

	public void removeAttribute(String name) {
		DOMHandle.removeVariable(this, "attr." + name);
		DOMHandle.execCommand(this, "removeAttribute", name);
	}

	public void style(String property, String value) {
		DOMHandle.setVariableValue(this, "style." + property, value);
		DOMHandle.setProperty(this, "style." + property, value);
	}

	public String style(String property) {
		return DOMHandle.getVariableValueByCommand(this, "style." + property, String.class, "getStyle", property);
	}

	public Element[] getElementsByTagName(String tagName) {
		return getElementsByTagName(tagName, -1);
	}

	public Element[] getElementsByTagName(String tagName, int count) {
		return getElementsBy("getElementsByTagName.length", "getElementsByTagName", tagName, count);
	}

	public <E extends Element> E[] getElementsByTagName(String tagName, int count, Class<E> castTo) {
		return ElementHandle.cast(getElementsByTagName(tagName, count), castTo);
	}

	public Element[] getElementsByClassName(String tagName) {
		return getElementsByClassName(tagName, -1);
	}

	public Element[] getElementsByClassName(String tagName, int count) {
		return getElementsBy("getElementsByClassName.length", "getElementsByClassName", tagName, count);
	}

	public <E extends Element> E[] getElementsByClassName(String tagName, int count, Class<E> castTo) {
		return ElementHandle.cast(getElementsByClassName(tagName, count), castTo);
	}

	public Element querySelector(String selector) {
		return querySelector(selector, null, null);
	}

	public <E extends Element> E querySelector(String selector, Class<E> cast) {
		return querySelector(selector, cast, null);
	}

	public <E extends Element> E querySelector(String selector, Class<E> cast, Class<?> typeValue) {
		Element e;
		if (cast == null) {
			e = new Element(this.window);
		} else {
			if (typeValue == null && cast.getTypeParameters().length > 0)
				typeValue = String.class;

			e = typeValue == null ? ElementHandle.getInstance(cast, window) : ElementHandle.getInstance(cast, window, typeValue);
		}

		DOMHandle.registerReturnByCommand(e, this, "querySelector", selector);

		return (E) e;
	}

	public Element[] querySelectorAll(String selector) {
		return querySelectorAll(selector, -1);
	}

	public Element[] querySelectorAll(String selector, int count) {
		return getElementsBy("querySelectorAll.length", "querySelectorAll", selector, count);
	}

	public <E extends Element> E[] querySelectorAll(String selector, Class<E> castTo) {
		return ElementHandle.cast(querySelectorAll(selector), castTo);
	}

	public <E extends Element> E[] querySelectorAll(String selector, int count, Class<E> castTo) {
		return ElementHandle.cast(querySelectorAll(selector, count), castTo);
	}

	private Element[] getElementsBy(String varName, String command, String tagName, int qnt) {
		if (qnt < 1)
			qnt = DOMHandle.getVariableValueByPropertyNoCache(this, varName, Integer.class, command + "('" + tagName + "').length");

		Element[] elements = new Element[qnt];
		for (int i = -1; ++i < qnt;) {
			elements[i] = new Element(this.window);
		}

		DOMHandle.registerReturnsByCommand(elements, this, command, tagName);

		return elements;
	}

	public Element cloneNode() {
		return (Element) super.cloneNode();
	}

	public Element cloneNode(boolean deep) {
		return (Element) super.cloneNode(deep);
	}

	public void replaceWith(String html) {
		innerHTML(html);
	}

	public void replaceWith(Element e) {
		DOMHandle.execCommand(this, "replaceWith", e);
	}

	public void replaceWith(Class<? extends Window> controllerClass) {
		replaceWith(controllerClass, null, null);
	}

	public void replaceWith(Class<? extends Window> controllerClass, Conversation conversation) {
		replaceWith(controllerClass, null, conversation);
	}

	public void replaceWith(Class<? extends Window> controllerClass, String pageName) {
		replaceWith(controllerClass, pageName, null);
	}

	public void replaceWith(Class<? extends Window> controllerClass, String pageName, Conversation conversation) {
		final Page page = WindowHandle.getPageByName(controllerClass, pageName);
		replaceWithPageURL((page.URLName().isEmpty() ? page.path() : page.URLName()), conversation);
	}

	public void replaceWithPageURL(String url) {
		replaceWithPageURL(url, null);
	}

	public void replaceWithPageURL(String url, Conversation conversation) {
		conversation = (conversation == null ? JRenderContext.getInstance().getRequest().getConversation() : conversation);

		DOMHandle.execCommand(this, "replaceWithPageURL", com.jrender.kernel.$JRenderContext.getContextPath() + "/" + url, conversation.getViewSession().getId(), conversation.getId());
	}

	public void innerHTML(String html) {
		DOMHandle.setProperty(this, "innerHTML", html);
	}

	public String innerHTML() {
		return DOMHandle.getVariableValueByProperty(this, "innerHTML", String.class, "innerHTML");
	}

	public void tagName(String tagName) {
		DOMHandle.setProperty(this, "tagName", tagName);
	}

	public String tagName() {
		return DOMHandle.getVariableValueByProperty(this, "tagName", String.class, "tagName");
	}

	public void title(String title) {
		DOMHandle.setProperty(this, "title", title);
	}

	public String title() {
		return DOMHandle.getVariableValueByProperty(this, "title", String.class, "title");
	}

	public void id(String id) {
		DOMHandle.setProperty(this, "id", id);
	}

	public String id() {
		return DOMHandle.getVariableValueByProperty(this, "id", String.class, "id");
	}

	public String namespaceURI() {
		return DOMHandle.getVariableValueByProperty(this, "namespaceURI", String.class, "namespaceURI");
	}

	public void dir(String dir) {
		DOMHandle.setProperty(this, "dir", dir);
	}

	public String dir() {
		return DOMHandle.getVariableValueByProperty(this, "dir", String.class, "dir");
	}

	public void lang(String lang) {
		DOMHandle.setProperty(this, "lang", lang);
	}

	public String lang() {
		return DOMHandle.getVariableValueByProperty(this, "lang", String.class, "lang");
	}

	public Integer offsetHeight() {
		return DOMHandle.getVariableValueByProperty(this, "offsetHeight", Integer.class, "offsetHeight");
	}

	public Integer offsetWidth() {
		return DOMHandle.getVariableValueByProperty(this, "offsetWidth", Integer.class, "offsetWidth");
	}

	public Integer offsetLeft() {
		return DOMHandle.getVariableValueByProperty(this, "offsetLeft", Integer.class, "offsetLeft");
	}

	public Integer offsetParent() {
		return DOMHandle.getVariableValueByProperty(this, "offsetParent", Integer.class, "offsetParent");
	}

	public Integer offsetTop() {
		return DOMHandle.getVariableValueByProperty(this, "offsetTop", Integer.class, "offsetTop");
	}

	public Integer scrollHeight() {
		return DOMHandle.getVariableValueByProperty(this, "scrollHeight", Integer.class, "scrollHeight");
	}

	public Integer scrollLeft() {
		return DOMHandle.getVariableValueByProperty(this, "scrollLeft", Integer.class, "scrollLeft");
	}

	public Integer scrollTop() {
		return DOMHandle.getVariableValueByProperty(this, "scrollTop", Integer.class, "scrollTop");
	}

	public Integer scrollWidth() {
		return DOMHandle.getVariableValueByProperty(this, "scrollWidth", Integer.class, "scrollWidth");
	}

	public Integer clientHeight() {
		return DOMHandle.getVariableValueByProperty(this, "clientHeight", Integer.class, "clientHeight");
	}

	public Integer clientWidth() {
		return DOMHandle.getVariableValueByProperty(this, "clientWidth", Integer.class, "clientWidth");
	}

	public void tabindex(String tabindex) {
		DOMHandle.setProperty(this, "tabindex", tabindex);
	}

	public Integer tabindex() {
		return DOMHandle.getVariableValueByProperty(this, "tabindex", Integer.class, "tabindex");
	}

	public void blur() {
		DOMHandle.execCommand(this, "blur");
	}
	
	public void click() {
		DOMHandle.execCommand(this, "click");
	}
	
	public void focus() {
		DOMHandle.execCommand(this, "focus");
	}

	public void remove() {
		DOMHandle.execCommand(this, "remove");
	}

	public void insertAdjacentHTML(String position, String text, Object... args) {
		DOMHandle.execCommand(this, "insertAdjacentHTML", position, text, args.length > 0 ? args : null);
	}
}