package q2p.boorugrabber.paheal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.help.Parser;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.SaveTemporaryTask;
import q2p.boorugrabber.storage.Storage;

public final class PostInstance extends IndexableTemporary implements Task {
	public PostInstance(final int id) {
		super(Paheal.pathPosts, id);
	}
	private boolean deleted = false;
	private String deletionReason = ""; // TODO: HAS?
	private long creationDate = 0;
	private String file = "";
	private int width = 0;
	private int height = 0;
	private long length = 0;
	private final LinkedList<Integer> tags = new LinkedList<Integer>();
	private int parent = -1; // TODO: HAS?
	private String source = "";

	public final LinkedList<Task> receive() {
		final Response response = Paheal.networker.receive("GET", "http://rule34.paheal.net/post/view/"+id, Paheal.headers);
		if(response == null)
			return null;
		
		final LinkedList<Task> ret = new LinkedList<Task>();
		ret.addLast(new SaveTemporaryTask(this));

		try {
			final Document document = Jsoup.parse(response.toString());
			if(document == null)
				return null;

			Element element = Parser.chain(document, "Tagsleft").clazz("blockbody").get();
			if(element == null) {
				element = Parser.chain(document, "Errormain").clazz("blockbody").get();
				if(element == null)
					return null;
				if(!element.text().startsWith("No image in the database has the ID #"))
					return null;

				return ret;
			}

			final Elements elements = element.children();
			if(elements.size() % 4 != 0)
				return null;

			String temp;
			for(int i = elements.size()-4; i > 0; i -= 4) {
				temp = elements.get(i + 1).text();
				if(temp.length() == 0)
					return null;

				tags.addLast(Paheal.tags.index(temp));
			}

			element = document.getElementById("main_image");
			if(element == null)
				return null;

			file = element.attr("src");
			if(!file.startsWith("http://rule34-data-"))
				return null;
			final int ti = file.indexOf(".paheal.net/_images/", "https://rule34-data-".length());
			if(ti == -1)
				return null;
			Integer.parseInt(file.substring("http://rule34-data-".length(), ti));
			
			width = Integer.parseInt(element.attr("data-width"));
			height = Integer.parseInt(element.attr("data-height"));
			if(width < 1 || height < 1)
				return null;

			element = document.getElementById("Imagemain");
			if(element == null)
				return null;
			element = Parser.chain(element.nextElementSibling()).clazz("blockbody").tag("form").tag("table").tag("tbody").get();
			if(element == null)
				return null;

			final Element el = Parser.chain(element).tagTotal("tr", 4, 0).tagTotal("td", 2, 0).tag("time").get();
			if(el == null)
				return null;

			creationDate = formater.parse(el.attr("datetime")).getTime();

			element = Parser.chain(element).tagTotal("tr", 4, 2).tag("td").tag("div").tag("a").get();
			if(element == null)
				return null;

			source = element.attr("href");
		} catch(final Exception e) {
			return null;
		}

		return ret;
	}

	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
	
	public final LinkedList<Task> work() {
		Log.lapse("Пост #", 50, id);
		final LinkedList<Task> ret = receive();
		if(ret == null)
			Log.out("Не удалось обработать пост #"+id);

		return ret;
	}

	public final void write(final DataOutputStream dos) throws IOException {
		// TODO Auto-generated method stub
		dos.writeBoolean(deleted);
		Storage.writeString(dos, deletionReason);
		dos.writeLong(creationDate);
		Storage.writeString(dos, file);
		dos.writeInt(width);
		dos.writeInt(height);
		dos.writeLong(length);
		Storage.writeInts(dos, tags);
		dos.writeInt(parent);
		Storage.writeString(dos, source);
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		// TODO Auto-generated method stub
		deleted = dis.readBoolean();
		deletionReason = Storage.readString(dis);
		creationDate = dis.readLong();
		file = Storage.readString(dis);
		width = dis.readInt();
		height = dis.readInt();
		length = dis.readLong();
		Storage.readInts(dis, tags);
		parent = dis.readInt();
		source = Storage.readString(dis);
	}
}