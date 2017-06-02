package q2p.boorugrabber.storage;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Writable {
	public void write(final DataOutputStream dos) throws IOException;
}