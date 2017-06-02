package q2p.boorugrabber.derpibooru.pools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import q2p.boorugrabber.derpibooru.Derpibooru;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.IndexBlock;
import q2p.boorugrabber.storage.Storage;

public final class PoolsBlock extends IndexBlock {
	public PoolsBlock() {
		super("Пулы", 5000, 125);
	}

	static File stateFile;
	public static final void init() {
		stateFile = new File(Derpibooru.pathPools + "state");
	}
	
	protected final PoolInstance getRegularTask(final int id) {
		return new PoolInstance(id);
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
		final Response response = Derpibooru.networker.receive("GET", "https://derpibooru.org/galleries.json", Derpibooru.headers);

		if(response == null)
			return null;

		try {
			final JSONArray pools = JSON.parseArray(response.toString());
			if(pools == null || pools.size() == 0)
				return null;
			
			return pools.getJSONObject(0).getInteger("id");
		} catch(final Exception e) {
			return null;
		}
	}
}