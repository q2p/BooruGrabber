package q2p.boorugrabber.danbooru.posts.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.versions.Version;

public final class TagFrame extends Version<TagFrame> {
	final TagType type;
	
	public TagFrame(final DataInputStream dis) throws IOException {
		type = TagType.getByLocalId(dis.readByte());
	}
	public TagFrame(final TagType type) {
		this.type = type;
	}
	
	protected final boolean same(final TagFrame frame) {
		return frame.type == type;
	}
	protected final void write(final DataOutputStream dos) throws IOException {
		dos.writeByte(type.localId);
	}
}