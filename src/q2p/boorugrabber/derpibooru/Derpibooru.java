package q2p.boorugrabber.derpibooru;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;
import q2p.boorugrabber.derpibooru.pools.PoolsBlock;
import q2p.boorugrabber.derpibooru.posts.PostsBlock;
import q2p.boorugrabber.derpibooru.tags.TagsBlock;
import q2p.boorugrabber.derpibooru.tags.TagsStorage;
import q2p.boorugrabber.global.BooruQueue;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.help.Parser;
import q2p.boorugrabber.network.Networker;
import q2p.boorugrabber.storage.Config.Field;
import q2p.boorugrabber.storage.Config.Primitive;

public final class Derpibooru extends BooruQueue {
	public static final Derpibooru me = new Derpibooru();
	public static String pathMain;
	public static String pathPosts;
	public static String pathPools;
	public static TagsStorage tags;

	public static Networker networker;
	
	public static final String[] headers = new String[] {
		"Cookie", "",
	};
	
	private Derpibooru() {
		super("derpibooru", "Derpibooru", new Field("cookies", Primitive.TEXT), new Field("cooldown", Primitive.INT));
		Derpibooru.pathMain = super.pathMain;
		pathPosts = pathMain+"posts/";
		pathPools = pathMain+"pools/";
		tags = new TagsStorage();
		PostsBlock.init();
		PoolsBlock.init();
		setBlocks(new PostsBlock(), new PoolsBlock(), new TagsBlock());
	}
	
	private int coolDown; // TODO: использовать
	
	protected final boolean initAdditional() {
		/*for(int i = headers.length-1; i != -1; i--) {
			if(headers[i].equals("Cookie")) {
				headers[i+1] = config.getText("cookies");
				break;
			}
		}*/
		headers[1] = config.getText("cookies");
		coolDown = config.getInt("cooldown");
		if(coolDown < 1) {
			Log.proxyValue("cooldown");
			return true;
		}

		Derpibooru.networker = super.networker;

		return false;
	}

	public final void cStart() {
		// TODO Auto-generated method stub

	}
	
	public final void cStop() {
		// TODO Auto-generated method stub

	}

	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
	public static final Long parseDate(final String time) {
		if(time == null)
			return null;

		return Assist.parseTime(time, formater);
	}
	public static final int[] parseTags(final String tags) {
		if(tags == null)
			return null;

		int[] ret;
		if(tags.length() == 0)
			ret = new int[0];
		else {
			final LinkedList<String> raw = Parser.split(tags, ',');
			ret = new int[raw.size()];
			for(int i = 0; !raw.isEmpty(); i++)
				ret[i] = Derpibooru.tags.index(raw.removeFirst().trim().replace(' ', '_'));
		}
		return ret;
	}
}