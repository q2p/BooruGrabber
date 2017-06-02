package q2p.boorugrabber.storage;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Checksum {
	public static final byte[] generate(final InputStream is) throws Exception {
		final byte[] buffer = new byte[1024];
		final MessageDigest complete = MessageDigest.getInstance("SHA1");
		int numRead;
		do {
			numRead = is.read(buffer);
			if(numRead > 0)
				complete.update(buffer, 0, numRead);
		} while (numRead != -1);
		return complete.digest();
	}
	public static final byte[] generate(final byte[] data) {
		final MessageDigest complete;
		try {
			complete = MessageDigest.getInstance("SHA1");
		} catch(final NoSuchAlgorithmException e) {
			return null;
		}
		complete.update(data);

		return complete.digest();
	}
}