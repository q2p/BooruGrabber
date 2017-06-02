package q2p.boorugrabber.e621.posts.notes;

import java.util.Comparator;

final class NoteComparator implements Comparator<Note> {
	public final int compare(final Note f, final Note s) {
		if(f.id < s.id)
			return -1;

		if(f.id > s.id)
			return 1;

		return 0;
	}
}