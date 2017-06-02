package q2p.boorugrabber.danbooru.pools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.Storage;

final class Pool extends IndexableTemporary {
	private String name;
	private String description;
	private long creationDate;
	private long updateDate;
	private boolean deleted;
	private boolean active;
	private boolean series;
	private int[] posts;

	Pool(final int id, final String name, final String description, final long creationDate, final long updateDate, final boolean deleted, final boolean active, final boolean series, final int[] posts) {
		super(Danbooru.pathPools, id);
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
		this.updateDate = updateDate;
		this.deleted = deleted;
		this.active = active;
		this.series = series;
		this.posts = posts;
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		Storage.writeString(dos, name);
		Storage.writeString(dos, description);
		dos.writeLong(creationDate);
		dos.writeLong(updateDate);
		dos.writeBoolean(deleted);
		dos.writeBoolean(active);
		dos.writeBoolean(series);
		Storage.writeInts(dos, posts);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		name = Storage.readString(dis);
		description = Storage.readString(dis);
		creationDate = dis.readLong();
		updateDate = dis.readLong();
		deleted = dis.readBoolean();
		active = dis.readBoolean();
		series = dis.readBoolean();
		posts = Storage.readInts(dis);
	}
}