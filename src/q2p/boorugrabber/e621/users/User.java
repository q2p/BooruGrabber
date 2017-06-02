package q2p.boorugrabber.e621.users;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.Storage;

final class User extends IndexableTemporary {
	private String name;
	private Rank rank;
	private long creationDate;
	private int avatar;

	User(final int id, final String name, final Rank rank, final long creationDate, final int avatar) {
		super(E621.pathUsers, id);
		this.name = name;
		this.rank = rank;
		this.creationDate = creationDate;
		this.avatar = avatar;
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		Storage.writeString(dos, name);
		dos.writeByte(rank.rawCode);
		dos.writeLong(creationDate);
		dos.writeInt(avatar);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		name = Storage.readString(dis);
		rank = Rank.getByRawCode(dis.readByte());
		creationDate = dis.readLong();
		avatar = dis.readInt();
	}
}