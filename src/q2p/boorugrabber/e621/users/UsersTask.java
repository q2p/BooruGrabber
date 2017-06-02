package q2p.boorugrabber.e621.users;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.UnpredictableAnswer;
import q2p.boorugrabber.queue.UnpredictableTask;
import q2p.boorugrabber.storage.SaveTemporaryTask;

final class UsersTask implements UnpredictableTask {
	public final int page;

	UsersTask(final int page) {
		this.page = page;
	}

	public final UnpredictableAnswer work() {
		Log.lapse("Пользователи #", 100, page);
		final UnpredictableAnswer ret = receive();
		if(ret == null)
			Log.out("Не удалось обработать список пользователей #"+page);

		return ret;
	}

	private final UnpredictableAnswer receive() {
		final Response response = E621.networker.receive("GET", "https://e621.net/user/index.json?page="+page, E621.headers);

		if(response == null)
			return null;

		final UnpredictableAnswer ret = new UnpredictableAnswer();
		
		try {
			final JSONArray users = JSON.parseArray(response.toString());
			if(users == null)
				return null;

			if(users.size() == 0)
				return ret.hasNoMore();

			for(int i = users.size() - 1; i != -1; i--) {
				final JSONObject user = users.getJSONObject(i);

				final String name = user.getString("name");
				if(name == null)
					return null;

				final Integer id = user.getInteger("id");
				if(id == null)
					return null;

				final Byte temp = user.getByte("level");
				if(temp == null)
					return null;
				final Rank rank = Rank.getByParseCode(temp);
				if(rank == null)
					return null;

				final Long creationDate = E621.parseSpaceDate(user.getString("created_at"));
				if(creationDate == null)
					return null;

				Integer avatar = user.getInteger("avatar_id");
				if(avatar == null)
					avatar = -1;

				ret.task(new SaveTemporaryTask(new User(id, name, rank, creationDate, avatar)));
			}
		} catch(final Exception e) {
			return null;
		}

		return ret;
	}
}