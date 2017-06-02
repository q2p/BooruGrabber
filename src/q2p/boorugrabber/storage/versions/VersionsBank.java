package q2p.boorugrabber.storage.versions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import q2p.boorugrabber.storage.Token;
import q2p.boorugrabber.storage.Tokenizer;

abstract class VersionsBank<V extends Version<V>, Identifier> {
	private final StorageToken token = new StorageToken();
	final LinkedList<Versions<V, Identifier>> bank = new LinkedList<Versions<V, Identifier>>();
	private final String path;
	private final String name;
	protected VersionsBank(final String path, final String name) {
		this.path = path;
		this.name = name;
	}
	
	public final void load() {
		Tokenizer.safeRead(path, name, token);
	}

	public final void save() {
		Tokenizer.safeWrite(path, name, token);
	}

	protected abstract V getFrame(final DataInputStream dis) throws Exception;
	protected abstract boolean sameIdentifier(final Identifier id1, final Identifier id2);
	protected abstract Identifier readIdentifier(final DataInputStream dis) throws Exception;
	protected abstract void writeIdentifier(final DataOutputStream dos, final Identifier id) throws IOException;
	private final class StorageToken implements Token {
		public final void generate() {
			bank.clear();
		}
		
		public final void read(final DataInputStream dis) throws Exception {
			bank.clear();
			for(int i = dis.readInt(); i != 0; i--) {
				final Identifier id = readIdentifier(dis);
				final LinkedList<V> versions = new LinkedList<V>();
				for(int j = dis.readInt(); j != 0; j--)
					versions.addLast(getFrame(dis));
				
				bank.addLast(new Versions<V, Identifier>(id, versions));
			}
		}
		
		public final void write(final DataOutputStream dos) throws IOException {
			dos.writeInt(bank.size());
			while(!bank.isEmpty()) {
				final Versions<V, Identifier> versions = bank.removeFirst();
				writeIdentifier(dos, versions.id);
				versions.write(dos);
			}
		}
	}
}