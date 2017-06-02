package q2p.boorugrabber.e621.pools;

import java.util.LinkedList;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.queue.Task;

final class PoolTask implements Task {
	public final PoolInstance pool;

	PoolTask(final int id) {
		pool = new PoolInstance(id);
	}

	public final LinkedList<Task> work() {
		Log.lapse("Пул #", 50, pool.id);
		final LinkedList<Task> ret = pool.receive();
		if(ret == null)
			Log.out("Не удалось обработать пул #"+pool.id);

		return ret;
	}
}