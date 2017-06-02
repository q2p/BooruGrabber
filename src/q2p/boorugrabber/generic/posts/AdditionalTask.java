package q2p.boorugrabber.generic.posts;

import java.util.LinkedList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import q2p.boorugrabber.generic.tags.TagType;
import q2p.boorugrabber.generic.tags.TagsBank;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.help.Parser;
import q2p.boorugrabber.network.Response;
import q2p.boorugrabber.queue.Task;
import q2p.boorugrabber.storage.SaveTemporaryTask;

public final class AdditionalTask implements Task {
	private final PostInstance post;

	public AdditionalTask(final PostInstance post) {
		this.post = post;
	}

	public final LinkedList<Task> work() {
		if(receive()) {
			Log.out("Не удалось обработать дополнительную информацию поста #"+post.id);
			return null;
		}
		
		final LinkedList<Task> ret = new LinkedList<Task>();
		ret.addLast(new SaveTemporaryTask(post));
		return ret;
	}
	
	private final boolean receive() {
		final Response response = post.booru.networker.receive("GET", post.booru.urlPrefix + "index.php?page=post&s=view&id=" + post.id, post.booru.headers);
		if(response == null)
			return true;

		try {
			final Document document = Jsoup.parse(response.toString());
			if(document == null)
				return true;

			Element element = document.getElementById("tag-sidebar");
			if(element == null)
				return true;

			Elements elements = element.getElementsByTag("li");

			final LinkedList<Integer> tags = post.tags;

			for(final Element tag : elements) {
				final TagType type = TagType.getByOutName(tag.className());
				if(type == null)
					return true;

				final Elements els = tag.getElementsByTag("a");
				for(final Element anchor : els)
					if(anchor.attr("href").startsWith("index.php?page=post")) {
						tags.addLast(TagsBank.index(anchor.text(), type));
						break;
					}
			}

			elements = document.getElementsByClass("note-box");
			if(elements.size() == 0)
				return false;
			
			final LinkedList<Note> notes = post.notes;

			String temp;
			for(final Element el : elements) {
				temp = el.id();
				if(!temp.startsWith("note-box-"))
					return true;

				final int id = Short.parseShort(temp.substring(9));

				temp = el.attr("style");
				if(temp == null)
					return true;

				final Note note = new Note();

				cssLoop:
					for(final String css : Parser.split(temp, ';')) {
						final int idx = css.indexOf(':');
						if(idx == -1)
							continue;

						final byte assign;
						switch(css.substring(0, idx).trim()) {
							case "left":
								assign = 0;
								break;
							case "top":
								assign = 1;
								break;
							case "width":
								assign = 2;
								break;
							case "height":
								assign = 3;
								break;
							default:
								continue cssLoop;
						}

						final Integer pos = parseCss(css.substring(idx+1).trim());
						if(pos == null)
							return true;

						if(note.set(assign, pos))
							return true;
					}

				element = document.getElementById("note-body-"+id);
				if(element == null)
					return true;

				if(note.set(element.text()))
					return true;

				notes.addLast(note);
			}

			elements = document.getElementsByClass("status-notice");
			for(final Element el : elements) {
				temp = el.text();

				if(temp.startsWith("This post was deleted.")) {
					post.deleted = true;

					if(post.deletionReason.length() != 0)
						post.deletionReason += '\n'+temp;
					else
						post.deletionReason = temp;
				}
			}
		} catch(final Exception e) {
			return true;
		}

		return false;
	}
	
	private final Integer parseCss(final String css) {
		if(!css.endsWith("px"))
			return null;

		return Integer.parseInt(css.substring(0, css.length()-2));
	}
}