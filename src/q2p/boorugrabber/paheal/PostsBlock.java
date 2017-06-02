package q2p.boorugrabber.paheal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Parser;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.IndexBlock;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.Storage;

public final class PostsBlock extends IndexBlock {
	private final File stateFile;
	public PostsBlock() {
		super("Посты", 8192, 2048);
		stateFile = new File(Paheal.pathPosts + "state");
	}
	
	protected final Task getRegularTask(final int id) {
		return new PostInstance(id);
	}

	protected final void saveState(final int indexing, final int updating) {
		Paheal.tags.save();
		final DataOutputStream dos = new DataOutputStream(Storage.initWrite(stateFile));
		try {
			dos.writeInt(indexing);
			dos.writeInt(updating);
			dos.flush();
			dos.close();
		} catch(final Exception e) {
			Assist.safeClose(dos);
			Assist.abort(stateFile, "Не удалось записать в файл", e);
		}
	}

	protected final int[] getState() {
		Paheal.tags.load();
		final int[] ret = new int[2];
		if(stateFile.exists()) {
			final DataInputStream dis = new DataInputStream(Storage.initRead(stateFile));
			try {
				ret[0] = dis.readInt();
				ret[1] = dis.readInt();
				dis.close();
			} catch(final Exception e) {
				Assist.safeClose(dis);
				Assist.abort(stateFile, "Не удалось записать в файл", e);
			}
		} else {
			ret[0] = 1;
			ret[1] = 1;
		}
		return ret;
	}

	protected final Integer getLastId() {
		final Response response = Paheal.networker.receive("GET", "http://rule34.paheal.net/post/list", Paheal.headers);

		if(response == null)
			return null;
		
		try {
			final Document document = Jsoup.parse(response.toString());
			if(document == null)
				return null;

			final Element element = Parser.chain(document.getElementById("imagelist")).clazz("blockbody").clazz("shm-image-list").clazz("thumb").clazz("shm-thumb-link").get();
			if(element == null)
				return null;

			final String temp = element.attr("href");
			if(!temp.startsWith("/post/view/"))
				return null;

			return Integer.parseInt(temp.substring(11));
		} catch(final Exception e) {
			return null;
		}
	}
}