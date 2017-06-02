package q2p.boorugrabber.e621.aliases;

import q2p.boorugrabber.e621.posts.tags.TagsStorage;
import q2p.boorugrabber.queue.UnpredictableBlock;
import q2p.boorugrabber.queue.UnpredictableTask;

public final class AliasesBlock extends UnpredictableBlock {
	public AliasesBlock() {
		super("Синонимы", 1, 1, (byte)16);
	}

	protected final UnpredictableTask getRegularTask(final int page) {
		return new AliasTask(page);
	}

	protected final void saveState() {
		TagsStorage.save();
		AliasStorage.save();
	}

	protected final void loadState() {
		TagsStorage.load();
		AliasStorage.load();
	}
}