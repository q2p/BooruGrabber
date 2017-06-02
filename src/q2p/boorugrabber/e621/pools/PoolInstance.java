package q2p.boorugrabber.e621.pools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.SaveTemporaryTask;
import q2p.boorugrabber.storage.Storage;

final class PoolInstance extends IndexableTemporary {
	private boolean deleted;
	private String name = "";
	private int author = -1;
	private String description = "";
	private long creationDate = -1;
	private long updateDate = -1;
	private boolean locked = false;
	private final LinkedList<Integer> posts = new LinkedList<Integer>();

	PoolInstance(final int id) {
		super(E621.pathPools, id);
	}
	
	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeBoolean(deleted);
		if(deleted)
			return;
		Storage.writeString(dos, name);
		dos.writeInt(author);
		Storage.writeString(dos, description);
		dos.writeLong(creationDate);
		dos.writeLong(updateDate);
		dos.writeBoolean(locked);
		Storage.writeInts(dos, posts);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		deleted = dis.readBoolean();
		if(deleted)
			return;
		name = Storage.readString(dis);
		author = dis.readInt();
		description = Storage.readString(dis);
		creationDate = dis.readLong();
		updateDate = dis.readLong();
		locked = dis.readBoolean();
		Storage.readInts(dis, posts);
	}
	
	final SaveTemporaryTask append(final LinkedList<PoolPost> posts) {
		while(!posts.isEmpty())
			this.posts.addLast(posts.removeFirst().id);
		
		return new SaveTemporaryTask(this);
	}

	final LinkedList<Task> receive() {
		final Response response = E621.networker.receive("GET", "https://e621.net/pool/show.json?id="+id, E621.headers);

		if(response == null)
			return null;
		
		try {
			final JSONObject pool = JSON.parseObject(response.toString());
			if(pool == null)
				return null;

			final LinkedList<Task> ret = new LinkedList<Task>();

			Boolean tb = pool.getBoolean("success");
			if(tb != null && tb == false) {
				deleted = true;
				ret.add(new SaveTemporaryTask(this));
				return ret;
			}
			
			deleted = false;

			if(pool.getInteger("id") == null)
				return null;

			name = pool.getString("name");
			if(name == null)
				return null;

			Long tl = E621.parseJSONDate(pool.getJSONObject("created_at"));
			if(tl == null)
				return null;
			creationDate = tl;

			tl = E621.parseJSONDate(pool.getJSONObject("updated_at"));
			if(tl == null)
				return null;
			updateDate = tl;

			final Integer ti = pool.getInteger("user_id");
			if(ti == null)
				return null;
			author = ti;

			tb = pool.getBoolean("is_locked");
			if(tb == null)
				return null;
			locked = tb;

			description = pool.getString("description");
			if(description == null)
				return null;

			final JSONArray posts = pool.getJSONArray("posts");
			if(posts == null)
				return null;

			if(posts.size() != 0)
				ret.addLast(new PostsCombiner(this).getTask());

			return ret;
		} catch(final Exception e) {
			return null;
		}
	}
}