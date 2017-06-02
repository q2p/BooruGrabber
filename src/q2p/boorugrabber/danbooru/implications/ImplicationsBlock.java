package q2p.boorugrabber.danbooru.implications;

import q2p.boorugrabber.e621.posts.tags.TagsStorage;
import q2p.boorugrabber.queue.UnpredictableBlock;

public final class ImplicationsBlock extends UnpredictableBlock {
	public ImplicationsBlock() {
		super("Требования", 1, 1, (byte)16);
	}

	protected final ImplicationsTask getRegularTask(final int page) {
		return new ImplicationsTask(page);
	}

	protected final void saveState() {
		TagsStorage.save();
		ImplicationsStorage.save();
	}

	protected final void loadState() {
		TagsStorage.load();
		ImplicationsStorage.load();
	}
}