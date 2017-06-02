package q2p.boorugrabber.derpibooru.posts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.derpibooru.Derpibooru;
import q2p.boorugrabber.derpibooru.posts.comments.Comment;
import q2p.boorugrabber.derpibooru.posts.comments.CommentsCombiner;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.SaveTemporaryTask;
import q2p.boorugrabber.storage.Storage;

public final class PostInstance extends IndexableTemporary implements Task {
	public PostInstance(final int id) {
		super(Derpibooru.pathPosts, id);
	}
	private boolean deleted;
	private String deletionReason = "";
	private String description = "";
	private long creationDate = 0;
	private long updateDate = 0;
	private int upScore = 0;
	private int downScore = 0;
	private int favs = 0;
	private String file = "";
	private int width = 0;
	private int height = 0;
	private long length = 0;
	private String preview = "";
	private String sample = "";
	private int[] tags = new int[0];
	private String source = "";
	private final LinkedList<Comment> comments = new LinkedList<Comment>();
	private String sha512 = "";

	public final LinkedList<Task> work() {
		Log.lapse("Пост #", 50, id);
		final LinkedList<Task> ret = receive();
		if(ret == null)
			Log.out("Не удалось обработать пост #"+id);

		return ret;
	}
	
	public final LinkedList<Task> receive() {
		final Response response = Derpibooru.networker.receive("GET", "https://derpibooru.org/"+id+".json", Derpibooru.headers);
		if(response == null)
			return null;
		
		final LinkedList<Task> ret = new LinkedList<Task>();
		
		deleted = response.code == 404;
		if(deleted)
			return ret;

		try {
			JSONObject object = JSON.parseObject(response.toString());
			if(object == null)
				return null;
			
			Long tl = Derpibooru.parseDate(object.getString("created_at"));
			if(tl == null)
				return null;
			creationDate = tl;

			tl = Derpibooru.parseDate(object.getString("updated_at"));
			if(tl == null)
				return null;
			updateDate = tl;
			
			Integer ti = object.getInteger("width");
			if(ti == null) {
				deletionReason = object.getString("deletion_reason");
				if(deletionReason == null) {
					ti = object.getInteger("duplicate_of");
					if(ti == null)
						return null;

					deletionReason = "dupe of " + ti;
				}

				return ret;
			}
			width = ti;

			ti = object.getInteger("height");
			if(ti == null)
				return null;
			height = ti;

			description = object.getString("description");
			if(description == null)
				return null;
			
			ti = object.getInteger("upvotes");
			if(ti == null)
				return null;
			upScore = ti;

			ti = object.getInteger("downvotes");
			if(ti == null)
				return null;
			downScore = ti;

			ti = object.getInteger("faves");
			if(ti == null)
				return null;
			favs = ti;
			
			tags = Derpibooru.parseTags(object.getString("tags"));
			if(tags == null)
				return null;

			source = object.getString("source_url");
			if(source == null)
				return null;

			sha512 = object.getString("orig_sha512_hash");
			if(sha512 == null) {
				sha512 = object.getString("sha512_hash");
				if(sha512 == null)
					sha512 = "";
			}

			object = object.getJSONObject("representations");
			if(object == null)
				return null;

			preview = checkLink(object.getString("thumb"));
			sample = checkLink(object.getString("large"));

			if(preview == null || sample == null)
				return null;
			
			file = object.getString("full");
			if(!file.startsWith("//derpicdn.net/img/view/"))
				return null;

			String temp = file.substring("//derpicdn.net/img/view/".length());
			String temp2 = "";

			int idx = temp.lastIndexOf('/');

			ti = temp.indexOf('.', idx);
			if(ti != -1) {
				temp2 = temp.substring(ti);
				temp = temp.substring(0, ti);
			}

			idx = temp.indexOf('_', idx);
			if(idx != -1)
				temp = temp.substring(0, idx);

			file = "https://derpicdn.net/img/view/"+temp+temp2;
		} catch(final Exception e) {
			return null;
		}
		
		ret.addLast(new CommentsCombiner(this).getTask());
		return ret;
	}

	private final String checkLink(final String url) {
		if(url == null || !url.startsWith("//derpicdn.net/img/"))
			return null;

		return "https:" + url;
	}

	public final SaveTemporaryTask appendComments(final LinkedList<Comment> comments) {
		Assist.drain(this.comments, comments);
		return new SaveTemporaryTask(this);
	}

	public final void write(final DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		dos.writeBoolean(deleted);
		Storage.writeString(dos, deletionReason);
		Storage.writeString(dos, description);
		dos.writeLong(creationDate);
		dos.writeLong(updateDate);
		dos.writeInt(upScore);
		dos.writeInt(downScore);
		dos.writeInt(favs);
		Storage.writeString(dos, file);
		dos.writeInt(width);
		dos.writeInt(height);
		dos.writeLong(length);
		Storage.writeString(dos, preview);
		Storage.writeString(dos, sample);
		Storage.writeInts(dos, tags);
		Storage.writeString(dos, source);
		Storage.write(dos, comments);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		// TODO Auto-generated method stub
		deleted = dis.readBoolean();
		deletionReason = Storage.readString(dis);
		description = Storage.readString(dis);
		creationDate = dis.readLong();
		updateDate = dis.readLong();
		upScore = dis.readInt();
		downScore = dis.readInt();
		favs = dis.readInt();
		file = Storage.readString(dis);
		width = dis.readInt();
		height = dis.readInt();
		length = dis.readLong();
		preview = Storage.readString(dis);
		sample = Storage.readString(dis);
		tags = Storage.readInts(dis);
		source = Storage.readString(dis);
		for(int i = dis.readInt(); i != 0; i--)
			comments.addLast(new Comment(dis));
	}
}