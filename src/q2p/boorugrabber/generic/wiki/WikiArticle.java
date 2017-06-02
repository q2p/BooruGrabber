package q2p.boorugrabber.generic.wiki;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.help.Parser;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.IndexableTemporary;
import q2p.boorugrabber.storage.SaveTemporaryTask;
import q2p.boorugrabber.storage.Storage;

public final class WikiArticle extends IndexableTemporary implements Task {
	public WikiArticle(final GenericBooru booru, final int id) {
		super(Danbooru.pathWiki, id);
		this.booru = booru;
	}
	public final GenericBooru booru;
	int id;
	private boolean deleted = false;
	private String title = "";
	private String body = "";

	public final LinkedList<Task> receive() {
		final Response response = booru.networker.receive("GET", booru.urlPrefix + "index.php?page=wiki&s=view&id=" + id, booru.headers); // TODO:
		if(response == null)
			return null;
		
		final LinkedList<Task> ret = new LinkedList<Task>();

		try {
			final Document document = Jsoup.parse(response.toString());
			if(document == null)
				return null;

			final Element element = Parser.chain(document, "content").tag("div").tag("table").tag("tbody").tag("tr").tagTotal("td", 2, 0).get();
			if(element == null) {
				if(response.code != 302)
					return null;

				deleted = true;
				return ret;
			}

			final Element el = Parser.chain(element).tagMin("h2", 1, 0).get();
			if(el == null)
				return null;

			final String temp = el.text();
			if(!temp.startsWith("Now Viewing: "))
				return null;

			title = temp.substring(13);
			body = element.html();
		} catch(final Exception e) {
			return null;
		}

		ret.addLast(new SaveTemporaryTask(this));
		return ret;
	}
	
	public final LinkedList<Task> work() {
		Log.lapse("Wiki #", 50, id);
		final LinkedList<Task> ret = receive();
		if(ret == null)
			Log.out("Не удалось обработать статью wiki #"+id);

		return ret;
	}

	public final void write(final DataOutputStream dos) throws IOException {
		dos.writeBoolean(deleted);

		if(!deleted) {
			Storage.writeString(dos, title);
			Storage.writeString(dos, body);
		}
	}
	
	public final void read(final DataInputStream dis) throws Exception {
		deleted = dis.readBoolean();
		if(!deleted) {
			title = Storage.readString(dis);
			body = Storage.readString(dis);
		}
	}
}