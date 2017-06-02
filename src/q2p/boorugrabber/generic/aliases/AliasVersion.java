package q2p.boorugrabber.generic.aliases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.storage.Writable;

final class AliasVersion implements Writable {
	final int from;
	private final LinkedList<Integer> versionsTo = new LinkedList<Integer>();
	private final LinkedList<Boolean> versionsApproved = new LinkedList<Boolean>();
	private final LinkedList<String> versionsReason = new LinkedList<String>();
	private final LinkedList<Long> versionsDates = new LinkedList<Long>();

	AliasVersion(final int from, final int to, final boolean approved, final String reason) {
		this.from = from;
		versionsTo.addLast(to);
		versionsApproved.addLast(approved);
		versionsReason.addLast(reason);
		versionsDates.addLast(Assist.currentGMTmilisec());
	}
	
	final void index(final int to, final boolean approved, final String reason) {
		if(to == versionsTo.getLast())
			versionsApproved.set(versionsApproved.size()-1, approved || versionsApproved.getLast());
		else {
			versionsTo.addLast(to);
			versionsApproved.addLast(approved);
			versionsReason.addLast(reason);
			versionsDates.addLast(Assist.currentGMTmilisec());
		}
	}
	
	AliasVersion(final DataInputStream dis) throws Exception {
		from = dis.readInt();
		for(int i = dis.readInt(); i != 0; i--) {
			versionsTo.addLast(dis.readInt());
			versionsApproved.addLast(dis.readBoolean());
			versionsDates.addLast(dis.readLong());
		}
	}

	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeInt(from);
		dos.writeInt(versionsTo.size());

		while(!versionsTo.isEmpty()) {
			dos.writeInt(versionsTo.removeFirst());
			dos.writeBoolean(versionsApproved.removeFirst());
			dos.writeLong(versionsDates.removeFirst());
		}
		dos.flush();
	}
}