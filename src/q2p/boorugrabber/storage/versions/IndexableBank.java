package q2p.boorugrabber.storage.versions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import q2p.boorugrabber.storage.Token;
import q2p.boorugrabber.storage.Tokenizer;

public abstract class IndexableBank<Identifier> {
	private final StorageToken token = new StorageToken();
	private final LinkedList<Identifier> storage = new LinkedList<Identifier>();

	private final String path;
	private final String name;
	protected IndexableBank(final String path, final String name) {
		this.path = path;
		this.name = name;
	}
	public final void load() {
		Tokenizer.safeRead(path, name, token);
	}
	public final void save() {
		Tokenizer.safeWrite(path, name, token);
	}
	
	public synchronized final int index(final Identifier id) {
		int i = 0;
		for(final Identifier sid : storage) {
			if(sameIdentifier(sid, id))
				return i;
			
			i++;
		}
		storage.addLast(id);
		
		return i;
	}
	
	protected abstract boolean sameIdentifier(final Identifier id1, final Identifier id2);
	protected abstract Identifier readIdentifier(final DataInputStream dis) throws Exception;
	protected abstract void writeIdentifier(final DataOutputStream dos, final Identifier id) throws IOException;
	private final class StorageToken implements Token {
		public final void generate() {
			storage.clear();
		}
		
		public final void read(final DataInputStream dis) throws Exception {
			storage.clear();
			for(int i = dis.readInt(); i != 0; i--)
				storage.addLast(readIdentifier(dis));
		}
		public final void write(final DataOutputStream dos) throws IOException {
			dos.writeInt(storage.size());
			while(!storage.isEmpty())
				writeIdentifier(dos, storage.removeFirst());
		}
	}
}