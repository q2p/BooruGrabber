package q2p.boorugrabber.queue;

final class Worker implements Runnable {
	private Thread thread;

	private Block block;

	final void redirect(final Block block) {
		this.block = block;
	}

	public final void run() {
		block.work();
	}

	final void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	final void join() {
		try {
			thread.join();
		} catch(final InterruptedException e) {}
	}
}