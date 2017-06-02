package q2p.boorugrabber.derpibooru.posts.comments;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.derpibooru.Derpibooru;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.segmented.SegmentInstance;

final class CommentsInstance extends SegmentInstance<Comment> {
	private final int postId;

	CommentsInstance(final int postId, final int page) {
		super(page);
		this.postId = postId;
	}
	
	protected final boolean receive() {
		final Response response = Derpibooru.networker.receive("GET", "https://derpibooru.org/images/"+postId+"/comments.json?page=1"+page, Derpibooru.headers);

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

				final String body = comment.getString("body");
				if(body == null)
					return true;
				
				final String author = comment.getString("author");
				if(author == null)
					return true;

				final Long date = Derpibooru.parseDate(comment.getString("posted_at"));
				if(date == null)
					return true;

				final Boolean deleted = comment.getBoolean("deleted");
				if(deleted == null)
					return true;

				segments.addLast(new Comment(id, author, date, body, deleted));
			}
		} catch(final Exception e) {
			return true;
		}

		return false;
	}
}