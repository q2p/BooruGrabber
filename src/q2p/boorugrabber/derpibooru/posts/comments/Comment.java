package q2p.boorugrabber.derpibooru.posts.comments;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.queue.segmented.Segment;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.Writable;

public final class Comment implements Segment, Writable {
	final int id;
	final String author;
	final long date;
	final String body;
	final boolean deleted;

	Comment(final int id, final String author, final long date, final String body, final boolean deleted) {
		this.id = id;
		this.author = author;
		this.date = date;
		this.body = body;
		this.deleted = deleted;
	}
	
	public Comment(final DataInputStream dis) throws Exception {
		id = dis.readInt();
		author = Storage.readString(dis);
		date = dis.readLong();
		body = Storage.readString(dis);
		deleted = dis.readBoolean();
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeInt(id);
		Storage.writeString(dos, author);
		dos.writeLong(date);
		Storage.writeString(dos, body);
		dos.writeBoolean(deleted);
	}

	public final boolean copyOf(final Segment stored) {
		return stored instanceof Comment && id == ((Comment)stored).id;
	}
}