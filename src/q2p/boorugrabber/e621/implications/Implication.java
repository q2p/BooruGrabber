package q2p.boorugrabber.e621.implications;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.Writable;

final class Implication implements Writable {
	final int tag;
	final int requires;
	boolean approved;

	Implication(final int tag, final int requires, final boolean approved) {
		this.tag = tag;
		this.requires = requires;
		this.approved = approved;
	}
	
	Implication(final DataInputStream dis) throws Exception {
		tag = dis.readInt();
		requires = dis.readInt();
		approved = dis.readBoolean();
	}

	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeInt(tag);
		dos.writeInt(requires);
		dos.writeBoolean(approved);
		dos.flush();
	}
}