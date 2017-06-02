package q2p.boorugrabber.derpibooru.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.versions.Version;

final class TagVersion extends Version<TagVersion> {
	final TagType type;
	final int aliasedTo;
	final String description;
	final int[] implies;
	
	TagVersion(final TagType type, final String description, final int aliasedTo, final int[] implies) {
		this.type = type;
		this.description = description;
		this.aliasedTo = aliasedTo;
		this.implies = implies;
	}
	TagVersion(final DataInputStream dis) throws Exception {
		type = TagType.getByLocalId(dis.readByte());
		aliasedTo = dis.readInt();
		description = Storage.readString(dis);
		implies = Storage.readInts(dis);
	}

	protected final void write(DataOutputStream dos) throws IOException {
		dos.writeByte(type == null ? -1 : type.localId);
		dos.writeInt(aliasedTo);
		Storage.writeString(dos, description);
		Storage.writeInts(dos, implies);
		dos.flush();
	}
	protected final boolean same(final TagVersion frame) {
		return frame.type == type && frame.aliasedTo == aliasedTo && frame.description.equals(description) && Arrays.equals(frame.implies, implies);
	}
}