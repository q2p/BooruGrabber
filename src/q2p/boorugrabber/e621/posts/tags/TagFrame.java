package q2p.boorugrabber.e621.posts.tags;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.versions.Version;

final class TagFrame extends Version {
	final String name;
	final TagType type;

	TagFrame(final String name, final TagType type) {
		this.name = name;
		this.type = type;
	}
	
	TagFrame(final DataInputStream dis) throws Exception {
		name = Storage.readString(dis);
		type = TagType.getByLocalId(dis.readByte());
	}

	protected final void write(final DataOutputStream dos) throws IOException {
		Storage.writeString(dos, name);
		dos.writeByte(type.localId);
		dos.flush();
	}
	
	protected final boolean same(final Version frame) {
		if(!(frame instanceof TagFrame))
			return false;
		
		final TagFrame tagFrame = (TagFrame) frame;
		if(!tagFrame.name.equals(name))
			throw new IllegalArgumentException("Новое имя \""+tagFrame.name+"\" тэга не совпадает с нынешним \""+name+'"');
		
		return type == tagFrame.type;
	}
}