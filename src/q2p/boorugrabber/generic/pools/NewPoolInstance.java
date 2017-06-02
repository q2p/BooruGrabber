package q2p.boorugrabber.generic.pools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.network.Response;

final class NewPoolInstance extends PoolInstance {
	NewPoolInstance(final GenericBooru booru, final int id) {
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

			final Element element = document.getElementById("content");
			if(element == null)
				return true;
			
			Elements elements = element.getElementsByTag("h3");
			if(elements.size() == 0) {
				elements = element.getElementsByTag("h1");
				if(elements.size() != 1)
					return true;

				if(!elements.get(0).text().equals("Nobody here but us chickens!"))
					return true;

				deleted = true;

				return false;
			} else if(elements.size() != 1)
				return true;

			String temp = elements.get(0).text();
			if(!temp.startsWith("Now Viewing: "))
				return true;

			name = temp.substring(13);

			elements = element.getElementsByClass("thumb");
			for(final Element el : elements) {
				temp = el.id();
				if(!temp.startsWith("p"))
					return true;

				posts.addLast(Integer.parseInt(temp.substring(1)));
			}

			return false;
		} catch(final Exception e) {
			return true;
		}
	}
}