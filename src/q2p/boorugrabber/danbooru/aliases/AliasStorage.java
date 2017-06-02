package q2p.boorugrabber.danbooru.aliases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.storage.versions.HeapVersionsBank;

final class AliasStorage extends HeapVersionsBank<AliasFrame, Integer>{
	protected AliasStorage() {
		super(Danbooru.pathMain, "aliases");
	}

	protected final AliasFrame getFrame(final DataInputStream dis) throws Exception {
		return new AliasFrame(dis);
	}
	protected final boolean sameIdentifier(final Integer id1, final Integer id2) {
		return id1 == id2;
	}
	protected final Integer readIdentifier(final DataInputStream dis) throws Exception {
		return dis.readInt();
	}
	protected final void writeIdentifier(final DataOutputStream dos, final Integer from) throws IOException {
		dos.writeInt(from);
	}
}