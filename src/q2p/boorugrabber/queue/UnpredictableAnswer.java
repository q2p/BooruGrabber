package q2p.boorugrabber.queue;

import java.util.LinkedList;

public final class UnpredictableAnswer {
	final LinkedList<Task> tasks = new LinkedList<Task>();

	public final void task(final Task task) {
		tasks.addLast(task);
	}
	
	boolean hasNoMore = false;
	public final UnpredictableAnswer hasNoMore() {
		hasNoMore = true;
		return this;
	}
}