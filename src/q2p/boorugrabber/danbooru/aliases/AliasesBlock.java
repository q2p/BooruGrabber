package q2p.boorugrabber.danbooru.aliases;

import q2p.boorugrabber.e621.posts.tags.TagsStorage;
import q2p.boorugrabber.queue.UnpredictableBlock;

public final class AliasesBlock extends UnpredictableBlock {
	public AliasesBlock() {
		super("Синонимы", 1, 1, (byte)16);
	}

	protected final AliasTask getRegularTask(final int page) {
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