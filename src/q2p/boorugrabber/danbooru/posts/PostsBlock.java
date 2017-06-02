package q2p.boorugrabber.danbooru.posts;
// TODO: same as e621
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.IndexBlock;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.Storage;

public final class PostsBlock extends IndexBlock {
	public PostsBlock() {
		super("Посты", 10000, 2500);
	}

	static File stateFile;
	public static final void init() {
		stateFile = new File(Danbooru.pathPosts + "state");
	}
	
	protected final Task getRegularTask(final int id) {
		return new PostTask(id);
	}

	protected final void saveState(final int indexing, final int updating) {
		Danbooru.tags.save();
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
		Danbooru.tags.load();
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
		final Response response = Danbooru.networker.receive("GET", "https://danbooru.donmai.us/posts.json?limit=1", null);
		if(response == null)
			return null;
		
		try {
			final JSONArray posts = JSON.parseArray(response.toString());
			if(posts == null)
				return null;

			final JSONObject post = posts.getJSONObject(0);
			if(post == null)
				return null;

			return post.getInteger("id");
		} catch(final Exception e) {
			return null;
		}
	}
}