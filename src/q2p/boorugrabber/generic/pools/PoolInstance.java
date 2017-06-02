package q2p.boorugrabber.generic.pools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.SaveTemporaryTask;
import q2p.boorugrabber.storage.Storage;

abstract class PoolInstance extends IndexableTemporary implements Task {
	final GenericBooru booru;
	
	protected boolean deleted;
	protected String name = "";
	protected LinkedList<Integer> posts = new LinkedList<Integer>();

	PoolInstance(final GenericBooru booru, final int id) {
		super(Danbooru.pathPools, id);
		this.booru = booru;
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeBoolean(deleted);
		if(deleted)
			return;
		Storage.writeString(dos, name);
		Storage.writeInts(dos, posts);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		deleted = dis.readBoolean();
		if(deleted)
			return;
		name = Storage.readString(dis);
		Storage.readInts(dis, posts);
	}

	public final LinkedList<Task> work() {
		Log.lapse("Пул #", 50, id);
		if(receive()) {
			Log.out("Не удалось обработать пул #"+id);
			return null;
		}

		final LinkedList<Task> ret = new LinkedList<Task>();
		ret.addLast(new SaveTemporaryTask(this));
		return ret;
	}

	abstract protected boolean receive();
}