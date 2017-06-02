package q2p.boorugrabber.e621.authors;

import q2p.boorugrabber.queue.UnpredictableBlock;
import q2p.boorugrabber.queue.UnpredictableTask;

public final class AuthorsBlock extends UnpredictableBlock {
	public AuthorsBlock() {
		super("Авторы", 1, 1, (byte)16);
	}

	protected final UnpredictableTask getRegularTask(final int page) {
		return new AuthorsTask(page);
	}

	protected final void saveState() {}

	protected final void loadState() {}
}