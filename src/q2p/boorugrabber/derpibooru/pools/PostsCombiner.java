package q2p.boorugrabber.derpibooru.pools;

import java.util.LinkedList;
import q2p.boorugrabber.queue.segmented.SegmentsCombiner;
import q2p.boorugrabber.storage.SaveTemporaryTask;

public final class PostsCombiner extends SegmentsCombiner<PoolPost> {
	private final PoolInstance pool;

	public PostsCombiner(final PoolInstance pool) {
		this.pool = pool;
	}

	public final PostsTask getTask(final String url) {
		return new PostsTask(this, url);
	}

	protected final String onError() {
		return "Не удалось обработать посты в пуле #" + pool.id;
	}

	protected final SaveTemporaryTask push(final LinkedList<PoolPost> posts) {
		return pool.append(posts);
	}
}