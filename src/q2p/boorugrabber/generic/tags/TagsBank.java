package q2p.boorugrabber.generic.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.versions.IndexableVersionsBank;

public final class TagsBank extends IndexableVersionsBank<TagVersion, Integer> {
	protected TagsBank(final String path) {
		super(path, "tags");
	}
	protected final TagVersion getFrame(final DataInputStream dis) throws Exception {
		return new TagVersion(dis);
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