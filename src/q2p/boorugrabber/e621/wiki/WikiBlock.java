package q2p.boorugrabber.e621.wiki;

import q2p.boorugrabber.queue.UnpredictableBlock;
import q2p.boorugrabber.queue.UnpredictableTask;

public final class WikiBlock extends UnpredictableBlock {
	public WikiBlock() {
		super("Wiki", 1, 1, (byte)16);
	}

	protected final UnpredictableTask getRegularTask(final int page) {
		return new WikiTask(page);
	}

	protected final void saveState() {}

	protected final void loadState() {}
}