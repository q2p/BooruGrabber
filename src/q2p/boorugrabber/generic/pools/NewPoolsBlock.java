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

public final class NewPoolsBlock extends PoolsBlock {
	public NewPoolsBlock(final int indexingLimit, final int updatingLimit) {
		super(indexingLimit, updatingLimit);
	}
	
	protected final void saveState(final int indexing, final int updating) {
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

	private int bigestLast = 0;
	protected final Integer getLastId() {
		for(int i = 0; i != 100; i += 25) {
			final Response response = booru.networker.receive("GET", booru.urlPrefix + "index.php?page=pool&s=list&pid=" + i, booru.headers);

			if(response == null)
				return null;

			try {
				final Document document = Jsoup.parse(response.toString());
				if(document == null)
					return null;

				Elements elements;

				final Element element = document.getElementById("content");
				if(element == null)
					return null;

				elements = element.getElementsByTag("tbody");
				if(elements.size() != 1) {
					elements = element.getElementsByTag("h1");
					if(elements.size() != 1)
						return null;

					if(!elements.get(0).text().equals("Nobody here but us chickens!"))
						return null;

					return 0;
				}

				elements = elements.get(0).getElementsByTag("tr");
				if(elements.size() == 0)
					return null;

				elements = elements.get(0).getElementsByTag("td");
				if(elements.size() != 4)
					return null;

				elements = elements.get(1).getElementsByTag("a");
				if(elements.size() != 2)
					return null;

				for(final Element el : elements) {
					final String temp = el.attr("href");
					if(temp.startsWith("index.php?page=pool&")) {
						int idx = temp.indexOf("&id=", 20)+4;
						if(idx == 3)
							return null;

						int idx2 = temp.indexOf('&', idx);
						if(idx2 == -1)
							idx2 = temp.length();

						idx = Integer.parseInt(temp.substring(idx, idx2));
						if(idx > bigestLast)
							bigestLast = idx;

						break;
					}
				}
			} catch(final Exception e) {
				return null;
			}
		}

		return bigestLast;
	}

	protected final NewPoolInstance getRegularTask(final int id) {
		return new NewPoolInstance(booru, id);
	}
}