package q2p.boorugrabber.derpibooru.posts.comments;

import q2p.boorugrabber.queue.segmented.SegmentedTask;

final class CommentTask extends SegmentedTask<Comment> {
	private final int postId;

	CommentTask(final CommentsCombiner comments, final int id) {
		super(comments, 1);
		postId = id;
	}
	
	protected final CommentsInstance getFirstInstance(final int page) {
		return new CommentsInstance(postId, page);
	}
}