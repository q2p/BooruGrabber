package q2p.boorugrabber.danbooru.wiki;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.danbooru.posts.tags.TagType;
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
		final Response response = Danbooru.networker.receive("GET", "https://danbooru.donmai.us/wiki_pages.json?search[order]=title&limit=100&page="+page, null);

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

				final Long creationDate = Danbooru.parseTDate(article.getString("created_at"));
				if(creationDate == null)
					return null;

				final Long updateDate = Danbooru.parseTDate(article.getString("updated_at"));
				if(updateDate == null)
					return null;

				final String title = article.getString("title");
				if(title == null)
					return null;

				final String body = article.getString("body");
				if(body == null)
					return null;

				final Boolean locked = article.getBoolean("is_locked");
				if(locked == null)
					return null;

				String otherNames = article.getString("other_names");
				if(otherNames == null)
					otherNames = "";

				final Boolean deleted = article.getBoolean("is_deleted");
				if(deleted == null)
					return null;
				
				final TagType type = TagType.getByOutId(article.getByte("category_name"));
				if(type == null)
					return null;

				ret.task(new SaveTemporaryTask(new WikiArticle(id, deleted, title, locked, creationDate, updateDate, body, otherNames, type)));
			}
		} catch(final Exception e) {
			return null;
		}

		return ret;
	}
}