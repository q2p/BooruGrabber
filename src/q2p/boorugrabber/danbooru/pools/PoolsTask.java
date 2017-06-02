package q2p.boorugrabber.danbooru.pools;

import java.util.LinkedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.help.Parser;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.UnpredictableAnswer;
import q2p.boorugrabber.queue.UnpredictableTask;
import q2p.boorugrabber.storage.SaveTemporaryTask;

final class PoolsTask implements UnpredictableTask {
	private final int page;

	PoolsTask(final int page) {
		this.page = page;
	}

	public final UnpredictableAnswer work() {
		Log.lapse("Пулы #", 50, page);
		final UnpredictableAnswer ret = receive();
		if(ret == null)
			Log.out("Не удалось обработать список пулов #" + page);

		return ret;
	}

	final UnpredictableAnswer receive() {
		final Response response = Danbooru.networker.receive("GET", "https://danbooru.donmai.us/pools.json?search[order]=created_at&limit=100&page="+page, null);

		if(response == null)
			return null;

		final UnpredictableAnswer ret = new UnpredictableAnswer();
		
		try {
			final JSONArray pools = JSON.parseArray(response.toString());
			if(pools == null)
				return null;

			if(pools.size() == 0)
				return ret.hasNoMore();

			for(int i = 0; i != pools.size(); i++) {
				final JSONObject pool = pools.getJSONObject(i);

				final Integer id = pool.getInteger("id");
				if(id == null)
					return null;

				final String name = pool.getString("name");
				if(name == null)
					return null;

				final Long creationDate = Danbooru.parseTDate(pool.getString("created_at"));
				if(creationDate == null)
					return null;

				final Long updateDate = Danbooru.parseTDate(pool.getString("updated_at"));
				if(updateDate == null)
					return null;

				final String description = pool.getString("description");
				if(description == null)
					return null;
				
				final Boolean active = pool.getBoolean("is_active");
				if(active == null)
					return null;
				
				final Boolean deleted = pool.getBoolean("is_deleted");
				if(deleted == null)
					return null;
				
				String temp = pool.getString("category");
				if(temp == null)
					return null;

				final boolean series;
				switch(temp) {
					case "collection":
						series = false;
						break;
					case "series":
						series = true;
						break;
					default:
						return null;
				}

				temp = pool.getString("post_ids");
				if(temp == null)
					return null;
				final LinkedList<String> tlist;
				final int[] posts;
				if(temp.length() == 0)
					posts = new int[0];
				else {
					tlist = Parser.split(temp, ' ');
					posts = new int[tlist.size()];
					for(int j = posts.length - 1; j != -1; j--)
						posts[j] = Integer.parseInt(tlist.removeLast());
				}

				ret.task(new SaveTemporaryTask(new Pool(id, name, description, creationDate, updateDate, deleted, active, series, posts)));
			}
		} catch(final Exception e) {
			return null;
		}

		return ret;
	}
}