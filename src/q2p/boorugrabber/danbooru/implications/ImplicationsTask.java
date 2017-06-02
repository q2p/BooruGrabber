package q2p.boorugrabber.danbooru.implications;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.danbooru.Danbooru;
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
		final Response response = Danbooru.networker.receive("GET", "https://danbooru.donmai.us/tag_implications.json?limit=100&page="+page, null);

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
				
				final String tag = implication.getString("antecedent_name");
				if(tag == null)
					return null;

				final String requires = implication.getString("consequent_name");
				if(requires == null)
					return null;

				final String reason = implication.getString("reason");
				if(reason == null)
					return null;

				final String temp = implication.getString("status");
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

				ImplicationsStorage.index(Danbooru.tags.index(tag), Danbooru.tags.index(requires), approved, reason);
			}
		} catch(final Exception e) {
			return null;
		}

		return new UnpredictableAnswer();
	}
}