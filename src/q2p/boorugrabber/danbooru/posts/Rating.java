package q2p.boorugrabber.danbooru.posts;

public enum Rating {
	EXPLICIT(0, "e"),
	QUESTIONABLE(1, "q"),
	SAFE(2, "s");

	public final String parseCode;
	public final byte rawCode;
	private Rating(final int rawCode, final String parseCode) {
		this.rawCode = (byte)rawCode;
		this.parseCode = parseCode;
	}
	public static Rating getByCode(final String code) {
		for(final Rating rating : values())
			if(rating.parseCode.equals(code))
				return rating;

		return null;
	}
	public static Rating getByRawCode(final byte rawCode) {
		for(final Rating rating : values())
			if(rating.rawCode == rawCode)
				return rating;

		return null;
	}
}