package q2p.boorugrabber.generic.posts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.Writable;

final class Note implements Writable {
	int x = -1;
	int y = -1;
	int width = -1;
	int height = -1;
	String body = null;

	Note(final int x, final int y, final int width, final int height, final String body) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.body = body;
	}
	
	Note() {}

	Note(final DataInputStream dis) throws Exception {
		x = dis.readInt();
		y = dis.readInt();
		width = dis.readInt();
		height = dis.readInt();
		body = Storage.readString(dis);
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(width);
		dos.writeInt(height);
		Storage.writeString(dos, body);
	}

	private boolean[] assign = new boolean[4];
	final boolean set(final int id, final int value) {
		if(assign == null || assign[id])
			return true;

		assign[id] = true;
		switch(id) {
			case 0:
				x = value;
				break;
			case 1:
				y = value;
				break;
			case 2:
				width = value;
				break;
			default:
				height = value;
		}

		for(byte i = 0; assign[i]; i++)
			if(i == 3) {
				assign = null;
				return false;
			}
		return false;
	}
	final boolean set(final String body) {
		if(assign != null)
			return true;

		this.body = body;
		return false;
	}
}