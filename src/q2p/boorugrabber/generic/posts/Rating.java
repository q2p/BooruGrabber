package q2p.boorugrabber.generic.posts;

enum Rating {
	EXPLICIT(2, "e"),
	QUESTIONABLE(1, "q"),
	SAFE(0, "s");

	final String parseCode;
	final byte rawCode;
	private Rating(final int rawCode, final String parseCode) {
		this.rawCode = (byte)rawCode;
		this.parseCode = parseCode;
	}
	static Rating getByCode(final String code) {
		for(final Rating rating : values())
			if(rating.parseCode.equals(code))
				return rating;

		return null;
	}
	static Rating getByRawCode(final byte rawCode) {
		for(final Rating rating : values())
			if(rating.rawCode == rawCode)
				return rating;

		return null;
	}
}