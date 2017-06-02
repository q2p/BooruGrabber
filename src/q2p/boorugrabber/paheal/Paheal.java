package q2p.boorugrabber.paheal;

import q2p.boorugrabber.global.BooruQueue;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Networker;
import q2p.boorugrabber.storage.Config.Field;
import q2p.boorugrabber.storage.Config.Primitive;

public final class Paheal extends BooruQueue {
	static String pathMain;
	static String pathPosts;
	static Networker networker;
	static TagsBank tags;

	public static final Paheal me = new Paheal();

	static final String[] headers = new String[] {"Cookie", "ui-tnc-agreed=true"};

	private Paheal() {
		super("paheal", "Rule34 Paheal", new Field("cooldown", Primitive.INT));
		
		pathMain = super.pathMain;
		pathPosts = super.pathMain+"posts/";
		networker = super.networker;
		tags = new TagsBank();

		setBlocks(new PostsBlock());
	}
	
	private int coolDown; // TODO: использовать
	
	protected final boolean initAdditional() {
		coolDown = config.getInt("cooldown");
		if(coolDown < 1) {
			Log.proxyValue("cooldown");
			return true;
		}

		return false;
	}

	public final void cStart() {
		// TODO Auto-generated method stub

	}
	
	public final void cStop() {
		// TODO Auto-generated method stub

	}
}