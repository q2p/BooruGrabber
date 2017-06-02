package q2p.boorugrabber.e621.posts.comments;

import java.util.LinkedList;
import q2p.boorugrabber.e621.posts.PostInstance;
import q2p.boorugrabber.queue.segmented.SegmentsCombiner;
import q2p.boorugrabber.storage.SaveTemporaryTask;

public final class CommentsCombiner extends SegmentsCombiner<Comment> {
	final PostInstance post;

	public CommentsCombiner(final PostInstance post) {
		super(new CommentComparator());
		this.post = post;
	}
	
	public final CommentTask getTask() {
		return new CommentTask(this, post.id);
	}
	
	protected final String onError() {
		return "Не удалось обработать комментарии под постом #"+post.id;
	}
	
	protected final SaveTemporaryTask push(final LinkedList<Comment> comments) {
		return post.appendComments(comments);
	}
}