package q2p.boorugrabber.e621.wiki;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.Storage;

final class WikiArticle extends IndexableTemporary {
	private String title;
	private boolean locked;
	private long creationDate;
	private long updatedDate;
	private String body;
	private short version;

	WikiArticle(final int id, final String title, final boolean locked, final long creationDate, final long updatedDate, final String body, final short version) {
		super(E621.pathWiki, id);
		this.title = title;
		this.locked = locked;
		this.creationDate = creationDate;
		this.updatedDate = updatedDate;
		this.body = body;
		this.version = version;
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		Storage.writeString(dos, title);
		dos.writeBoolean(locked);
		dos.writeLong(creationDate);
		dos.writeLong(updatedDate);
		Storage.writeString(dos, body);
		dos.writeShort(version);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		title = Storage.readString(dis);
		locked = dis.readBoolean();
		creationDate = dis.readLong();
		updatedDate = dis.readLong();
		body = Storage.readString(dis);
		version = dis.readShort();
	}
}