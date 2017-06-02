package q2p.boorugrabber.help;

import java.util.LinkedList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class Parser {
	public static final int endIndex(final String string, final String pattern, final int fromIndex) {
		final int i = string.indexOf(pattern, fromIndex);
		if(i == -1)
			return -1;
		return i+pattern.length();
	}
	public static final int endIndexTrain(final String string, int fromIndex, final String ... patterns) {
		for(final String pattern : patterns) {
			final int i = string.indexOf(pattern, fromIndex);
			if(i == -1)
				return -1;
			fromIndex = i + pattern.length();
		}
		return fromIndex;
	}
	public static final List<String> devideBy(final String string, final String beg, final String end, final List<String> container) {
		int b;
		int e = 0;
		while(true) {
			b = string.indexOf(beg, e);
			if(b == -1)
				break;
			b += beg.length();
			e = string.indexOf(end, b);
			if(e == -1)
				break;
			container.add(string.substring(b, e));
			e += end.length();
		}
		return container;
	}

	public static final LinkedList<String> split(final String string, final char pattern) {
		final LinkedList<String> container = new LinkedList<String>();
		int pidx = 0;
		int idx;
		while(true) {
			idx = string.indexOf(pattern, pidx);
			if(idx == -1) {
				container.add(string.substring(pidx));
				break;
			}
			container.add(string.substring(pidx, idx));
			pidx = idx+1;
		}

		return container;
	}
	public static final LinkedList<String> split(final String string, final String pattern) {
		final LinkedList<String> container = new LinkedList<String>();
		int pidx = 0;
		int idx;
		while(true) {
			idx = string.indexOf(pattern, pidx);
			if(idx == -1) {
				container.add(string.substring(pidx));
				break;
			}
			container.add(string.substring(pidx, idx));
			pidx = idx+pattern.length();
		}

		return container;
	}
	
	public static final String innerHTML(final String string) {
		final int idx = string.indexOf('>')+1;
		return string.substring(idx, string.indexOf('<', idx)).trim();
	}
	
	public static final Chain chain(final Element element, final String id) {
		if(element == null)
			return new Chain(null);

		return new Chain(element.getElementById(id));
	}

	public static final Chain chain(final Element element) {
		return new Chain(element);
	}

	public static final class Chain {
		private Element element;
		private Chain(final Element element) {
			this.element = element;
		}
		
		public final Chain tag(final String tag) {
			if(element == null)
				return this;

			final boolean free = true;

			final Elements elements = element.children();
			for(final Element el : elements)
				if(el.tagName().equals(tag)) if(free)
					element = el;
				else {
					element = null;
					return this;
				}

			return this;
		}
		public final Chain tagTotal(final String tag, int amount, int select) {
			if(element == null)
				return this;

			final Elements elements = element.children();
			for(final Element el : elements)
				if(el.tagName().equals(tag))
					amount--;

			if(amount != 0) {
				element = null;
				return this;
			}
			
			for(final Element el : elements)
				if(el.tagName().equals(tag)) {
					if(select == 0) {
						element = el;
						return this;
					}
					select--;
				}

			element = null;
			return this;
		}
		public final Chain tagMin(final String tag, int minimum, int select) {
			if(element == null)
				return this;

			final Elements elements = element.children();
			for(final Element el : elements)
				if(el.tagName().equals(tag))
					minimum--;

			if(minimum > 0) {
				element = null;
				return this;
			}
			
			for(final Element el : elements)
				if(el.tagName().equals(tag)) {
					if(select == 0) {
						element = el;
						return this;
					}
					select--;
				}

			element = null;
			return this;
		}
		public final Chain clazz(final String clazz) {
			if(element == null)
				return this;

			final boolean free = true;

			final Elements elements = element.children();
			for(final Element el : elements)
				if(el.hasClass(clazz)) if(free)
					element = el;
				else {
					element = null;
					return this;
				}

			return this;
		}
		public final Chain clazzTotal(final String clazz, int amount, int select) {
			if(element == null)
				return this;

			final Elements elements = element.children();
			for(final Element el : elements)
				if(el.hasClass(clazz))
					amount--;

			if(amount != 0) {
				element = null;
				return this;
			}
			
			for(final Element el : elements)
				if(el.hasClass(clazz)) {
					if(select == 0) {
						element = el;
						return this;
					}
					select--;
				}

			element = null;
			return this;
		}
		public final Chain clazzMin(final String clazz, int minimum, int select) {
			if(element == null)
				return this;

			final Elements elements = element.children();
			for(final Element el : elements)
				if(el.hasClass(clazz))
					minimum--;

			if(minimum > 0) {
				element = null;
				return this;
			}
			
			for(final Element el : elements)
				if(el.hasClass(clazz)) {
					if(select == 0) {
						element = el;
						return this;
					}
					select--;
				}

			element = null;
			return this;
		}
		public final Element get() {
			return element;
		}
	}
}