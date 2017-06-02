package q2p.boorugrabber.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Log;

public final class Networker {
	private int interval;
	private int connectionTimeout;
	private long passAfter = -1;
	private Proxy proxy;

	static {
		HttpURLConnection.setFollowRedirects(false);
	}

	public final void set(final int interval, final Proxy proxy, final int connectionTimeout) {
		this.interval = interval;
		this.connectionTimeout = connectionTimeout;
		this.proxy = proxy;
	}

	public final Response receive(final String method, final String url, final String[] headers) {
		for(byte i = 4; i != 0; i--) {
			final Response ret = receiveAttempt(method, url, headers);
			if(ret != null)
				return ret;
		}
		
		Log.out("Не удалось установить надёжное соединение с сервером:\n"+url);
		
		return null;
	}
	
	private final Object INTERVAL_LOCK = new Object();
	private final Response receiveAttempt(final String method, final String url, final String[] headers) {
		synchronized(INTERVAL_LOCK) {
			if(interval != 0) if(System.currentTimeMillis() < passAfter) {
				final long toWait = passAfter - System.currentTimeMillis();
				if(toWait > 0) {
					passAfter += interval;
					try {
						Thread.sleep(toWait);
					} catch(final InterruptedException e) {}
				}
			} else
				passAfter = System.currentTimeMillis() + interval;
		}
		
		final URLConnection uc;
		try {
			if(proxy == null)
				uc = new URL(url).openConnection();
			else
				uc = new URL(url).openConnection(proxy);
		} catch(final Exception e) {
			return null;
		}

		final HttpURLConnection connection;
		if(url.startsWith("https://"))
			connection = (HttpsURLConnection) uc;
		else
			connection = (HttpURLConnection) uc;

		try {
			connection.setConnectTimeout(connectionTimeout);
			connection.setReadTimeout(connectionTimeout);

			connection.setRequestMethod(method);
			
			if(headers != null) for(int i = 0; i != headers.length; i += 2)
				connection.setRequestProperty(headers[i], headers[i+1]);
			
			final long limit = System.currentTimeMillis() + connectionTimeout;

			connection.connect();

			if(Assist.limit(limit))
				return null;

			final InputStream in;

			final short responseCode = (short)connection.getResponseCode();

			if(Assist.limit(limit))
				return null;

			final long contentLength = connection.getContentLengthLong();

			if(Assist.limit(limit))
				return null;

			final String redirected = connection.getHeaderField("Location");

			if(Assist.limit(limit))
				return null;

			if(responseCode >= 400)
				in = connection.getErrorStream();
			else
				in = connection.getInputStream();

			if(Assist.limit(limit))
				return null;

			final ByteArrayOutputStream out = new ByteArrayOutputStream();

			int bytesRead;
			final byte[] buffer = new byte[4096];
			while ((bytesRead = in.read(buffer)) != -1) {
				if(Assist.limit(limit))
					return null;

				out.write(buffer, 0, bytesRead);
			}

			return new Response(responseCode, contentLength, redirected, out);
		} catch(final Exception e) {
			return null;
		} finally {
			try {
				connection.disconnect();
			} catch(final Exception e) {}
		}
	}
}