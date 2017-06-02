package q2p.boorugrabber.e621.wiki;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.UnpredictableAnswer;
import q2p.boorugrabber.queue.UnpredictableTask;
import q2p.boorugrabber.storage.SaveTemporaryTask;

final class WikiTask implements UnpredictableTask {
	public final int page;

	WikiTask(final int page) {
		this.page = page;
	}

	public final UnpredictableAnswer work() {
		Log.lapse("Wiki #", 50, page);
		final UnpredictableAnswer ret = receive(page);
		if(ret == null)
			Log.out("Не удалось обработать список статей в wiki #"+page);

		return ret;
	}

	private static final UnpredictableAnswer receive(final int page) {
		final Response response = E621.networker.receive("GET", "https://e621.net/wiki/index.json?order=title&limit=100&page="+page, E621.headers);

		if(response == null)
			return null;

		final UnpredictableAnswer ret = new UnpredictableAnswer();
		
		try {
			final JSONArray articles = JSON.parseArray(response.toString());
			if(articles == null)
				return null;

			if(articles.size() == 0)
				return ret.hasNoMore();

			for(int i = articles.size() - 1; i != -1; i--) {
				final JSONObject article = articles.getJSONObject(i);

				final Integer id = article.getInteger("id");
				if(id == null)
					return null;

				final String title = article.getString("title");
				if(title == null)
					return null;

				final Long creationDate = E621.parseJSONDate(article.getJSONObject("created_at"));
				if(creationDate == null)
					return null;

				final Long updateDate = E621.parseJSONDate(article.getJSONObject("updated_at"));
				if(updateDate == null)
					return null;

				final String body = article.getString("body");
				if(body == null)
					return null;

				final Boolean locked = article.getBoolean("locked");
				if(locked == null)
					return null;

				Short version = article.getShort("version");
				if(version == null)
					return null;
				version--;

				ret.task(new SaveTemporaryTask(new WikiArticle(id, title, locked, creationDate, updateDate, body, version)));
			}
		} catch(final Exception e) {
			return null;
		}

		return ret;
	}
}