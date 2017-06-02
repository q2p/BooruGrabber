package q2p.boorugrabber.queue.segmented;

import java.util.LinkedList;
import q2p.boorugrabber.queue.Task;

public abstract class SegmentsWaiter<T extends Segment> {
	abstract Task append(final LinkedList<Segment> segments);
}