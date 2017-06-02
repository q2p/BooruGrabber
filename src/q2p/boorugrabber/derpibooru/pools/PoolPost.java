package q2p.boorugrabber.derpibooru.pools;

import q2p.boorugrabber.queue.segmented.Segment;

final class PoolPost implements Segment {
	final int id;

	PoolPost(final int id) {
		this.id = id;
	}

	public final boolean copyOf(final Segment stored) {
		return stored instanceof PoolPost && ((PoolPost) stored).id == id;
	}
}