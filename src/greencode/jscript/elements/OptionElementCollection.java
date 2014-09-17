package greencode.jscript.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;
import greencode.jscript.Window;

public final class OptionElementCollection extends DOM implements Iterable<OptionElement>{

	OptionElementCollection(Window window) {
		super(window);
	}
	
	List<OptionElement> list;
	private OptionIterator iterator;
	
	@Override
	public Iterator<OptionElement> iterator() {
		if(iterator == null)
			iterator = new OptionIterator();
		else
			iterator.reset();
		
		return iterator;
	}
	
	public OptionElement namedItem(String nameOrId) {
		String res;
		for (OptionElement o : list) {
			if((res = o.getAttribute("name")) != null && res.equals(nameOrId) || (res = o.getAttribute("id")) != null && res.equals(nameOrId))
				return o;
		}
		
		return null;
	}
	
	public OptionElement item(int index) {
		OptionElement e = list.get(index);
		if(e == null)
			DOMHandle.registerElementByCommand(this, e = new OptionElement(this.window), "item", index);
		return e;
	}

	public Integer length() {
		if(list == null) {
			list = new ArrayList<OptionElement>();
			final Integer size = DOMHandle.getVariableValueByProperty(this, "length", Integer.class, "length");
			for (int i = -1; ++i < size;)
				list.add(null);
		}
		
		return list.size();
	}
	
	public void add(OptionElement option) {
		add(option, null);
	}
	
	public void add(OptionElement option, Integer index) {
		if(index == null)
		{
			list.add(option);
			DOMHandle.execCommand(this, "add", option);	
		}else {
			list.add(index, option);
			DOMHandle.execCommand(this, "add", option, index);	
		}	
	}
	
	public OptionElement remove(int index) {
		OptionElement e = list.remove(index);
		DOMHandle.execCommand(this, "remove", index);
		return e;
	}
	
	private class OptionIterator implements Iterator<OptionElement> {
		private int currentIndex = 0;
		
		public void reset() { currentIndex = 0; };
		
		@Override
		public boolean hasNext() { return currentIndex < list.size(); }

		@Override
		public OptionElement next() { return item(currentIndex++); }

		@Override
		public void remove() {}
	}
}
