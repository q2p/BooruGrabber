package q2p.boorugrabber.help;

import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class XMLParser {
	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	public static final Document parse(final byte[] bytes) {
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			final Document doc = builder.parse(bais);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (final Exception e) {
			return null;
		}
	}

	public static final String attribute(final NamedNodeMap attributes, final String name) {
		final Node node = attributes.getNamedItem(name);
		if(node == null)
			return null;

		return node.getTextContent();
	}
	
	public static final Integer integer(final NamedNodeMap attributes, final String name) {
		final Node node = attributes.getNamedItem(name);
		if(node == null)
			return null;

		return Integer.parseInt(node.getTextContent());
	}

	public static final Integer negativeIfEmpty(final NamedNodeMap attributes, final String name) {
		final Node node = attributes.getNamedItem(name);
		if(node == null)
			return null;

		final String temp = node.getTextContent();
		if(temp.length() == 0)
			return -1;

		final int ret = Integer.parseInt(temp);

		if(ret < 0)
			return -1;

		return ret;
	}
}