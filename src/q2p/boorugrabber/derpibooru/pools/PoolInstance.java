package q2p.boorugrabber.derpibooru.pools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.derpibooru.Derpibooru;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.SaveTemporaryTask;
import q2p.boorugrabber.storage.Storage;

final class PoolInstance extends IndexableTemporary implements Task {
	PoolInstance(final int id) {
		super(Derpibooru.pathPools, id);
	}
	public final LinkedList<Task> work() {
		Log.lapse("Пул #", 50, id);
		final LinkedList<Task> ret = receive();
		if(ret == null)
			Log.out("Не удалось обработать пул #"+id);

		return ret;
	}

	private boolean deleted = false;
	private String name = "";
	private String description = "";
	private long creationDate = -1;
	private long updateDate = -1;
	private final LinkedList<Integer> posts = new LinkedList<Integer>();

	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeBoolean(deleted);
		if(deleted)
			return;
		Storage.writeString(dos, name);
		Storage.writeString(dos, description);
		dos.writeLong(creationDate);
		dos.writeLong(updateDate);
		Storage.writeInts(dos, posts);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		deleted = dis.readBoolean();
		if(deleted)
			return;
		name = Storage.readString(dis);
		description = Storage.readString(dis);
		creationDate = dis.readLong();
		updateDate = dis.readLong();
		Storage.readInts(dis, posts);
	}
	
	final SaveTemporaryTask append(final LinkedList<PoolPost> posts) {
		while(!posts.isEmpty())
			this.posts.addLast(posts.removeFirst().id);
		
		return new SaveTemporaryTask(this);
	}

	final LinkedList<Task> receive() {
		String url = "https://derpibooru.org/galleries/0/"+id+".json";
		Response response = Derpibooru.networker.receive("GET", url, Derpibooru.headers);

		if(response == null)
			return null;
		
		final LinkedList<Task> ret = new LinkedList<Task>();
		
		if(response.redirected != null) {
			url = response.redirected;
			if(url.equals("https://derpibooru.org/")) {
				deleted = true;
				return ret;
			}

			if(!url.startsWith("https://derpibooru.org/galleries/"))
				return null;

			final int idx = url.indexOf('/', "https://derpibooru.org/galleries/".length())+1;
			if(idx == 0 || !url.substring(idx).equals(id+".json"))
				return null;

			response = Derpibooru.networker.receive("GET", url, Derpibooru.headers);
			if(response == null || response.redirected != null)
				return null;
		}
		
		try {
			final JSONObject pool = JSON.parseObject(response.toString());
			if(pool == null)
				return null;

			final JSONObject to = pool.getJSONObject("gallery");
			if(to == null)
				return null;

			name = to.getString("title");
			if(name == null)
				return null;

			description = to.getString("description");
			if(description == null)
				return null;

			final String temp = to.getString("spoiler_warning");
			if(temp == null)
				return null;
			if(temp.length() != 0)
				description += "\n"+temp;

			Long tl = Derpibooru.parseDate(to.getString("created_at"));
			if(tl == null)
				return null;
			creationDate = tl;

			tl = Derpibooru.parseDate(to.getString("updated_at"));
			if(tl == null)
				return null;
			updateDate = tl;

			ret.addLast(new PostsCombiner(this).getTask(url));
			return ret;
		} catch(final Exception e) {
			return null;
		}
	}
}