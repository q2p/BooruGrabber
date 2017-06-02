package q2p.boorugrabber.generic.aliases;

import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.generic.tags.TagsBank;
import q2p.boorugrabber.queue.UnpredictableBlock;
import q2p.boorugrabber.queue.UnpredictableTask;

public final class AliasesBlock extends UnpredictableBlock {
	private final GenericBooru booru;

	public AliasesBlock(final GenericBooru booru, final byte skip) {
		super("Синонимы", 0, 50, skip);
		this.booru = booru;
	}

	protected final UnpredictableTask getRegularTask(final int page) {
		return new AliasTask(booru, page);
	}

	protected final void saveState() {
		TagsBank.save(booru.pathMain);
		AliasesStorage.save(booru.pathMain);
	}

	protected final void loadState() {
		TagsBank.load(booru.pathMain);
		AliasesStorage.load(booru.pathMain);
	}
}