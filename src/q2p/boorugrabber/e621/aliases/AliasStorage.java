package q2p.boorugrabber.e621.aliases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.versions.HeapVersionsBank;

final class AliasStorage extends HeapVersionsBank<AliasFrame, String> {
	protected AliasStorage() {
		super(E621.pathMain, "aliases");
	}
	protected final AliasFrame getFrame(final DataInputStream dis) throws Exception {
		return new AliasFrame(dis);
	}	
	protected final String readIdentifier(final DataInputStream dis) throws Exception {
		return Storage.readString(dis);
	}
	protected final boolean sameIdentifier(final String from1, final String from2) {
		return from1.equals(from2);
	}
	protected final void writeIdentifier(final DataOutputStream dos, final String from) throws IOException {
		Storage.writeString(dos, from);
	}
}