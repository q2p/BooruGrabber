package q2p.boorugrabber.danbooru;

import java.text.SimpleDateFormat;
import java.util.Locale;
import q2p.boorugrabber.danbooru.aliases.AliasesBlock;
import q2p.boorugrabber.danbooru.implications.ImplicationsBlock;
import q2p.boorugrabber.danbooru.pools.PoolsBlock;
import q2p.boorugrabber.danbooru.posts.PostsBlock;
import q2p.boorugrabber.danbooru.posts.tags.TagsStorage;
import q2p.boorugrabber.danbooru.wiki.WikiBlock;
import q2p.boorugrabber.global.BooruQueue;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Networker;
import q2p.boorugrabber.storage.Config.Field;
import q2p.boorugrabber.storage.Config.Primitive;

public final class Danbooru extends BooruQueue {
	public static final Danbooru me = new Danbooru();
	public static String pathMain;
	public static String pathPosts;
	public static String pathPools;
	public static String pathAuthors;
	public static String pathWiki;
	public static TagsStorage tags;

	public static Networker networker;
	
	private Danbooru() {
		super("danb", "Danbooru", new Field("cooldown", Primitive.INT));
		Danbooru.pathMain = super.pathMain;
		pathPosts = pathMain+"posts/";
		pathPools = pathMain+"pools/";
		pathAuthors = pathMain+"authors/";
		pathWiki = pathMain+"wiki/";
		tags = new TagsStorage();
		PostsBlock.init();
		setBlocks(new PostsBlock(), new PoolsBlock(), new WikiBlock(), new AliasesBlock(), new ImplicationsBlock());
	}
	
	private int coolDown; // TODO: использовать
	
	protected final boolean initAdditional() {
		coolDown = config.getInt("cooldown");
		if(coolDown < 1) {
			Log.proxyValue("cooldown");
			return true;
		}

		Danbooru.networker = super.networker;

		return false;
	}

	public final void cStart() {
		// TODO Auto-generated method stub

	}
	
	public final void cStop() {
		// TODO Auto-generated method stub

	}

	private static final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
	public static final Long parseTDate(final String time) {
		if(time == null)
			return null;

		return Assist.parseTime(time, formater);
	}
}