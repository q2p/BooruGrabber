package q2p.boorugrabber.e621.posts.tags;

import java.util.LinkedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.e621.posts.PostInstance;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.SaveTemporaryTask;

public final class TagsTask implements Task {
	private final PostInstance post;

	public TagsTask(final PostInstance post) {
		this.post = post;
	}

	public final LinkedList<Task> work() {
		final LinkedList<Integer> tags = new LinkedList<Integer>();

		if(receive(post.id, tags)) {
			Log.out("Не удалось обработать тэги для поста #"+post.id);
			return null;
		}
		
		final LinkedList<Task> ret = new LinkedList<Task>();

		final SaveTemporaryTask task = post.appendTags(tags);

		if(task != null)
			ret.addLast(task);

		return ret;
	}
	
	private static final boolean receive(final int postId, final LinkedList<Integer> container) {
		final Response response = E621.networker.receive("GET", "https://e621.net/post/tags.json?id="+postId, E621.headers);

		if(response == null)
			return true;

		try {
			final JSONArray tags = JSON.parseArray(response.toString());
			if(tags == null)
				return true;

			for(int i = tags.size()-1; i != -1; i--) {
				final JSONObject tag = tags.getJSONObject(i);
				if(tag == null)
					return true;

				final Integer id = tag.getInteger("id");
				if(id == null)
					return true;

				final String name = tag.getString("name");
				if(name == null)
					return true;
				
				final Byte temp = tag.getByte("type");
				if(temp == null)
					return true;
				final TagType type = TagType.getByOutId(temp);
				if(type == null)
					return true;

				if(TagsStorage.index(id, name, type))
					return true;

				container.addLast(id);
			}
		} catch(final Exception e) {
			return true;
		}

		return false;
	}
}