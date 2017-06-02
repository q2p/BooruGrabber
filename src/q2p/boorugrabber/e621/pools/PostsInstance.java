package q2p.boorugrabber.e621.pools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.segmented.SegmentInstance;

final class PostsInstance extends SegmentInstance<PoolPost> {
	private final int id;

	PostsInstance(final int id, final int page) {
		super(page);
		this.id = id;
	}
	
	protected final boolean receive() {
		final Response response = E621.networker.receive("GET", "https://e621.net/pool/show.json?id="+id+"&page="+page, E621.headers);

		if(response == null)
			return true;
		
		try {
			final JSONObject pool = JSON.parseObject(response.toString());
			if(pool == null)
				return true;

			final JSONArray posts = pool.getJSONArray("posts");
			if(posts == null)
				return true;

			for(int i = posts.size() - 1; i != -1; i--) {
				final JSONObject post = posts.getJSONObject(i);
				if(post == null)
					return true;

				final Integer id = post.getInteger("id");
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