package q2p.boorugrabber.storage.versions;

public abstract class IndexableVersionsBank<V extends Version<V>, Identifier> extends VersionsBank<V, Identifier> {
	protected IndexableVersionsBank(final String path, final String name) {
		super(path, name);
	}
	public final int index(final Identifier id) {
		int i = 0;
		synchronized(bank) {
			for(final Versions<V, Identifier> stored : bank) {
				if(sameIdentifier(stored.id, id))
					return i;

				i++;
			}

			bank.addLast(new Versions<V, Identifier>(id));
		}
		return i;
	}
	public final int index(final Identifier id, final V version) {
		int i = 0;
		synchronized(bank) {
			for(final Versions<V, Identifier> stored : bank) {
				if(sameIdentifier(stored.id, id)) {
					stored.index(version);
					return i;
				}

				i++;
			}

			bank.addLast(new Versions<V, Identifier>(id, version));
		}
		return i;
	}
}