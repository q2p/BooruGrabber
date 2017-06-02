package q2p.boorugrabber.danbooru.posts.notes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.Writable;

public final class Note implements Writable {
	final int id;
	final long creationDate;
	final long updateDate;
	final int x;
	final int y;
	final int width;
	final int height;
	final String body;
	final short version;
	final boolean active;

	Note(final int id, final long creationDate, final long updateDate, final int x, final int y, final int width, final int height, final String body, final short version, final boolean active) {
		this.id = id;
		this.creationDate = creationDate;
		this.updateDate = updateDate;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.body = body;
		this.version = version;
		this.active = active;
	}
	
	public Note(final DataInputStream dis) throws Exception {
		id = dis.readInt();
		creationDate = dis.readLong();
		updateDate = dis.readLong();
		x = dis.readInt();
		y = dis.readInt();
		width = dis.readInt();
		height = dis.readInt();
		body = Storage.readString(dis);
		version = dis.readShort();
		active = dis.readBoolean();
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeInt(id);
		dos.writeLong(creationDate);
		dos.writeLong(updateDate);
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(width);
		dos.writeInt(height);
		Storage.writeString(dos, body);
		dos.writeShort(version);
		dos.writeBoolean(active);
	}
}