package q2p.boorugrabber.queue.segmented;

import java.util.LinkedList;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.queue.Task;

public abstract class SegmentedTask<T extends Segment> implements Task {
	private final SegmentsCombiner<T> combiner;
	private int page;

	protected SegmentedTask(final SegmentsCombiner<T> combiner, final int startPage) {
		this.combiner = combiner;
		page = startPage;
	}
	
	public final LinkedList<Task> work() {
		final SegmentInstance<T> instance = getFirstInstance(page);

		if(instance.receive()) {
			Log.out(combiner.onError());
			return null;
		}

		final LinkedList<Task> ret = new LinkedList<Task>();

		if(combiner.append(instance.segments)) {
			page++;
			ret.addLast(this);
		} else {
			final Task saveTask = combiner.push(combiner.segments);
			if(saveTask != null)
				ret.addLast(saveTask);
		}

		return ret;
	}
	
	protected abstract SegmentInstance<T> getFirstInstance(final int page);
}