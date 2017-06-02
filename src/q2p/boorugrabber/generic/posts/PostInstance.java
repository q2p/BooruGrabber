package q2p.boorugrabber.generic.posts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.help.XMLParser;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.SaveTemporaryTask;
import q2p.boorugrabber.storage.Storage;

public final class PostInstance extends IndexableTemporary implements Task {
	public PostInstance(final GenericBooru booru, final int id) {
		super(Danbooru.pathPosts, id);
		this.booru = booru;
	}
	public final GenericBooru booru;
	boolean deleted = false;
	String deletionReason = "";
	long creationDate = 0;
	int score = 0;
	String file = "";
	int width = 0;
	int height = 0;
	long length = 0;
	String preview = "";
	String sample = "";
	Rating rating = Rating.SAFE;
	final LinkedList<Integer> tags = new LinkedList<Integer>();
	int parent = -1;
	String source = "";
	final LinkedList<Note> notes = new LinkedList<Note>();

	public final LinkedList<Task> receive() {
		final Response response = booru.networker.receive("GET", booru.urlPrefix + "index.php?page=dapi&s=post&q=index&id=" + id, booru.headers);
		if(response == null)
			return null;

		final LinkedList<Task> ret = new LinkedList<Task>();

		try {
			final Document document = XMLParser.parse(response.data);
			if(document == null)
				return null;

			final NodeList nodes = document.getElementsByTagName("post");
			if(nodes.getLength() != 1) {
				if(document.getElementsByTagName("posts").getLength() != 1)
					return null;

				deleted = true;

				ret.addLast(new SaveTemporaryTask(this));
				return ret;
			}

			final NamedNodeMap map = nodes.item(0).getAttributes();
			if(map == null)
				return null;

			final String temp = XMLParser.attribute(map, "score");
			if(temp == null)
				return null;

			score = Integer.parseInt(temp);

			file = parseLink(XMLParser.attribute(map, "file_url"));
			if(file == null)
				return null;

			sample = parseLink(XMLParser.attribute(map, "sample_url"));
			if(sample == null)
				return null;

			preview = parseLink(XMLParser.attribute(map, "preview_url"));
			if(preview == null)
				return null;

			Integer ti = XMLParser.integer(map, "width");
			if(ti == null)
				return null;

			width = ti;

			ti = XMLParser.integer(map, "height");
			if(ti == null)
				return null;

			height = ti;

			rating = Rating.getByCode(XMLParser.attribute(map, "rating"));
			if(rating == null)
				return null;
			
			ti = XMLParser.negativeIfEmpty(map, "parent_id");
			if(ti == null)
				return null;

			parent = ti;

			source = XMLParser.attribute(map, "source");
			if(source == null)
				return null;

			final Long tl = GenericBooru.parseDate(XMLParser.attribute(map, "created_at"));
			if(tl == null)
				return null;

			creationDate = tl;
		} catch(final Exception e) {
			return null;
		}
		ret.addLast(new AdditionalTask(this));
		return ret;
	}
	
	public final LinkedList<Task> work() {
		Log.lapse("Пост #", 50, id);
		final LinkedList<Task> ret = receive();
		if(ret == null)
			Log.out("Не удалось обработать пост #"+id);

		return ret;
	}

	private final String parseLink(final String url) {
		if(url == null)
			return null;

		if(url.startsWith("//"))
			return booru.ssl ? "https:" : "http:" + url;

		return url;
	}

	public final SaveTemporaryTask append(final LinkedList<Integer> tags, final LinkedList<Note> notes) {
		return new SaveTemporaryTask(this);
	}

	public final void write(final DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		dos.writeBoolean(deleted);
		Storage.writeString(dos, deletionReason);
		dos.writeLong(creationDate);
		dos.writeInt(score);
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
		deletionReason = Storage.readString(dis);
		creationDate = dis.readLong();
		score = dis.readInt();
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