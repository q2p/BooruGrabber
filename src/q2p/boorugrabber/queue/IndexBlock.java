package q2p.boorugrabber.queue;

import java.util.LinkedList;
import q2p.boorugrabber.help.Log;

public abstract class IndexBlock extends Block {
	private final int indexingLimit;
	private final int updatingLimit;

	private int left;
	private int cap;

	final boolean work(final boolean updating) {
		if(updating) {
			if(updatingLimit == 0)
				return false;
		} else if(indexingLimit == 0)
			return false;

		this.updating = updating;
		
		errorAbort = false;

		Log.out(name);

		final int[] state = getState();

		Log.out("Состояние загружено");

		if(updating) {
			nextId = state[1];
			cap = state[0];
			left = Math.min(cap, updatingLimit);
		} else {
			nextId = state[0];
			final Integer temp = getLastId();
			if(temp == null) {
				Log.out("Не удалось получить последний индекс");
				return true;
			}
			cap = temp;
			left = Math.min(1+cap-nextId, indexingLimit);
		}
		
		hireWorkers();

		if(!errorAbort) {
			Log.out("Состояние сохраняется");

			if(updating)
				saveState(state[0], nextId);
			else
				saveState(nextId, state[1]);

			Log.out("Состояние сохранено");
		}

		tasks.clear();

		return errorAbort;
	}

	public IndexBlock(final String name, final int indexingLimit, final int updatingLimit) {
		super(name, updatingLimit != 0);
		this.indexingLimit = indexingLimit;
		this.updatingLimit = updatingLimit;
	}

	final void work() {
		Task task;
		while(true) {
			synchronized(LOCK) {
				if(errorAbort)
					return;
				
				if(!tasks.isEmpty()) {
					task = tasks.removeFirst();
					tookers++;
				} else if(left != 0) {
					if(updating) {
						task = getRegularTask(nextId);
						tookers++;
						nextId++;
						if(nextId > cap)
							nextId = 1;
						left--;
					} else {
						task = getRegularTask(nextId);
						tookers++;
						nextId++;
						left--;
					}
				} else if(tookers != 0) {
					try {
						LOCK.wait();
					} catch(final InterruptedException e) {}
					continue;
				} else
					return;
			}

			final LinkedList<Task> newTasks = task.work();

			synchronized(LOCK) {
				if(newTasks == null) errorAbort = true;
				else
					while(!newTasks.isEmpty())
						tasks.addLast(newTasks.removeFirst());
				tookers--;
				LOCK.notifyAll();
			}
		}
	}
	
	protected abstract Task getRegularTask(final int id);
	protected abstract void saveState(final int indexing, final int updating);
	protected abstract int[] getState();
	protected abstract Integer getLastId();
}