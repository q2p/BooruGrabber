package q2p.boorugrabber.e621.pools;

import q2p.boorugrabber.queue.segmented.SegmentedTask;

final class PostsTask extends SegmentedTask<PoolPost> {
	private final int id;

	PostsTask(final PostsCombiner posts, final int id) {
		super(posts, 1);
		this.id = id;
	}
	
	protected final PostsInstance getFirstInstance(final int page) {
		return new PostsInstance(id, page);
	}
}