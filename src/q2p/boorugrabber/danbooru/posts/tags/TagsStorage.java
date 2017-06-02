package q2p.boorugrabber.danbooru.posts.tags;

import java.io.DataInputStream;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.storage.versions.IndexableStringsBank;

public final class TagsStorage extends IndexableStringsBank<TagFrame> {
	public TagsStorage() {
		super(Danbooru.pathMain, "tags");
	}

	protected final TagFrame getFrame(final DataInputStream dis) throws Exception {
		return new TagFrame(dis);
	}
}