package q2p.boorugrabber.paheal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.versions.IndexableBank;

final class TagsBank extends IndexableBank<String> {
	protected TagsBank() {
		super(Paheal.pathMain, "tags");
	}

	protected final boolean sameIdentifier(final String name1, final String name2) {
		return name1.equals(name2);
	}
	protected final String readIdentifier(final DataInputStream dis) throws Exception {
		return Storage.readString(dis);
	}
	protected final void writeIdentifier(final DataOutputStream dos, final String name) throws IOException {
		Storage.writeString(dos, name);
	}
}