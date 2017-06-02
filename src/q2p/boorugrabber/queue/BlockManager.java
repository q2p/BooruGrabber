package q2p.boorugrabber.queue;

import q2p.boorugrabber.help.Log;

public final class BlockManager {
	private final String name;

	public Block[] blocks = null;

	public BlockManager(final String name) {
		this.name = name;
	}

	final boolean work() {
		// TODO: что делать при ошибке?
		Log.out(name);
		if(blocks == null) {
			Log.out("Не объявленны блоки");
			return true;
		}
		Log.out("Индексация");
		for(final Block block : blocks)
			if(block.work(false))
				return true;
		Log.out("Обновление");
		for(final Block block : blocks)
			if(block.supportUpdates && block.work(true)) return true;

		return false;
	}
}