package q2p.boorugrabber.generic.tags;

public enum TagType {
	GENERAL   ((byte)0,"tag-type-general",  (byte)0),
	ARTIST    ((byte)3,"tag-type-artist",   (byte)1),
	COPYRIGHT ((byte)1,"tag-type-copyright",(byte)2),
	CHARACTER ((byte)2,"tag-type-character",(byte)3);
	
	public final byte localId;
	public final String outName;
	public final byte privilege;

	private TagType(final byte localId, final String outName, final byte privilege) {
		this.localId = localId;
		this.outName = outName;
		this.privilege = privilege;
	}
	
	public static final TagType getByOutName(final String outName) {
		for(final TagType type : values())
			if(type.outName.equals(outName))
				return type;

		return null;
	}
	public static final TagType getByLocalId(final int localId) {
		for(final TagType type : values())
			if(type.localId == localId)
				return type;

		return null;
	}
}