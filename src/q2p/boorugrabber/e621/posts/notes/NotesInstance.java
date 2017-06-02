package q2p.boorugrabber.e621.posts.notes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.segmented.SegmentInstance;

final class NotesInstance extends SegmentInstance<Note> {
	private final int postId;

	NotesInstance(final int postId, final int page) {
		super(page);
		this.postId = postId;
	}
	
	protected final boolean receive() {
		final Response response = E621.networker.receive("GET", "https://e621.net/note/index.json?post_id="+postId+"&page="+page, E621.headers);

		if(response == null)
			return true;
		
		try {
			final JSONArray notes = JSON.parseArray(response.toString());
			if(notes == null)
				return true;

			for(int i = 0; i != notes.size(); i++) {
				final JSONObject note = notes.getJSONObject(i);

				if(note == null)
					return true;

				final Integer id = note.getInteger("id");
				if(id == null)
					return true;

				Integer author;
				try {
					author = note.getInteger("creator_id");
					if(author == null)
						author = -1;
				} catch (final NumberFormatException e) {
					author = -1;
				}

				final Integer x = note.getInteger("x");
				if(x == null)
					return true;

				final Integer y = note.getInteger("y");
				if(y == null)
					return true;

				final Integer width = note.getInteger("width");
				if(width == null)
					return true;

				final Integer height = note.getInteger("height");
				if(height == null)
					return true;

				final Boolean active = note.getBoolean("is_active");
				if(active == null)
					return true;

				final Long date = E621.parseJSONDate(note.getJSONObject("updated_at"));
				if(date == null)
					return true;

				final Short version = note.getShort("version");
				if(version == null)
					return true;

				final String body = note.getString("body");
				if(body == null)
					return true;

				segments.addLast(new Note(id, author, date, x, y, width, height, body, version, active));
			}
		} catch(final Exception e) {
			return true;
		}

		return false;
	}
}