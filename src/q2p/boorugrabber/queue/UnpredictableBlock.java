package q2p.boorugrabber.queue;

import java.util.LinkedList;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Log;

public abstract class UnpredictableBlock extends Block {
	private final int start;
	private final int step;

	final boolean work(final boolean updating) {
		if(skips == 0)
			return false;

		if(skip == 0)
			skip = skips;
		else {
			skip--;
			return false;
		}

		errorAbort = false;

		Log.out(name);

		loadState();

		Log.out("Состояние загружено");

		nextId = start;
		ended = false;

		hireWorkers();

		if(!errorAbort) {
			Log.out("Состояние сохраняется");

			saveState();
		}

		tasks.clear();

		return errorAbort;
	}

	public UnpredictableBlock(final String name, final int start, final int step, final byte skip) {
		super(name, false);
		this.start = start;
		this.step = step;
		skips = skip;
		this.skip = 0;
	}

	private boolean ended;

	private final byte skips;
	private byte skip;

	final void work() {
		UnpredictableTask uTask = null;
		Task task = null;
		int id = 0;
		while(true) {
			synchronized(LOCK) {
				if(errorAbort)
					return;
				
				if(!tasks.isEmpty()) {
					task = tasks.removeFirst();
					tookers++;
				} else if(!ended) {
					id = nextId;

					uTask = getRegularTask(id);

					tookers++;

					nextId += step;
				} else if(tookers != 0) {
					try {
						LOCK.wait();
					} catch(final InterruptedException e) {}
					continue;
				} else
					return;
			}

			if(task != null) {
				final LinkedList<Task> newTasks = task.work();

				task = null;

				synchronized(LOCK) {
					if(newTasks == null) {
						errorAbort = true;
						skip = 0;
					} else
						Assist.drain(tasks, newTasks);

					tookers--;
					LOCK.notifyAll();
				}
			} else {
				final UnpredictableAnswer answer = uTask.work();

				uTask = null;

				synchronized(LOCK) {
					if(answer == null)
						errorAbort = true;
					else {
						Assist.drain(tasks, answer.tasks);

						if(answer.hasNoMore)
							ended = true;
					}

					tookers--;
					LOCK.notifyAll();
				}
			}
		}
	}
	
	protected abstract UnpredictableTask getRegularTask(final int id);
	protected abstract void saveState();
	protected abstract void loadState();
}