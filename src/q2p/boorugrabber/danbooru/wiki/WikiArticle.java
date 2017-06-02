package q2p.boorugrabber.danbooru.wiki;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.danbooru.posts.tags.TagType;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.Storage;

final class WikiArticle extends IndexableTemporary {
	int id;
	private boolean deleted;
	private String title;
	private boolean locked;
	private long creationDate;
	private long updatedDate;
	private String body;
	private String otherNames;
	private TagType type;

	WikiArticle(final int id, final boolean deleted, final String title, final boolean locked, final long creationDate, final long updatedDate, final String body, final String otherNames, final TagType type) {
		super(Danbooru.pathPosts, id);
		this.deleted = deleted;
		this.title = title;
		this.locked = locked;
		this.creationDate = creationDate;
		this.updatedDate = updatedDate;
		this.body = body;
		this.otherNames = otherNames;
		this.type = type;
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		Storage.writeString(dos, title);
		dos.writeBoolean(deleted);
		dos.writeBoolean(locked);
		dos.writeLong(creationDate);
		dos.writeLong(updatedDate);
		Storage.writeString(dos, body);
		Storage.writeString(dos, otherNames);
		dos.writeByte(type.localId);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		title = Storage.readString(dis);
		deleted = dis.readBoolean();
		locked = dis.readBoolean();
		creationDate = dis.readLong();
		updatedDate = dis.readLong();
		body = Storage.readString(dis);
		otherNames = Storage.readString(dis);
		type = TagType.getByLocalId(dis.readByte());
	}
}