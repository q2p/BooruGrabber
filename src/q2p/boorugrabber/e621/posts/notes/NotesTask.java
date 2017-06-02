package q2p.boorugrabber.e621.posts.notes;

import q2p.boorugrabber.queue.segmented.SegmentedTask;

final class NotesTask extends SegmentedTask<Note> {
	private final int id;

	NotesTask(final NotesCombiner notes, final int id) {
		super(notes, 1);
		this.id = id;
	}
	
	protected final NotesInstance getFirstInstance(final int page) {
		return new NotesInstance(id, page);
	}
}
