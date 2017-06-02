package q2p.boorugrabber.danbooru.posts.notes;

import java.util.LinkedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.danbooru.posts.PostInstance;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.SaveTemporaryTask;

public final class NotesInstance implements Task {
	private final PostInstance post;
	private final LinkedList<Note> notes = new LinkedList<Note>();

	public NotesInstance(final PostInstance post) {
		this.post = post;
	}
	
	public final LinkedList<Task> work() {
		final LinkedList<Task> ret = receive();
		if(ret == null)
			Log.out("Не удалось обработать заметки к посту #"+post.id);

		return ret;
	}

	public final LinkedList<Task> receive() {
		final Response response = Danbooru.networker.receive("GET", "https://danbooru.donmai.us/notes.json?group_by=note&search[post_id]=" + post.id, null);

		if(response == null)
			return null;
		
		try {
			final JSONArray notes = JSON.parseArray(response.toString());
			if(notes == null)
				return null;

			if(notes.size() == 0)

				for(int i = notes.size() - 1; i != -1; i--) {
					final JSONObject note = notes.getJSONObject(i);

					final Integer id = note.getInteger("id");
					if(id == null)
						return null;

					final Long creationDate = Danbooru.parseTDate(note.getString("created_at"));
					if(creationDate == null)
						return null;

					final Long updateDate = Danbooru.parseTDate(note.getString("updated_at"));
					if(updateDate == null)
						return null;

					final Integer x = note.getInteger("x");
					if(x == null)
						return null;

					final Integer y = note.getInteger("y");
					if(y == null)
						return null;

					final Integer width = note.getInteger("width");
					if(width == null)
						return null;

					final Integer height = note.getInteger("height");
					if(height == null)
						return null;

					final Boolean active = note.getBoolean("is_active");
					if(active == null)
						return null;
					
					final String body = note.getString("body");
					if(body == null)
						return null;

					final Short version = note.getShort("version");
					if(version == null)
						return null;

					this.notes.addLast(new Note(id, creationDate, updateDate, x, y, width, height, body, version, active));
				}
		} catch(final Exception e) {
			return null;
		}

		final LinkedList<Task> ret = new LinkedList<Task>();
		ret.addLast(new SaveTemporaryTask(post));
		return ret;
	}
}