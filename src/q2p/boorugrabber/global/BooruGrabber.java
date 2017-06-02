package q2p.boorugrabber.global;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import q2p.boorugrabber.danbooru.Danbooru;
import q2p.boorugrabber.derpibooru.Derpibooru;
import q2p.boorugrabber.e621.E621;
import q2p.boorugrabber.generic.GenericBooru;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.help.Parser;
import q2p.boorugrabber.paheal.Paheal;
import q2p.boorugrabber.queue.QueueManager;

public final class BooruGrabber {
	// TODO: http://hypnohub.net/
	// TODO: https://chan.sankakucomplex.com/
	// TODO: https://derpibooru.org/
	// TODO: http://www.megabooru.com/
	// TODO: http://www.hard55.com/
	// + TODO: https://rule34.paheal.net/
	// + TODO: https://rule34.xxx/
	// + TODO: gelbooru
	// + TODO: http://furry.booru.org/
	// + TODO: danbooru (modifie e621)
	// + TODO: https://tbib.org/
	// + TODO: http://xbooru.com/

	private static final BufferedReader consoleReader;

	static {
		consoleReader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static final BooruQueue[] queues = new BooruQueue[] {
		Derpibooru.me,
		Paheal.me,
		GenericBooru.fabric("r34", "Rule34", "https://rule34.xxx/", 4000, 1000, false, 1000, 250, (byte)16, 0, 0, null),
		E621.me,
		GenericBooru.fabric("gel", "Gelbooru", "http://gelbooru.com/", 4000, 1000, true, 1000, 250, (byte)16, 500, 125, null),
		GenericBooru.fabric("fb", "FurryBooru", "http://furry.booru.org/", 2000, 500, false, 400, 100, (byte)16, 0, 0, null),
		Danbooru.me,
		GenericBooru.fabric("tbib", "The Big ImageBoard", "https://tbib.org/", 4000, 1000, false, 1000, 250, (byte)16, 0, 0, new String[] {"Host", "tbib.org", "User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0", "Cookie", ""}),
		GenericBooru.fabric("xb", "XBooru", "http://xbooru.com/", 1000, 250, false, 400, 100, (byte)16, 0, 0, null),
	};

	private static final void input() {
		Log.out("Введите команду");
		while(true) {
			final LinkedList<String> parts;
			try {
				final String line = consoleReader.readLine();
				Log.out("%: "+line);
				parts = Parser.split(line, ' ');
			} catch(final Exception e) {
				Assist.abort("Не удалось прочитать команду из консоли");
				return;
			}

			final String command = parts.removeFirst();
			switch(command) {
				case "start":
					cStart(parts);
					break;
				case "stop":
					cStop(parts);
					break;
				case "list":
					cList();
					break;
				case "kill":
					cKill();
					return;
				default:
					Log.out("Не допустимая команда");
			}
		}
	}
	
	private static final void cList() {
		Log.out("Список очередей:");
		for(final BooruQueue queue : queues)
			Log.out(queue.abbreviation + " - " + queue.name);
	}
	
	private static final void cStart(final LinkedList<String> args) {
		final LinkedList<BooruQueue> selected = select(args);
		while(!selected.isEmpty())
			selected.removeFirst().cStart();
	}
	
	private static final void cStop(final LinkedList<String> args) {
		final LinkedList<BooruQueue> selected = select(args);
		while(!selected.isEmpty())
			selected.removeFirst().cStop();
	}

	private static final void cKill() {
		for(final BooruQueue queue : queues)
			queue.cStop();
	}

	private static final LinkedList<BooruQueue> select(final LinkedList<String> names) {
		final LinkedList<BooruQueue> selected = new LinkedList<BooruQueue>();
		String name;
		loop:
			while(!names.isEmpty()) {
				name = names.removeFirst();
				for(final BooruQueue q : queues)
					if(q.abbreviation.equals(name)) {
						for(final BooruQueue s : selected)
							if(s == q)
								continue loop;
						selected.add(q);
						continue loop;
					}
				if("all".equals(name)) all:
					for(final BooruQueue q : queues) {
						for(final BooruQueue s : selected)
							if(s == q)
								continue all;

						selected.add(q);
					}
			}
		return selected;
	}

	public static final void main(final String[] args) {
		Log.out("Инициализация...");

		boolean errors = false;
		for(final BooruQueue queue : queues)
			errors |= queue.init();

		if(errors)
			return;

		QueueManager.init();

		Log.out("Инициализация завершена успешно");

		input();
	}
}