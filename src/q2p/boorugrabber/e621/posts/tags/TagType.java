package q2p.boorugrabber.e621.posts.tags;

public enum TagType {
	GENERAL   ((byte)0,(byte)0,(byte)0),
	ARTIST    ((byte)4,(byte)1,(byte)2),
	COPYRIGHT ((byte)2,(byte)3,(byte)3),
	CHARACTER ((byte)3,(byte)4,(byte)4),
	SPECIES   ((byte)1,(byte)5,(byte)1);
	
	public final byte localId;
	public final byte outId;
	public final byte privilege;

	private TagType(final byte localId, final byte outId, final byte privilege) {
		this.localId = localId;
		this.outId = outId;
		this.privilege = privilege;
	}
	
	public static final TagType getByOutId(final int outId) {
		for(final TagType type : values())
			if(type.outId == outId)
				return type;

		return null;
	}
	public static final TagType getByLocalId(final byte localId) {
		for(final TagType type : values())
			if(type.localId == localId)
				return type;

		return null;
	}
}