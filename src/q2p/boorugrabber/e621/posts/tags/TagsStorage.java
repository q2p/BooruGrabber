package q2p.boorugrabber.e621.posts.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.storage.versions.IndexableVersionsBank;

public final class TagsStorage extends IndexableVersionsBank<TagFrame, Integer> {
	protected TagsStorage() {
		super(E621.pathMain, "tags");
	}
	protected final TagFrame getFrame(final DataInputStream dis) throws Exception {
		return new TagFrame(dis);
	}
	protected final boolean sameIdentifier(final Integer id1, final Integer id2) {
		return id1 == id2;
	}
	protected final Integer readIdentifier(final DataInputStream dis) throws Exception {
		return dis.readInt();
	}
	protected final void writeIdentifier(final DataOutputStream dos, final Integer id) throws IOException {
		dos.writeInt(id);
	}
}