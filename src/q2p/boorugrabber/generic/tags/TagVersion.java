package q2p.boorugrabber.generic.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.versions.Version;
// TODO: same as e621
final class TagVersion extends Version<TagVersion> {
	final String name;
	final TagType type;

	TagVersion(final String name, final TagType type) {
		this.name = name;
		this.type = type;
	}
	
	TagVersion(final DataInputStream dis) throws Exception {
		name = Storage.readString(dis);
		type = TagType.getByLocalId(dis.readByte());
	}

	protected final void write(final DataOutputStream dos) throws IOException {
		Storage.writeString(dos, name);
		dos.writeByte(type.localId);
		dos.flush();
	}
	
	protected final boolean same(final TagVersion frame) {
		if(!frame.name.equals(name))
			throw new IllegalArgumentException("Новое имя \""+frame.name+"\" тэга не совпадает с нынешним \""+name+'"');
		
		return type == frame.type;
	}
}