package q2p.boorugrabber.generic.aliases;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.generic.tags.TagsBank;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.UnpredictableAnswer;
import q2p.boorugrabber.queue.UnpredictableTask;

final class AliasTask implements UnpredictableTask {
	private final GenericBooru booru;
	private final int page;

	AliasTask(final GenericBooru booru, final int page) {
		this.booru = booru;
		this.page = page;
	}

	public final UnpredictableAnswer work() {
		Log.lapse("Синонимы #", 2500, page);
		final UnpredictableAnswer ret = receive();
		if(ret == null)
			Log.out("Не удалось обработать список синонимов #"+page);

		return ret;
	}

	private final UnpredictableAnswer receive() {
		final Response response = booru.networker.receive("GET", booru.urlPrefix + "index.php?page=alias&s=list&pid="+page, booru.headers);

		if(response == null)
			return null;
		
		try {
			final Document document = Jsoup.parse(response.toString());
			if(document == null)
				return null;

			final Element element = document.getElementById("aliases");
			if(element == null)
				return null;

			final Elements elements = element.getElementsByClass("even");
			if(elements.size() == 0)
				return new UnpredictableAnswer().hasNoMore();

			for(final Element el : elements) {
				final Elements els = el.getElementsByTag("td");
				if(els.size() != 4)
					return null;
				
				String temp = GenericBooru.findAnchorText(els.get(1));
				if(temp == null)
					return null;
				final int from = TagsBank.index(temp);
				temp = GenericBooru.findAnchorText(els.get(2));
				if(temp == null)
					return null;
				final int to = TagsBank.index(temp);
				final String reason = els.get(3).text();
				AliasesStorage.index(from, to, !el.hasClass("pending-tag"), reason);
			}
		} catch(final Exception e) {
			return null;
		}

		return new UnpredictableAnswer();
	}
}