package q2p.boorugrabber.storage.versions;

public abstract class HeapVersionsBank<V extends Version<V>, Identifier> extends VersionsBank<V, Identifier> {
	protected HeapVersionsBank(final String path, final String name) {
		super(path, name);
	}
	public final synchronized void index(final Identifier id, final V version) {
		for(final Versions<V, Identifier> stored : bank) {
			if(sameIdentifier(stored.id, id)) {
				stored.index(version);
				return;
			}
		}

		bank.addLast(new Versions<V, Identifier>(id, version));
	}
}