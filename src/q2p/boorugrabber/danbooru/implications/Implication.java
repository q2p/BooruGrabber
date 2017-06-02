package q2p.boorugrabber.danbooru.implications;

import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.versions.Version;

final class Implication extends Version<Implication> {
	final int requires;
	final boolean approved;
	
	protected final boolean same(final Implication frame) {
		return frame.requires == frame.requires;
	}
	protected void write(DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		
	}
}