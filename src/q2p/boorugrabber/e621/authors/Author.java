package q2p.boorugrabber.e621.authors;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.Storage;

final class Author extends IndexableTemporary {
	private String name;
	private String otherNames;
	private String groupName;
	private String urls;
	private boolean active;
	private short version;

	Author(final int id, final String name, final String otherNames, final String groupName, final String urls, final boolean active, final short version) {
		super(E621.pathAuthors, id);
		this.name = name;
		this.otherNames = otherNames;
		this.groupName = groupName;
		this.urls = urls;
		this.active = active;
		this.version = version;
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		Storage.writeString(dos, name);
		Storage.writeString(dos, otherNames);
		Storage.writeString(dos, groupName);
		Storage.writeString(dos, urls);
		dos.writeBoolean(active);
		dos.writeShort(version);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		name = Storage.readString(dis);
		otherNames = Storage.readString(dis);
		groupName = Storage.readString(dis);
		urls = Storage.readString(dis);
		active = dis.readBoolean();
		version = dis.readShort();
	}
}