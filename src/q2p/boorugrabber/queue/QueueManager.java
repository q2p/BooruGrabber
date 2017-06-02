package q2p.boorugrabber.queue;

import q2p.boorugrabber.global.BooruGrabber;

public final class QueueManager implements Runnable {
	private static final BlockManager[] managers;

	static {
		managers = new BlockManager[BooruGrabber.queues.length];
		for(int i = BooruGrabber.queues.length-1; i != -1; i--)
			managers[i] = BooruGrabber.queues[i].manager;
	}

	public static final void init() {
		new Thread(new QueueManager()).start();
	}

	public final void run() {
		while(true)
			for(final BlockManager manager : managers)
				manager.work();
	}
}