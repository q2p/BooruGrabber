package q2p.boorugrabber.storage;

import java.util.LinkedList;
import q2p.boorugrabber.queue.Task;

public final class SaveTemporaryTask implements Task {
	private final IndexableTemporary temporary;
	public SaveTemporaryTask(final IndexableTemporary temporary) {
		this.temporary = temporary;
	}
	
	public final LinkedList<Task> work() {
		Tokenizer.flush(temporary.dirrectory, temporary.id, temporary);
		return new LinkedList<Task>();
	}
}
