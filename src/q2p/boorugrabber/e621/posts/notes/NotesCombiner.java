package q2p.boorugrabber.e621.posts.notes;

import java.util.LinkedList;
import q2p.boorugrabber.e621.posts.PostInstance;
import q2p.boorugrabber.queue.segmented.SegmentsCombiner;
import q2p.boorugrabber.storage.SaveTemporaryTask;

public final class NotesCombiner extends SegmentsCombiner<Note> {
	final PostInstance post;

	public NotesCombiner(final PostInstance post) {
		super(new NoteComparator());
		this.post = post;
	}
	
	public final NotesTask getTask() {
		return new NotesTask(this, post.id);
	}
	
	protected final String onError() {
		return "Не удалось обработать заметки к посту #"+post.id;
	}
	
	protected final SaveTemporaryTask push(final LinkedList<Note> notes) {
		return post.appendNotes(notes);
	}
}