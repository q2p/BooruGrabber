package q2p.boorugrabber.derpibooru.posts.comments;

import java.util.Comparator;

final class CommentComparator implements Comparator<Comment> {
	public final int compare(final Comment f, final Comment s) {
		if(f.id < s.id)
			return -1;

		if(f.id > s.id)
			return 1;

		return 0;
	}
}
