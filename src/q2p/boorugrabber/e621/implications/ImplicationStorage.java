package q2p.boorugrabber.e621.implications;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.storage.Token;
import q2p.boorugrabber.storage.Tokenizer;

final class ImplicationStorage {
	private static final ImplicationToken token = new ImplicationToken();

	private static final LinkedList<Implication> implicationsStorage = new LinkedList<Implication>();

	static final void load() {
		Tokenizer.safeRead(E621.pathMain, "implications", token);
	}

	static final void save() {
		Tokenizer.safeWrite(E621.pathMain, "implications", token);
	}
	
	static synchronized void index(final int tag, final int requires, final boolean approved) {
		for(final Implication implication : implicationsStorage)
			if(implication.tag == tag && implication.requires == requires) {
				if(approved)
					implication.approved = true;

				return;
			}

		implicationsStorage.addLast(new Implication(tag, requires, approved));
	}
	
	private static final class ImplicationToken implements Token {
		public final void generate() {
			implicationsStorage.clear();
		}
		
		public final void read(final DataInputStream dis) throws Exception {
			implicationsStorage.clear();

			for(int i = dis.readInt(); i != 0; i--)
				implicationsStorage.addLast(new Implication(dis));
		}
		
		public final void write(final DataOutputStream dos) throws IOException {
			dos.writeInt(implicationsStorage.size());

			while(!implicationsStorage.isEmpty())
				implicationsStorage.removeFirst().write(dos);
		}
	}
}