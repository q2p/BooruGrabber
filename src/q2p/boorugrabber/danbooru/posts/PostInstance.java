package q2p.boorugrabber.danbooru.posts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.danbooru.posts.notes.Note;
import q2p.boorugrabber.danbooru.posts.notes.NotesInstance;
import q2p.boorugrabber.danbooru.posts.tags.TagFrame;
import q2p.boorugrabber.danbooru.posts.tags.TagType;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Parser;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.SaveTemporaryTask;
import q2p.boorugrabber.storage.Storage;

public final class PostInstance extends IndexableTemporary {
	public PostInstance(final int id) {
		super(Danbooru.pathPosts, id);
	}
	boolean deleted;
	boolean banned = false;
	long creationDate = -1;
	int upScore = -1;
	int downScore = -1;
	int favs = -1;
	String file = "";
	int width = -1;
	int height = -1;
	long length = -1;
	String preview = "";
	String sample = "";
	Rating rating = Rating.SAFE;
	final LinkedList<Integer> tags = new LinkedList<Integer>();
	int parent = -1;
	String source = "";
	final LinkedList<Note> notes = new LinkedList<Note>();

	public final LinkedList<Task> receive() {
		final Response response = Danbooru.networker.receive("GET", "https://danbooru.donmai.us/posts/"+id+".json", null);
		if(response == null)
			return null;

		final LinkedList<Task> ret = new LinkedList<Task>();
		if(response.code == 404) {
			deleted = true;
			ret.addLast(new SaveTemporaryTask(this));
			return ret;
		}

		try {
			final JSONObject post = JSON.parseObject(response.toString());
			if(post == null)
				return null;

			final Long tl = Danbooru.parseTDate(post.getString("created_at"));
			if(tl == null)
				return null;
			creationDate = tl;

			String temp = post.getString("source");
			if(temp == null)
				return null;
			source = temp;

			rating = Rating.getByCode(post.getString("rating"));
			if(rating == null)
				return null;

			Integer ti = post.getInteger("image_width");
			width = ti == null ? -1 : ti;

			ti = post.getInteger("image_height");
			height = ti == null ? -1 : ti;

			ti = post.getInteger("fav_count");
			if(ti == null)
				return null;
			favs = ti;
			
			ti = post.getInteger("parent_id");
			parent = ti == null ? -1 : ti;
			
			ti = post.getInteger("file_size");
			if(ti == null)
				return null;
			length = ti;
			
			ti = post.getInteger("up_score");
			if(ti == null)
				return null;
			upScore = ti;
			
			ti = post.getInteger("down_score");
			if(ti == null)
				return null;
			downScore = ti;

			Boolean tb = post.getBoolean("is_deleted");
			if(tb == null)
				return null;
			deleted = tb;

			tb = post.getBoolean("is_banned");
			if(tb == null)
				return null;
			banned = tb;
			
			if(
					indexTags(post, "tag_string_artist", TagType.ARTIST) ||
					indexTags(post, "tag_string_character", TagType.CHARACTER) ||
					indexTags(post, "tag_string_copyright", TagType.COPYRIGHT) ||
					indexTags(post, "tag_string_general", TagType.GENERAL)
					) return null;

			temp = parseLink(post,"file_url");
			if(temp == null)
				return null;
			file = temp;
			
			temp = parseLink(post,"large_file_url");
			if(temp == null)
				return null;
			sample = temp;
			
			temp = parseLink(post,"preview_file_url");
			if(temp == null)
				return null;
			preview = temp;
		} catch(final Exception e) {
			return null;
		}
		
		ret.addLast(new NotesInstance(this));
		return ret;
	}

	private final boolean indexTags(final JSONObject post, final String field, final TagType type) {
		final String temp = post.getString(field);
		if(temp == null)
			return true;

		if(temp.length() != 0) {
			final LinkedList<String> parsed = Parser.split(temp, ' ');
			do
				tags.addLast(Danbooru.tags.index(parsed.removeFirst(), new TagFrame(type)));
			while(!parsed.isEmpty());
		}

		return false;
	}

	private final String parseLink(final JSONObject post, String field) {
		field = post.getString(field);

		if(field == null)
			return "";

		if(!field.startsWith("/data/") && !field.equals("/images/download-preview.png"))
			return null;

		return "https://danbooru.donmai.us"+field;
	}

	public final SaveTemporaryTask appendNotes(final LinkedList<Note> notes) {
		Assist.drain(this.notes, notes);
		return new SaveTemporaryTask(this);
	}

	public final void write(final DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		dos.writeBoolean(deleted);
		dos.writeLong(creationDate);
		dos.writeInt(upScore);
		dos.writeInt(downScore);
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
		Storage.writeString(dos, source);
		Storage.write(dos, notes);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		// TODO Auto-generated method stub
		deleted = dis.readBoolean();
		creationDate = dis.readLong();
		upScore = dis.readInt();
		downScore = dis.readInt();
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
		source = Storage.readString(dis);
		for(int i = dis.readInt(); i != 0; i--)
			notes.addLast(new Note(dis));
	}
}