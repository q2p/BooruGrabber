package q2p.boorugrabber.generic.aliases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import q2p.boorugrabber.storage.Token;
import q2p.boorugrabber.storage.Tokenizer;

final class AliasesStorage {
	private static final AliasToken token = new AliasToken();

	private static final LinkedList<AliasVersion> aliasStorage = new LinkedList<AliasVersion>();

	static final void load(final String path) {
		Tokenizer.safeRead(path, "aliases", token);
	}

	static final void save(final String path) {
		Tokenizer.safeWrite(path, "aliases", token);
	}
	
	static synchronized void index(final int from, final int to, final boolean approved, final String reason) {
		for(final AliasVersion alias : aliasStorage)
			if(alias.from == from) {
				alias.index(to, approved, reason);

				return;
			}

		aliasStorage.addLast(new AliasVersion(from, to, approved, reason));
	}
	
	private static final class AliasToken implements Token {
		public final void generate() {
			aliasStorage.clear();
		}
		
		public final void read(final DataInputStream dis) throws Exception {
			aliasStorage.clear();

			for(int i = dis.readInt(); i != 0; i--)
				aliasStorage.addLast(new AliasVersion(dis));
		}
		
		public final void write(final DataOutputStream dos) throws IOException {
			dos.writeInt(aliasStorage.size());

			while(!aliasStorage.isEmpty())
				aliasStorage.removeFirst().write(dos);
		}
	}
}