package q2p.boorugrabber.generic.wiki;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.generic.tags.TagsBank;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.IndexBlock;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.Storage;

public final class WikiBlock extends IndexBlock {
	private final GenericBooru booru;
	
	private int bigestLast = 0;
	private final File stateFile;
	public WikiBlock(final GenericBooru booru, final int indexingLimit, final int updatingLimit) {
		super("Wiki", indexingLimit, updatingLimit);
		this.booru = booru;
		stateFile = new File(booru.pathWiki + "state");
	}
	
	protected final Task getRegularTask(final int id) {
		return new WikiArticle(booru, id);
	}

	protected final void saveState(final int indexing, final int updating) {
		TagsBank.save(booru.pathMain);
		final DataOutputStream dos = new DataOutputStream(Storage.initWrite(stateFile));
		try {
			dos.writeInt(indexing);
			dos.writeInt(updating);
			dos.writeInt(bigestLast);
			dos.flush();
			dos.close();
		} catch(final Exception e) {
			Assist.safeClose(dos);
			Assist.abort(stateFile, "Не удалось записать в файл", e);
		}
	}

	protected final int[] getState() {
		TagsBank.load(booru.pathMain);
		final int[] ret = new int[2];
		if(stateFile.exists()) {
			final DataInputStream dis = new DataInputStream(Storage.initRead(stateFile));
			try {
				ret[0] = dis.readInt();
				ret[1] = dis.readInt();
				bigestLast = dis.readInt();
				dis.close();
			} catch(final Exception e) {
				Assist.safeClose(dis);
				Assist.abort(stateFile, "Не удалось записать в файл", e);
			}
		} else {
			ret[0] = 1;
			ret[1] = 1;
			bigestLast = 0;
		}
		return ret;
	}

	protected final Integer getLastId() {
		final Response response = booru.networker.receive("GET", booru.urlPrefix + "index.php?page=wiki&s=list", booru.headers);

		if(response == null)
			return null;
		
		try {
			final Document document = Jsoup.parse(response.toString());
			if(document == null)
				return null;

			final Element element = document.getElementById("tag-sidebar");
			if(element == null)
				return null;

			final Elements elements = element.getElementsByTag("a");
			for(final Element el : elements) {
				final String temp = el.attr("href");
				int idx = temp.indexOf("&id=")+4;
				if(idx == 3)
					return null;

				idx = Integer.parseInt(temp.substring(idx));
				if(idx > bigestLast)
					bigestLast = idx;
			}
		} catch(final Exception e) {
			return null;
		}

		return bigestLast;
	}
}