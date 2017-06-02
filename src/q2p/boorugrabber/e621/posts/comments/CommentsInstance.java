package q2p.boorugrabber.e621.posts.comments;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.segmented.SegmentInstance;

final class CommentsInstance extends SegmentInstance<Comment> {
	private final int postId;

	CommentsInstance(final int postId, final int page) {
		super(page);
		this.postId = postId;
	}
	
	protected final boolean receive() {
		final Response response = E621.networker.receive("GET", "https://e621.net/comment/index.json?post_id="+postId+"&page="+page, E621.headers);

		if(response == null)
			return true;
		
		try {
			final JSONArray comments = JSON.parseArray(response.toString());
			if(comments == null)
				return true;

			for(int i = comments.size() - 1; i != -1; i--) {
				final JSONObject comment = comments.getJSONObject(i);

				if(comment == null)
					return true;
				
				final Integer id = comment.getInteger("id");
				if(id == null)
					return true;
				
				Integer author;
				try {
					author = comment.getInteger("creator_id");
					if(author == null)
						author = -1;
				} catch (final NumberFormatException e) {
					author = -1;
				}

				final Integer score = comment.getInteger("score");
				if(score == null)
					return true;

				final Long date = E621.parseSpaceDate(comment.getString("created_at"));
				if(date == null)
					return true;

				final String body = comment.getString("body");
				if(body == null)
					return true;

				segments.addLast(new Comment(id, author, date, score, body));
			}
		} catch(final Exception e) {
			return true;
		}

		return false;
	}
}