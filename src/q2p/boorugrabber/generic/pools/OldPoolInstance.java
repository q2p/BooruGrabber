package q2p.boorugrabber.generic.pools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.network.Response;

final class OldPoolInstance extends PoolInstance {
	OldPoolInstance(final GenericBooru booru, final int id) {
		super(booru, id);
	}
	
	protected final boolean receive() {
		final Response response = booru.networker.receive("GET", booru.urlPrefix+"index.php?page=pool&s=show&id="+id, booru.headers);
		if(response == null)
			return true;
		
		try {
			final Document document = Jsoup.parse(response.toString());
			if(document == null)
				return true;

			Elements elements;

			Element element = document.getElementById("pool-show");
			if(element == null) {
				element = document.getElementById("content");
				if(element == null)
					return true;

				elements = element.getElementsByTag("h1");
				if(elements.size() != 1)
					return true;

				if(!elements.get(0).text().equals("Nobody here but us chickens!"))
					return true;

				deleted = true;

				return false;
			}

			elements = element.getElementsByTag("h4");
			if(elements.size() != 1)
				return true;

			String temp = elements.get(0).text();

			if(!temp.startsWith("Pool: "))
				return true;

			name = temp.substring(6);

			elements = element.getElementsByClass("thumb");
			for(final Element el : elements) {
				final Elements els = el.getElementsByTag("a");
				if(els.size() != 1)
					return true;

				temp = els.get(0).attr("href");

				int idx = temp.indexOf('?') + 1;

				if(idx == 0)
					return true;

				idx = temp.indexOf("&id=", idx) + 4;

				if(idx == 3)
					return true;

				int idx2 = temp.indexOf('&', idx);
				if(idx2 == -1)
					idx2 = temp.length();

				posts.addLast(Integer.parseInt(temp.substring(idx, idx2)));
			}

			return false;
		} catch(final Exception e) {
			return true;
		}
	}
}