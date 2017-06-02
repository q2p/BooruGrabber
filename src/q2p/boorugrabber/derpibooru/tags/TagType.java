package q2p.boorugrabber.derpibooru.tags;

public enum TagType {
	OC        ((byte)0,"oc",     (byte)1),
	ARTIST    ((byte)1,"artist", (byte)0),
	SPOILER   ((byte)2,"spoiler",(byte)2);
	
	public final byte localId;
	public final String namespace;
	public final byte privilege;

	private TagType(final byte localId, final String namespace, final byte privilege) {
		this.localId = localId;
		this.namespace = namespace;
		this.privilege = privilege;
	}
	
	public static final TagType getByNamespace(final String namespace) {
		for(final TagType type : values())
			if(type.namespace.equals(namespace))
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