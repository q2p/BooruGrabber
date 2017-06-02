package q2p.boorugrabber.danbooru.posts;

import java.util.LinkedList;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.queue.Task;

final class PostTask implements Task {
	public final PostInstance post;

	PostTask(final int id) {
		post = new PostInstance(id);
	}

	public final LinkedList<Task> work() {
		Log.lapse("Пост #", 50, post.id);
		final LinkedList<Task> ret = post.receive();
		if(ret == null)
			Log.out("Не удалось обработать пост #"+post.id);

		return ret;
	}
}