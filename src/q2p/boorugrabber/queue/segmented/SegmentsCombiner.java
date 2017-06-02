package q2p.boorugrabber.queue.segmented;

import java.util.Comparator;
import java.util.LinkedList;
import q2p.boorugrabber.queue.Task;

public abstract class SegmentsCombiner<T extends Segment> {
	final LinkedList<T> segments = new LinkedList<T>();

	final Comparator<T> comparator;

	protected SegmentsCombiner() {
		comparator = null;
	}

	protected SegmentsCombiner(final Comparator<T> comparator) {
		this.comparator = comparator;
	}

	protected abstract String onError();

	final boolean append(final LinkedList<T> segments) {
		if(segments.isEmpty()) {
			if(comparator != null)
				this.segments.sort(comparator);

			return false;
		}

		loop:
			while(!segments.isEmpty()) {
				final T passed = segments.removeFirst();

				for(final T stored : this.segments)
					if(passed.copyOf(stored))
						continue loop;

				this.segments.addLast(passed);
			}

		return true;
	}
	
	protected abstract Task push(final LinkedList<T> segments);
}