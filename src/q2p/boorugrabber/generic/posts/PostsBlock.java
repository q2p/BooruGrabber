package q2p.boorugrabber.generic.posts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.generic.tags.TagsBank;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.XMLParser;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.IndexBlock;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.Storage;

public final class PostsBlock extends IndexBlock {
	private final GenericBooru booru;
	
	private final File stateFile;
	public PostsBlock(final GenericBooru booru, final int indexingLimit, final int updatingLimit) {
		super("Посты", indexingLimit, updatingLimit);
		this.booru = booru;
		stateFile = new File(booru.pathPosts + "state");
	}
	
	protected final Task getRegularTask(final int id) {
		return new PostInstance(booru, id);
	}

	protected final void saveState(final int indexing, final int updating) {
		TagsBank.save(booru.pathMain);
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
		TagsBank.load(booru.pathMain);
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
		final Response response = booru.networker.receive("GET", booru.urlPrefix + "index.php?page=dapi&s=post&q=index&limit=1", booru.headers);

		if(response == null)
			return null;
		
		try {
			final Document document = XMLParser.parse(response.data);

			final NodeList posts = document.getElementsByTagName("post");

			Node post = posts.item(0);
			if(post == null)
				return null;

			final NamedNodeMap attributes = post.getAttributes();
			if(attributes == null)
				return null;

			post = attributes.getNamedItem("id");
			if(post == null)
				return null;

			final String temp = post.getTextContent();
			if(temp == null)
				return null;

			return Integer.parseInt(temp);
		} catch(final Exception e) {
			return null;
		}
	}
}