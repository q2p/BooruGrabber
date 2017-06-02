package q2p.boorugrabber.e621.implications;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.UnpredictableAnswer;
import q2p.boorugrabber.queue.UnpredictableTask;

final class ImplicationsTask implements UnpredictableTask {
	public final int page;

	ImplicationsTask(final int page) {
		this.page = page;
	}

	public final UnpredictableAnswer work() {
		Log.lapse("Требования тэгов #", 50, page);
		final UnpredictableAnswer ret = receive(page);
		if(ret == null)
			Log.out("Не удалось обработать список требований тэгов #"+page);

		return ret;
	}

	private static final UnpredictableAnswer receive(final int page) {
		final Response response = E621.networker.receive("GET", "https://e621.net/tag_implication/index.json?order=date&page="+page, E621.headers);

		if(response == null)
			return null;
		
		try {
			final JSONArray implications = JSON.parseArray(response.toString());
			if(implications == null)
				return null;

			if(implications.size() == 0)
				return new UnpredictableAnswer().hasNoMore();

			for(int i = implications.size() - 1; i != -1; i--) {
				final JSONObject implication = implications.getJSONObject(i);

				if(implication.getInteger("id") == null)
					return null;
				
				final Integer tag = implication.getInteger("predicate_id");
				if(tag == null)
					return null;

				final Integer requires = implication.getInteger("consequent_id");
				if(requires == null)
					return null;

				final Boolean pending = implication.getBoolean("pending");
				if(pending == null)
					return null;

				ImplicationStorage.index(tag, requires, !pending);
			}
		} catch(final Exception e) {
			return null;
		}

		return new UnpredictableAnswer();
	}
}