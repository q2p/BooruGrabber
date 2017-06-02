package q2p.boorugrabber.danbooru.pools;

import q2p.boorugrabber.queue.UnpredictableBlock;

public final class PoolsBlock extends UnpredictableBlock {
	public PoolsBlock() {
		super("Пулы", 1, 1, (byte)16);
	}

	protected final PoolsTask getRegularTask(final int id) {
		return new PoolsTask(id);
	}
	
	protected final void saveState() {}
	
	protected final void loadState() {}
}