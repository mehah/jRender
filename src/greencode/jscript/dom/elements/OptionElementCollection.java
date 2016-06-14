package greencode.jscript.dom.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Node;
import greencode.jscript.dom.Window;

public final class OptionElementCollection<T> extends DOM implements Iterable<OptionElement<T>>{
	private final Class<T> typeValue;

	OptionElementCollection(Window window, Class<T> typeValue) {
		super(window);
		this.typeValue = typeValue;
	}
	
	List<OptionElement<T>> list;
	private OptionIterator iterator;
	
	public Iterator<OptionElement<T>> iterator() {
		if(iterator == null)
			iterator = new OptionIterator();
		else
			iterator.reset();
		
		return iterator;
	}
	
	public OptionElement<T> namedItem(String nameOrId) {
		String res;
		for (OptionElement<T> o : list) {
			if((res = o.getAttribute("name")) != null && res.equals(nameOrId) || (res = o.getAttribute("id")) != null && res.equals(nameOrId))
				return o;
		}
		
		return null;
	}
	
	public OptionElement<T> item(int index) {
		OptionElement<T> e = list.get(index);
		if(e == null)
			DOMHandle.registerReturnByCommand((Node)(e = new OptionElement<T>(this.window, typeValue)), this, "item", index);
		return e;
	}

	public Integer length() {
		if(list == null) {
			list = new ArrayList<OptionElement<T>>();
			final Integer size = DOMHandle.getVariableValueByProperty(this, "length", Integer.class, "length");
			for (int i = -1; ++i < size;)
				list.add(null);
		}
		
		return list.size();
	}
	
	public void add(OptionElement<T> option) {
		add(option, null);
	}
	
	public void add(OptionElement<T> option, Integer index) {
		if(index == null)
		{
			list.add(option);
			DOMHandle.execCommand(this, "add", option);	
		}else {
			list.add(index, option);
			DOMHandle.execCommand(this, "add", option, index);	
		}	
	}
	
	public OptionElement<T> remove(int index) {
		OptionElement<T> e = list.remove(index);
		DOMHandle.execCommand(this, "remove", index);
		return e;
	}
	
	private class OptionIterator implements Iterator<OptionElement<T>> {
		private int currentIndex = 0;
		
		public void reset() { currentIndex = 0; };
		
		public boolean hasNext() { return currentIndex < list.size(); }

		public OptionElement<T> next() { return item(currentIndex++); }

		public void remove() {}
	}
}
