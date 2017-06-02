package q2p.boorugrabber.derpibooru.pools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.derpibooru.Derpibooru;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.segmented.SegmentInstance;

final class PostsInstance extends SegmentInstance<PoolPost> {
	private final String url;

	PostsInstance(final String url, final int page) {
		super(page);
		this.url = url;
	}
	
	protected final boolean receive() {
		final Response response = Derpibooru.networker.receive("GET", url+"?page="+page, Derpibooru.headers);

		if(response == null)
			return true;
		
		try {
			final JSONObject pool = JSON.parseObject(response.toString());
			if(pool == null)
				return true;
			
			final JSONArray posts = pool.getJSONArray("images");
			if(posts == null)
				return true;
			
			for(int i = 0, j = posts.size(); i != j; i++) {
				final Integer id = posts.getJSONObject(i).getInteger("id");
				if(id == null)
					return true;

				segments.addLast(new PoolPost(id));
			}
		} catch(final Exception e) {
			return true;
		}

		return false;
	}
}