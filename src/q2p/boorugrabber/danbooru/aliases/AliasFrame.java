package q2p.boorugrabber.danbooru.aliases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.versions.Version;

final class AliasFrame extends Version<AliasFrame> {
	private final int to;
	private final boolean approved;
	private final String reason;

	AliasFrame(final int to, final boolean approved, final String reason) {
		this.to = to;
		this.approved = approved;
		this.reason = reason;
	}
	AliasFrame(final DataInputStream dis) throws Exception {
		to = dis.readInt();
		approved = dis.readBoolean();
		reason = Storage.readString(dis);
	}
	protected final boolean same(final AliasFrame frame) {
		return frame.to == to && frame.approved == approved && reason.equals(reason);
	}
	protected final void write(final DataOutputStream dos) throws IOException {
		dos.writeInt(to);
		dos.writeBoolean(approved);
		Storage.writeString(dos, reason);
		dos.flush();
	}
}