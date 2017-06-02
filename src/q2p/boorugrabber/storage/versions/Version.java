package q2p.boorugrabber.storage.versions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.help.Assist;

public abstract class Version<V extends Version<V>> {
	private final long date;
	
	protected Version() {
		date = Assist.currentGMTmilisec();
	}
	
	protected abstract boolean same(final V version);
	
	Version(final DataInputStream dis) throws Exception {
		date = dis.readLong();
	}

	final void save(final DataOutputStream dos) throws IOException {
		dos.writeLong(date);
		write(dos);
	}
	protected abstract void write(final DataOutputStream dos) throws IOException;
}