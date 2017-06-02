package q2p.boorugrabber.e621.implications;

import q2p.boorugrabber.e621.posts.tags.TagsStorage;
import q2p.boorugrabber.queue.UnpredictableBlock;
import q2p.boorugrabber.queue.UnpredictableTask;

public final class ImplicationsBlock extends UnpredictableBlock {
	public ImplicationsBlock() {
		super("Требования", 1, 1, (byte)16);
	}

	protected final UnpredictableTask getRegularTask(final int page) {
		return new ImplicationsTask(page);
	}

	protected final void saveState() {
		TagsStorage.save();
		ImplicationStorage.save();
	}

	protected final void loadState() {
		TagsStorage.load();
		ImplicationStorage.load();
	}
}