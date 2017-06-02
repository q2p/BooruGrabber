package q2p.boorugrabber.generic;

import java.text.SimpleDateFormat;
import java.util.Locale;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import q2p.boorugrabber.generic.aliases.AliasesBlock;
import q2p.boorugrabber.generic.pools.NewPoolsBlock;
import q2p.boorugrabber.generic.pools.OldPoolsBlock;
import q2p.boorugrabber.generic.pools.PoolsBlock;
import q2p.boorugrabber.generic.posts.PostsBlock;
import q2p.boorugrabber.generic.wiki.WikiBlock;
import q2p.boorugrabber.global.BooruQueue;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.storage.Config.Field;
import q2p.boorugrabber.storage.Config.Primitive;

public final class GenericBooru extends BooruQueue {
	public final String urlPrefix;
	public final String pathMain;
	public final String pathPosts;
	public final String pathPools;
	public final String pathWiki;

	public final boolean ssl;

	public final String[] headers;

	public static final GenericBooru fabric(final String abbreviation, final String name, final String urlPrefix, final int postsIndexingLimit, final int postsUpdatingLimit, final boolean newPools, final int poolsIndexingLimit, final int poolsUpdatingLimit, final byte aliasesSkip, final int wikiIndexingLimit, final int wikiUpdatingLimit, final String[] headers) {
		final PoolsBlock pools = newPools ? new NewPoolsBlock(poolsIndexingLimit, poolsUpdatingLimit) : new OldPoolsBlock(poolsIndexingLimit, poolsUpdatingLimit);
		final GenericBooru ret = new GenericBooru(abbreviation, name, urlPrefix, postsIndexingLimit, postsUpdatingLimit, pools, aliasesSkip, wikiIndexingLimit, wikiUpdatingLimit, headers);
		pools.init(ret);

		return ret;
	}

	private GenericBooru(final String abbreviation, final String name, final String urlPrefix, final int postsIndexingLimit, final int postsUpdatingLimit, final PoolsBlock poolsBlock, final byte aliasesSkip, final int wikiIndexingLimit, final int wikiUpdatingLimit, final String[] headers) {
		// TODO: куки не всегда нужны (если их нет в заголовках)
		super(abbreviation, name, new Field("cooldown", Primitive.INT), new Field("cookies", Primitive.TEXT));
		ssl = urlPrefix.startsWith("https:");
		this.urlPrefix = urlPrefix;
		pathMain = super.pathMain;
		pathPosts = pathMain+"posts/";
		pathPools = pathMain+"pools/";
		pathWiki = pathMain+"wiki/";

		this.headers = headers;

		final PostsBlock posts = new PostsBlock(this, postsIndexingLimit, postsUpdatingLimit);
		final PoolsBlock pools = poolsBlock;
		final AliasesBlock aliases = new AliasesBlock(this, aliasesSkip);
		final WikiBlock wiki = new WikiBlock(this, wikiIndexingLimit, wikiUpdatingLimit);

		setBlocks(posts, pools, wiki, aliases);
	}
	
	private int coolDown; // TODO: использовать
	
	protected final boolean initAdditional() {
		if(headers != null) for(int i = headers.length-1; i != -1; i--)
			if(headers[i].equals("Cookie")) {
				headers[i+1] =  config.getText("cookies");
				break;
			}

		coolDown = config.getInt("cooldown");
		if(coolDown < 1) {
			Log.proxyValue("cooldown");
			return true;
		}

		return false;
	}
	
	private static final SimpleDateFormat formater = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
	public static final Long parseDate(final String time) {
		if(time == null)
			return null;

		return Assist.parseTime(time, formater);
	}

	public final void cStart() {
		// TODO Auto-generated method stub

	}
	
	public final void cStop() {
		// TODO Auto-generated method stub

	}
	
	public static final String findAnchorText(final Element element) {
		final Elements elements = element.getElementsByTag("a");
		if(elements.size() != 1)
			return null;

		return elements.get(0).text();
	}
}