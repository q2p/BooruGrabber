package q2p.boorugrabber.generic.pools;

import java.io.File;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.queue.IndexBlock;

public abstract class PoolsBlock extends IndexBlock {
	protected GenericBooru booru;
	
	protected File stateFile;
	public PoolsBlock(final int indexingLimit, final int updatingLimit) {
		super("Пулы", indexingLimit, updatingLimit);
	}
	public final void init(final GenericBooru booru) {
		this.booru = booru;
		stateFile = new File(booru.pathPools + "state");
	}
	
	protected abstract PoolInstance getRegularTask(final int id);
}