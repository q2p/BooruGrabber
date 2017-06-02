package q2p.boorugrabber.storage.versions;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

final class Versions<V extends Version<V>, Identifier> {
	final Identifier id;
	final LinkedList<V> versions;

	final void index(final V version) {
		if(versions.isEmpty())
			versions.addLast(version);
		else {
			if(!versions.getLast().same(version))
				versions.addLast(version);
		}
	}
	
	Versions(final Identifier id) {
		this.id = id;
		versions = new LinkedList<V>();
	}
	Versions(final Identifier id, final LinkedList<V> versions) {
		this.id = id;
		this.versions = versions;
	}
	Versions(final Identifier id, final V version) {
		this.id = id;
		versions = new LinkedList<V>();
		versions.addLast(version);
	}
	
	final void write(final DataOutputStream dos) throws IOException {
		dos.writeInt(versions.size());
		while(!versions.isEmpty())
			versions.removeFirst().save(dos);
	}
}