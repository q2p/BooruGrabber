package q2p.boorugrabber.storage;

import java.io.DataInputStream;

public interface Temporary extends Writable {
	public void read(final DataInputStream dis) throws Exception;
}