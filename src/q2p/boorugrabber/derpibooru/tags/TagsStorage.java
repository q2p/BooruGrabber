package q2p.boorugrabber.derpibooru.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.derpibooru.Derpibooru;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.versions.IndexableVersionsBank;

public final class TagsStorage extends IndexableVersionsBank<TagVersion, String> {
	public TagsStorage() {
		super(Derpibooru.pathMain, "tags");
	}
	protected final TagVersion getFrame(final DataInputStream dis) throws Exception {
		return new TagVersion(dis);
	}
	protected final String readIdentifier(final DataInputStream dis) throws Exception {
		return Storage.readString(dis);
	}
	protected final boolean sameIdentifier(final String name1, final String name2) {
		return name1.equals(name2);
	}
	protected final void writeIdentifier(final DataOutputStream dos, final String name) throws IOException {
		Storage.writeString(dos, name);
	}
}