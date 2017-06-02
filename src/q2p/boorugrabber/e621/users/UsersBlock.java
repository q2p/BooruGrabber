package q2p.boorugrabber.e621.users;

import q2p.boorugrabber.queue.UnpredictableBlock;
import q2p.boorugrabber.queue.UnpredictableTask;

public final class UsersBlock extends UnpredictableBlock {
	public UsersBlock() {
		super("Пользователи", 1, 1, (byte)128);
	}

	protected final UnpredictableTask getRegularTask(final int page) {
		return new UsersTask(page);
	}

	protected final void saveState() {}

	protected final void loadState() {}
}