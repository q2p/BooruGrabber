package q2p.boorugrabber.e621.pools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.IndexBlock;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.Storage;

public final class PoolsBlock extends IndexBlock {
	public PoolsBlock() {
		super("Пулы", 1000, 250);
	}

	static File stateFile;
	private static int bigestLast = 0;
	public static final void init() {
		stateFile = new File(E621.pathPools + "state");
	}
	
	protected final Task getRegularTask(final int id) {
		return new PoolTask(id);
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

	protected final Integer getLastId() {
		for(int i = 1; i != 4; i++) {
			final Response response = E621.networker.receive("GET", "https://e621.net/pool/index.json?page="+i, E621.headers);

			if(response == null)
				return null;
			
			try {
				final JSONArray pools = JSON.parseArray(response.toString());
				if(pools == null)
					return null;

				for(int j = pools.size() - 1; j != -1; j--) {
					final JSONObject pool = pools.getJSONObject(j);
					if(pool == null)
						return null;

					final Integer id = pool.getInteger("id");
					if(id == null)
						return null;

					if(id > bigestLast)
						bigestLast = id;
				}
			} catch(final Exception e) {
				return null;
			}
		}

		return bigestLast;
	}
}