package q2p.boorugrabber.help;

import java.io.Closeable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;

public final class Assist {
	public static final String folder = "E:/@MyFolder/p/other/BooruStore/"; // TODO: автоматическое определение
	
	public static final boolean same(final Object one, final Object two) {
		if(one == null)
			return two == null;

		return one.equals(two);
	}

	public static final void abort(final File file, final String comment, final Exception exception) {
		abort(file, comment+"\n"+exception.getMessage());
	}
	public static final void abort(final File file, final String comment) {
		abort(file.getAbsolutePath() + "\n" + comment);
	}
	public static final void abort(final String reason) {
		Log.safe(reason);
		Log.destroy();
		System.exit(1);
	}

	public static final void safeClose(final Closeable closeable) {
		try {
			closeable.close();
		} catch(final Exception e) {}
	}
	
	public static synchronized final Long parseTime(final String time, final SimpleDateFormat format) {
		try {
			return format.parse(time).getTime();
		} catch (final Exception e) {
			return null;
		}
	}

	private static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
	public static final long currentGMTmilisec() {
		return Calendar.getInstance(gmtTimeZone).getTimeInMillis();
	}

	public static final boolean limit(final long timeLimit) {
		return System.currentTimeMillis() > timeLimit;
	}
	
	public static final <T> void drain(final LinkedList<T> to, final LinkedList<T> from) {
		while(!from.isEmpty())
			to.addLast(from.removeFirst());
	}
}