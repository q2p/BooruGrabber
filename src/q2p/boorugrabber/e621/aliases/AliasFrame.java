package q2p.boorugrabber.e621.aliases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.versions.Version;

public final class AliasFrame extends Version<AliasFrame> {
	private final int to;
	private final boolean approved;
	
	public AliasFrame(final DataInputStream dis) throws IOException {
		to = dis.readInt();
		approved = dis.readBoolean();
	}
	protected final boolean same(final AliasFrame frame) {
		return frame.to == to && frame.approved == approved;
	}
	protected void write(final DataOutputStream dos) throws IOException {
		dos.writeInt(to);
		dos.writeBoolean(approved);
		dos.flush();
	}
}