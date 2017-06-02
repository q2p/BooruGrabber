package q2p.boorugrabber.e621.posts.comments;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.queue.segmented.Segment;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.Writable;

public final class Comment implements Segment, Writable {
	final int id;
	final int author;
	final long date;
	final int score;
	final String body;

	Comment(final int id, final int author, final long date, final int score, final String body) {
		this.id = id;
		this.author = author;
		this.date = date;
		this.score = score;
		this.body = body;
	}
	
	public Comment(final DataInputStream dis) throws Exception {
		id = dis.readInt();
		author = dis.readInt();
		date = dis.readLong();
		score = dis.readInt();
		body = Storage.readString(dis);
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeInt(id);
		dos.writeInt(author);
		dos.writeLong(date);
		dos.writeInt(score);
		Storage.writeString(dos, body);
	}

	public final boolean copyOf(final Segment stored) {
		return stored instanceof Comment && id == ((Comment)stored).id;
	}
}