package q2p.boorugrabber.generic.pools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.storage.Storage;

public final class OldPoolsBlock extends PoolsBlock {
	public OldPoolsBlock(final int indexingLimit, final int updatingLimit) {
		super(indexingLimit, updatingLimit);
	}

	protected final void saveState(final int indexing, final int updating) {
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
		final Response response = booru.networker.receive("GET", booru.urlPrefix+"index.php?page=pool&s=list", booru.headers);

		if(response == null)
			return null;

		try {
			final Document document = Jsoup.parse(response.toString());
			if(document == null)
				return null;

			Elements elements;

			Element element = document.getElementById("pool-index");
			if(element == null) {
				element = document.getElementById("content");
				if(element == null)
					return null;

				elements = element.getElementsByTag("h1");
				if(elements.size() != 1)
					return null;

				if(!elements.get(0).text().equals("Nobody here but us chickens!"))
					return null;

				return 0;
			}

			elements = element.getElementsByTag("tbody");

			if(elements.size() != 1)
				return null;

			elements = elements.get(0).getElementsByTag("tr");
			if(elements.size() == 0)
				return null;

			elements = elements.get(0).getElementsByTag("td");
			if(elements.size() != 4)
				return null;

			elements = elements.get(0).getElementsByTag("a");
			if(elements.size() != 1)
				return null;

			final String temp = elements.get(0).attr("href");

			int idx = temp.indexOf('?') + 1;

			if(idx == 0)
				return null;

			idx = temp.indexOf("&id=", idx);

			if(idx == -1)
				return null;

			return Integer.parseInt(temp.substring(idx+4, temp.length()));
		} catch(final Exception e) {
			return null;
		}
	}

	protected final OldPoolInstance getRegularTask(final int id) {
		return new OldPoolInstance(booru, id);
	}
}