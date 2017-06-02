package q2p.boorugrabber.network;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public final class Response {
	public final short code;
	public final long length;
	public final String redirected;
	public final byte[] data;

	Response(final short responseCode, final long contentLength, final String redirected, final ByteArrayOutputStream stream) {
		code = responseCode;
		length = contentLength;
		this.redirected = redirected;
		data = stream.toByteArray();
	}

	public final String toString() {
		return new String(data, StandardCharsets.UTF_8);
	}
}