package q2p.boorugrabber.derpibooru.tags;

import q2p.boorugrabber.queue.UnpredictableBlock;
import q2p.boorugrabber.queue.UnpredictableTask;

public final class TagsBlock extends UnpredictableBlock {
	public TagsBlock() {
		super("Тэги", 1, 1, (byte)16);
	}

	protected final UnpredictableTask getRegularTask(final int page) {
		return new TagsTask(page);
	}

	protected final void saveState() {}

	protected final void loadState() {}
}