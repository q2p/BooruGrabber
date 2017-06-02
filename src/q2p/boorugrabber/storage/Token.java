package q2p.boorugrabber.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Token {
	void generate();
	void read(final DataInputStream dis) throws Exception;
	void write(final DataOutputStream dos) throws IOException;
}