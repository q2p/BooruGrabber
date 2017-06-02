package q2p.boorugrabber.danbooru.aliases;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.danbooru.posts.tags.TagsStorage;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.UnpredictableAnswer;
import q2p.boorugrabber.queue.UnpredictableTask;

final class AliasTask implements UnpredictableTask {
	public final int page;

	AliasTask(final int page) {
		this.page = page;
	}

	public final UnpredictableAnswer work() {
		Log.lapse("Синонимы #", 50, page);
		final UnpredictableAnswer ret = receive(page);
		if(ret == null)
			Log.out("Не удалось обработать список синонимов #"+page);

		return ret;
	}

	private static final UnpredictableAnswer receive(final int page) {
		final Response response = Danbooru.networker.receive("GET", "https://danbooru.donmai.us/tag_aliases.json?limit=100&page="+page, null);

		if(response == null)
			return null;

		try {
			final JSONArray aliases = JSON.parseArray(response.toString());
			if(aliases == null)
				return null;

			if(aliases.size() == 0)
				return new UnpredictableAnswer().hasNoMore();

			for(int i = aliases.size() - 1; i != -1; i--) {
				final JSONObject alias = aliases.getJSONObject(i);

				final String from = alias.getString("antecedent_name");
				if(from == null)
					return null;

				final String to = alias.getString("consequent_name");
				if(to == null)
					return null;

				final String reason = alias.getString("reason");
				if(reason == null)
					return null;

				final String temp = alias.getString("status");
				if(temp == null)
					return null;
				final boolean approved;
				switch(temp) {
					case "pending":
						approved = false;
						break;
					case "active":
						approved = true;
						break;
					default:
						return null;
				}

				AliasStorage.index(TagsStorage.index(from), TagsStorage.index(to), approved, reason);
			}
		} catch(final Exception e) {
			return null;
		}

		return new UnpredictableAnswer();
	}
}