package q2p.boorugrabber.e621;

import java.text.SimpleDateFormat;
import java.util.Locale;
import com.alibaba.fastjson.JSONObject;
import q2p.boorugrabber.e621.aliases.AliasesBlock;
import q2p.boorugrabber.e621.authors.AuthorsBlock;
import q2p.boorugrabber.e621.implications.ImplicationsBlock;
import q2p.boorugrabber.e621.pools.PoolsBlock;
import q2p.boorugrabber.e621.posts.PostsBlock;
import q2p.boorugrabber.e621.users.UsersBlock;
import q2p.boorugrabber.e621.wiki.WikiBlock;
import q2p.boorugrabber.global.BooruQueue;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Networker;
import q2p.boorugrabber.storage.Config.Field;
import q2p.boorugrabber.storage.Config.Primitive;

public final class E621 extends BooruQueue {
	public static final E621 me = new E621();
	public static String pathMain;
	public static String pathPosts;
	public static String pathPools;
	public static String pathAuthors;
	public static String pathWiki;
	public static String pathUsers;

	public static Networker networker;
	
	public static final String[] headers = new String[] {
		"Host", "e621.net",
		"User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:45.0) Gecko/20100101 Firefox/45.0",
		"DNT", "1",
		"Cookie", "",
		"Connection", "keep-alive",
	};
	
	private E621() {
		super("e621", "E621", new Field("cookies", Primitive.TEXT), new Field("cooldown", Primitive.INT));
		E621.pathMain = super.pathMain;
		pathPosts = pathMain+"posts/";
		pathPools = pathMain+"pools/";
		pathAuthors = pathMain+"authors/";
		pathWiki = pathMain+"wiki/";
		pathUsers = pathMain+"users/";
		PostsBlock.init();
		PoolsBlock.init();
		setBlocks(new PostsBlock(), new PoolsBlock(), new AuthorsBlock(), new WikiBlock(), new UsersBlock(), new AliasesBlock(), new ImplicationsBlock());
	}
	
	private int coolDown; // TODO: использовать
	
	protected final boolean initAdditional() {
		for(int i = headers.length-1; i != -1; i--)
			if(headers[i].equals("Cookie")) {
				headers[i+1] = config.getText("cookies");
				break;
			}
		coolDown = config.getInt("cooldown");
		if(coolDown < 1) {
			Log.proxyValue("cooldown");
			return true;
		}

		E621.networker = super.networker;

		return false;
	}

	public final void cStart() {
		// TODO Auto-generated method stub

	}
	
	public final void cStop() {
		// TODO Auto-generated method stub

	}

	private static final SimpleDateFormat tFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
	private static final SimpleDateFormat spaceFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
	private static final SimpleDateFormat extendedSpaceFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
	public static final Long parseTDate(final String time) {
		if(time == null)
			return null;

		return Assist.parseTime(time, tFormater);
	}
	public static final Long parseSpaceDate(final String time) {
		if(time == null)
			return null;

		return Assist.parseTime(time, spaceFormater);
	}
	public static final Long parseExtendedSpaceDate(final String time) {
		if(time == null)
			return null;

		return Assist.parseTime(time, extendedSpaceFormater);
	}
	public static final Long parseJSONDate(final JSONObject time) {
		if(time == null)
			return null;

		final Long mili = time.getLong("s");
		if(mili == null)
			return null;

		final Long nano = time.getLong("n");
		if(nano == null)
			return null;

		return mili*1000+nano/1000000;
	}
}