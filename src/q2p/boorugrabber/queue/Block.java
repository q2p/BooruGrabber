package q2p.boorugrabber.queue;

import java.util.LinkedList;

public abstract class Block {
	// TODO: загружать кол-во потоков из конфига
	private static final byte threads = 16; // TODO: 8
	private static final Worker[] workers = new Worker[threads];
	static {
		for(int i = workers.length-1; i != -1; i--)
			workers[i] = new Worker();
	}

	protected final String name;
	
	protected int nextId;

	protected final LinkedList<Task> tasks = new LinkedList<Task>();

	abstract boolean work(final boolean updating);
	
	protected boolean updating;
	final boolean supportUpdates;

	protected Block(final String name, final boolean supporUpdates) {
		this.name = name;
		supportUpdates = supporUpdates;
	}
	
	protected boolean errorAbort;
	protected final Object LOCK = new Object();
	protected int tookers = 0;

	abstract void work();

	protected final void hireWorkers() {
		for(final Worker worker : workers)
			worker.redirect(this);

		for(final Worker worker : workers)
			worker.start();

		for(final Worker worker : workers)
			worker.join();
	}
}