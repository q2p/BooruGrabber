package q2p.boorugrabber.storage;

public abstract class IndexableTemporary implements Temporary {
	public final String dirrectory;
	public int id;

	protected IndexableTemporary(final String dirrectory, final int id) {
		this.dirrectory = dirrectory;
		this.id = id;
	}
}