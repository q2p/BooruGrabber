package q2p.boorugrabber.derpibooru.tags;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.derpibooru.Derpibooru;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.UnpredictableAnswer;
import q2p.boorugrabber.queue.UnpredictableTask;

final class TagsTask implements UnpredictableTask {
	public final int page;

	TagsTask(final int page) {
		this.page = page;
	}

	public final UnpredictableAnswer work() {
		Log.lapse("Тэги #", 50, page);
		final UnpredictableAnswer ret = receive(page);
		if(ret == null)
			Log.out("Не удалось обработать список тэгов #"+page);

		return ret;
	}

	private static final UnpredictableAnswer receive(final int page) {
		final Response response = Derpibooru.networker.receive("GET", "https://derpibooru.org/tags.json?page="+page, Derpibooru.headers);

		if(response == null)
			return null;

		final UnpredictableAnswer ret = new UnpredictableAnswer();
		
		try {
			final JSONArray tags = JSON.parseArray(response.toString());
			if(tags == null)
				return null;

			if(tags.size() == 0)
				return ret.hasNoMore();

			for(int i = tags.size() - 1; i != -1; i--) {
				final JSONObject tag = tags.getJSONObject(i);

				if(tag.getInteger("id") == null)
					return null;
				
				String description = tag.getString("short_description");
				if(description == null)
					return null;
				
				String name = tag.getString("description");
				if(name == null)
					return null;
				if(name.length() != 0) {
					if(description.length() != 0)
						description += '\n';
					
					description += name;
				}
				
				name = tag.getString("aliased_to");
				final int aliasedTo = name == null ? -1 : Derpibooru.tags.index(name);

				name = tag.getString("name");
				if(name == null)
					return null;
				
				final int[] implies = Derpibooru.parseTags(tag.getString("implied_tags"));
				if(implies == null)
					return null;
				
				Derpibooru.tags.index(name, new TagVersion(TagType.getByNamespace(tag.getString("namespace")), description, aliasedTo, implies));
			}
		} catch(final Exception e) {
			return null;
		}

		return ret;
	}
}