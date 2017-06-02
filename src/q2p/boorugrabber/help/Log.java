package q2p.boorugrabber.help;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public final class Log {
	private static FileOutputStream fos = null;
	static {
		try {
			fos = new FileOutputStream(Assist.folder+"log.txt");
		} catch(final Exception e) {
			Assist.abort("Не удалось начать запись в лог файл");
		}
	}
	public static final void proxyValue(final String name) {
		out("Не допустимое значение "+name+" в конфигурации");
	}
	
	public static final void out(final String message) {
		try {
			write(message);
		} catch(final Exception e) {
			Assist.abort("Не удлось записать сообщение в лог файл.");
		}
	}
	private static synchronized void write(final String message) throws Exception {
		System.out.println(message);
		if(fos != null) {
			fos.write((message+'\n').getBytes(StandardCharsets.UTF_8));
			fos.flush();
		}
	}
	
	public static final void safe(final String message) {
		try {
			write(message);
		} catch(final Exception e) {}
	}
	
	public static final void destroy() {
		if(fos != null) {
			Assist.safeClose(fos);
			fos = null;
		}
	}
	

	public static final void lapse(final String message, final int interval, final int id) {
		if(id%interval == 0)
			out(message+id);
	}
}