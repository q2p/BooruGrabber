package q2p.boorugrabber.e621.aliases;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
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
		final Response response = E621.networker.receive("GET", "https://e621.net/tag_alias/index.json?order=date&page="+page, E621.headers);

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

				if(alias.getInteger("id") == null)
					return null;

				final String from = alias.getString("name");
				if(from == null)
					return null;

				final Integer to = alias.getInteger("alias_id");
				if(to == null)
					return null;

				final Boolean pending = alias.getBoolean("pending");
				if(pending == null)
					return null;

				AliasStorage.index(from, to, !pending);
			}
		} catch(final Exception e) {
			return null;
		}

		return new UnpredictableAnswer();
	}
}