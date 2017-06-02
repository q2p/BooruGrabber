package q2p.boorugrabber.derpibooru.pools;

import q2p.boorugrabber.queue.segmented.SegmentedTask;

final class PostsTask extends SegmentedTask<PoolPost> {
	private final String url;

	PostsTask(final PostsCombiner posts, final String url) {
		super(posts, 1);
		this.url = url;
	}
	
	protected final PostsInstance getFirstInstance(final int page) {
		return new PostsInstance(url, page);
	}
}