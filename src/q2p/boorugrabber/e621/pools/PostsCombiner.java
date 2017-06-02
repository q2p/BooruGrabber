package q2p.boorugrabber.e621.pools;

import java.util.LinkedList;
import q2p.boorugrabber.queue.segmented.SegmentsCombiner;
import q2p.boorugrabber.storage.SaveTemporaryTask;

public final class PostsCombiner extends SegmentsCombiner<PoolPost> {
	final PoolInstance pool;

	public PostsCombiner(final PoolInstance pool) {
		this.pool = pool;
	}
	
	public final PostsTask getTask() {
		return new PostsTask(this, pool.id);
	}
	
	protected final String onError() {
		return "Не удалось обработать посты в пуле #"+ pool.id;
	}
	
	protected final SaveTemporaryTask push(final LinkedList<PoolPost> posts) {
		return pool.append(posts);
	}
}