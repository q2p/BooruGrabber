package q2p.boorugrabber.queue.segmented;

import java.util.LinkedList;

public abstract class SegmentInstance<T extends Segment> {
	protected final int page;

	protected final LinkedList<T> segments = new LinkedList<T>();

	protected SegmentInstance(final int page) {
		this.page = page;
	}
	
	protected abstract boolean receive();
}