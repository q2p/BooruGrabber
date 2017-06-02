package q2p.boorugrabber.e621.authors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.UnpredictableAnswer;
import q2p.boorugrabber.queue.UnpredictableTask;
import q2p.boorugrabber.storage.SaveTemporaryTask;

final class AuthorsTask implements UnpredictableTask {
	public final int page;

	AuthorsTask(final int page) {
		this.page = page;
	}

	public final UnpredictableAnswer work() {
		Log.lapse("Авторы #", 50, page);
		final UnpredictableAnswer ret = receive(page);
		if(ret == null)
			Log.out("Не удалось обработать список авторов #"+page);

		return ret;
	}

	private static final UnpredictableAnswer receive(final int page) {
		final Response response = E621.networker.receive("GET", "https://e621.net/artist/index.json?order=name&limit=100&page="+page, E621.headers);

		if(response == null)
			return null;

		final UnpredictableAnswer ret = new UnpredictableAnswer();
		
		try {
			final JSONArray authors = JSON.parseArray(response.toString());
			if(authors == null)
				return null;

			if(authors.size() == 0)
				return ret.hasNoMore();

			for(int i = authors.size() -1; i != -1; i--) {
				final JSONObject author = authors.getJSONObject(i);

				final Integer id = author.getInteger("id");
				if(id == null)
					return null;

				final String name = author.getString("name");
				if(name == null)
					return null;

				final String otherNames = author.getString("other_names");
				if(otherNames == null)
					return null;

				String groupName = author.getString("group_name");
				if(groupName == null)
					groupName = "";

				final String urls = author.getString("urls");
				if(urls == null)
					return null;

				Short version = author.getShort("version");
				if(version == null)
					version = 0;
				else
					version--;

				final Boolean active = author.getBoolean("is_active");
				if(active == null)
					return null;

				ret.task(new SaveTemporaryTask(new Author(id, name, otherNames, groupName, urls, active, version)));
			}
		} catch(final Exception e) {
			return null;
		}

		return ret;
	}
}