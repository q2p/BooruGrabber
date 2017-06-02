package q2p.boorugrabber.danbooru.wiki;

import q2p.boorugrabber.queue.UnpredictableBlock;

public final class WikiBlock extends UnpredictableBlock {
	public WikiBlock() {
		super("Wiki", 1, 1, (byte)16);
	}

	protected final WikiTask getRegularTask(final int page) {
		return new WikiTask(page);
	}

	protected final void saveState() {}

	protected final void loadState() {}
}