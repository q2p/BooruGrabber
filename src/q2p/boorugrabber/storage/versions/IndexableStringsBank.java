package q2p.boorugrabber.storage.versions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.Storage;

public abstract class IndexableStringsBank<V extends Version<V>> extends IndexableVersionsBank<V, String> {
	protected IndexableStringsBank(final String path, final String name) {
		super(path, name);
	}

	protected final boolean sameIdentifier(final String id1, final String id2) {
		return id1.equals(id2);
	}
	protected final String readIdentifier(final DataInputStream dis) throws Exception {
		return Storage.readString(dis);
	}
	protected final void writeIdentifier(final DataOutputStream dos, final String id) throws IOException {
		Storage.writeString(dos, id);
	}
}