package q2p.boorugrabber.derpibooru.posts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.derpibooru.Derpibooru;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.IndexBlock;
import q2p.boorugrabber.storage.Storage;

public final class PostsBlock extends IndexBlock {
	public PostsBlock() {
		super("Посты", 4000, 1000);
	}

	static File stateFile;
	public static final void init() {
		stateFile = new File(Derpibooru.pathPosts + "state");
	}
	
	protected final PostInstance getRegularTask(final int id) {
		return new PostInstance(id);
	}

	protected final void saveState(final int indexing, final int updating) {
		Derpibooru.tags.save();
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
		Derpibooru.tags.load();
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
			ret[0] = 0;
			ret[1] = 0;
		}
		return ret;
	}

	protected final Integer getLastId() {
		final Response response = Derpibooru.networker.receive("GET", "https://derpibooru.org/images.json?deleted=", Derpibooru.headers);

		if(response == null)
			return null;

		try {
			final JSONObject object = JSON.parseObject(response.toString());
			if(object == null)
				return null;

			final JSONArray posts = object.getJSONArray("images");
			if(posts == null || posts.size() == 0)
				return null;

			return posts.getJSONObject(0).getInteger("id");
		} catch(final Exception e) {
			return null;
		}
	}
}