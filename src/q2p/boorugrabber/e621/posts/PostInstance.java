package q2p.boorugrabber.e621.posts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.danbooru.posts.Rating;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.e621.posts.comments.Comment;
import q2p.boorugrabber.e621.posts.comments.CommentsCombiner;
import q2p.boorugrabber.e621.posts.notes.Note;
import q2p.boorugrabber.e621.posts.notes.NotesCombiner;
import q2p.boorugrabber.e621.posts.tags.TagsTask;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.SaveTemporaryTask;
import q2p.boorugrabber.storage.Storage;

public final class PostInstance extends IndexableTemporary {
	public PostInstance(final int id) {
		super(E621.pathPosts, id);
	}
	boolean deleted;
	String deletionReason = "";
	int creator = -1;
	String description = "";
	long creationDate = 0;
	int score = 0;
	int favs = 0;
	String file = "https://e621.net/images/deleted-preview.png";
	int width = 0;
	int height = 0;
	long length = 0;
	String preview = "https://e621.net/images/deleted-preview.png";
	String sample = "https://e621.net/images/deleted-preview.png";
	Rating rating = Rating.SAFE;
	final LinkedList<Integer> tags = new LinkedList<Integer>();
	int parent = -1;
	final LinkedList<String> sources = new LinkedList<String>();
	final LinkedList<Comment> comments = new LinkedList<Comment>();
	final LinkedList<Note> notes = new LinkedList<Note>();

	private byte takers = 0;

	public final LinkedList<Task> receive() {
		final Response response = E621.networker.receive("GET", "https://e621.net/post/show.json?id="+id, E621.headers);
		if(response == null)
			return null;

		final String textResponse = response.toString();
		
		final LinkedList<Task> ret = new LinkedList<Task>();

		if(textResponse.equals("{}")) {
			deleted = true;
			ret.addLast(new SaveTemporaryTask(this));
			return ret;
		}

		try {
			final JSONObject post = JSON.parseObject(textResponse);
			if(post == null)
				return null;

			String temp = post.getString("status");
			if(temp == null)
				return null;

			deleted = temp.equals("deleted");

			if(deleted) {
				temp = post.getString("delreason");
				if(temp != null)
					deletionReason = null;
			}
			
			ret.add(new TagsTask(this));
			takers++;

			if(post.getInteger("id") == null)
				return null;

			description = post.getString("description");
			if(description == null)
				return null;

			Long tl = E621.parseJSONDate(post.getJSONObject("created_at"));
			if(tl == null)
				return null;
			creationDate = tl;
			
			Integer ti;
			try {
				ti = post.getInteger("creator_id");
				if(ti == null)
					creator = -1;
				else
					creator = ti;
			} catch(final NumberFormatException e) {
				creator = -1;
			}

			ti = post.getInteger("score");
			if(ti == null)
				return null;
			score = ti;

			ti = post.getInteger("fav_count");
			if(ti == null)
				return null;
			favs = ti;
			
			file = parseLink(post.getString("file_url"));
			if(file == null)
				return null;
			
			ti = post.getInteger("width");
			Integer ti2 = post.getInteger("height");
			if(ti == null || ti2 == null) {
				if(!deleted)
					return null;

				ti = post.getInteger("preview_width");
				ti2 = post.getInteger("preview_height");
			}
			width = ti;
			height = ti2;

			tl = post.getLong("file_size");
			if(tl == null) {
				if(!deleted)
					return null;
			} else
				length = tl;
			
			preview = parseLink(post.getString("preview_url"));
			if(preview == null)
				return null;
			
			sample = parseLink(post.getString("sample_url"));
			if(sample == null) if(deleted)
				sample = preview;
			else
				return null;
			
			rating = Rating.getByCode(post.getString("rating"));
			if(rating == null)
				return null;

			try {
				ti = post.getInteger("parent_id");
				if(ti != null)
					parent = ti;
			} catch(final NumberFormatException e) {}

			final JSONArray sources = post.getJSONArray("sources");
			if(sources != null) for(int i = sources.size() - 1; i != -1; i--)
				this.sources.addLast(sources.getString(i));
			
			Boolean tb = post.getBoolean("has_comments");
			if(tb == null) {
				if(!deleted)
					return null;
			} else if(tb) {
				ret.addLast(new CommentsCombiner(this).getTask());
				takers++;
			}

			tb = post.getBoolean("has_notes");
			if(tb == null) {
				if(!deleted)
					return null;
			} else if(tb) {
				ret.addLast(new NotesCombiner(this).getTask());
				takers++;
			}
		} catch(final Exception e) {
			return null;
		}

		return ret;
	}

	private final String parseLink(final String url) {
		if(url == null)
			return null;

		if(url.startsWith("//"))
			return "https:"+url;

		if(url.startsWith("/"))
			return "https://e621.net"+url;

		return url;
	}

	public final SaveTemporaryTask appendTags(final LinkedList<Integer> tags) {
		Assist.drain(this.tags, tags);
		
		return appendSaveCheck();
	}

	public final SaveTemporaryTask appendComments(final LinkedList<Comment> comments) {
		Assist.drain(this.comments, comments);
		
		return appendSaveCheck();
	}
	
	public final SaveTemporaryTask appendNotes(final LinkedList<Note> notes) {
		Assist.drain(this.notes, notes);
		
		return appendSaveCheck();
	}

	private final synchronized SaveTemporaryTask appendSaveCheck() {
		if(--takers == 0)
			return new SaveTemporaryTask(this);

		return null;
	}

	public final void write(final DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		dos.writeBoolean(deleted);
		Storage.writeString(dos, deletionReason);
		dos.writeInt(creator);
		Storage.writeString(dos, description);
		dos.writeLong(creationDate);
		dos.writeInt(score);
		dos.writeInt(favs);
		Storage.writeString(dos, file);
		dos.writeInt(width);
		dos.writeInt(height);
		dos.writeLong(length);
		Storage.writeString(dos, preview);
		Storage.writeString(dos, sample);
		dos.writeByte(rating.rawCode);
		Storage.writeInts(dos, tags);
		dos.writeInt(parent);
		Storage.writeStrings(dos, sources);
		Storage.write(dos, comments);
		Storage.write(dos, notes);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		// TODO Auto-generated method stub
		deleted = dis.readBoolean();
		deletionReason = Storage.readString(dis);
		creator = dis.readInt();
		description = Storage.readString(dis);
		creationDate = dis.readLong();
		score = dis.readInt();
		favs = dis.readInt();
		file = Storage.readString(dis);
		width = dis.readInt();
		height = dis.readInt();
		length = dis.readLong();
		preview = Storage.readString(dis);
		sample = Storage.readString(dis);
		rating = Rating.getByRawCode(dis.readByte());
		Storage.readInts(dis, tags);
		parent = dis.readInt();
		Storage.readStrings(dis, sources);
		for(int i = dis.readInt(); i != 0; i--)
			comments.addLast(new Comment(dis));
		for(int i = dis.readInt(); i != 0; i--)
			notes.addLast(new Note(dis));
	}
}