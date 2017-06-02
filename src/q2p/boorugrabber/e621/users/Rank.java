package q2p.boorugrabber.e621.users;

public enum Rank {
	BLOCKED     ((byte)0, (byte)10),
	UNACTIVATED ((byte)1, (byte)0),
	MEMBER      ((byte)2, (byte)20),
	PRIVILEGED  ((byte)3, (byte)30),
	CONTRIBUTOR ((byte)4, (byte)33),
	JANITOR     ((byte)5, (byte)35),
	MODERATOR   ((byte)6, (byte)40),
	ADMIN       ((byte)7, (byte)50);
	
	public final byte rawCode;
	public final byte parseCode;
	private Rank(final byte rawCode, final byte parseCode) {
		this.rawCode = rawCode;
		this.parseCode = parseCode;
	}
	public static Rank getByParseCode(final byte parseCode) {
		for(final Rank rating : values())
			if(rating.parseCode == parseCode)
				return rating;

		return null;
	}
	public static Rank getByRawCode(final byte rawCode) {
		for(final Rank rating : values())
			if(rating.rawCode == rawCode)
				return rating;

		return null;
	}
}