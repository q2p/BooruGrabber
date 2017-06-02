package q2p.boorugrabber.global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import q2p.boorugrabber.help.Assist;
import q2p.boorugrabber.help.Log;
import q2p.boorugrabber.network.Networker;
import q2p.boorugrabber.queue.Block;
import q2p.boorugrabber.queue.BlockManager;
import q2p.boorugrabber.storage.Config;
import q2p.boorugrabber.storage.Storage;
import q2p.boorugrabber.storage.Config.Field;

public abstract class BooruQueue {
	public final Networker networker = new Networker();
	
	public final String abbreviation;
	public final String name;
	protected final String pathMain;
	protected final String pathConfig;

	protected final Config config;

	public final BlockManager manager;

	protected BooruQueue(final String abbreviation, final String name, final Field ... fields) {
		this.abbreviation = abbreviation;
		this.name = name;
		
		pathMain = Assist.folder+'/'+abbreviation+'/';
		pathConfig = pathMain + "config.txt";

		config = new Config(fields);

		manager = new BlockManager(name);
	}

	protected final void setBlocks(final Block ... blocks) {
		manager.blocks = blocks;
	}

	public final boolean init() {
		Storage.initDirectory(pathMain);

		try {
			if(!new File(pathConfig).exists()) {
				final FileOutputStream fos = Storage.initWrite(pathConfig);
				try {
					config.write(fos);
					fos.close();
				} catch(final Exception e) {
					Assist.safeClose(fos);
					Assist.abort(pathConfig+"\nНе удалось записать в файл\n"+e.getMessage());
				}
				Log.out("Заполни файл конфигурации для "+abbreviation);
				return true;
			} else {
				final FileInputStream fis = Storage.initRead(pathConfig);
				try {
					final String error = config.load(fis);
					fis.close();
					if(error != null) {
						Log.out(error);
						return true;
					}
				} catch(final Exception e) {
					Assist.safeClose(fis);
					Assist.abort(pathConfig+"\nНе удалось прочитать файл"+e.getMessage());
				}
			}
		} catch(final Exception e) {
			Assist.abort(pathConfig+"\nНе удалось прочитать файл"+e.getMessage());
		}

		final Proxy proxy;
		if(config.getBool("proxy_enabled")) {
			final Proxy.Type proxyType;
			switch(config.getText("proxy_type")) {
				case "direct":
					proxyType = Proxy.Type.DIRECT;
					break;
				case "http":
					proxyType = Proxy.Type.HTTP;
					break;
				case "socks":
					proxyType = Proxy.Type.SOCKS;
					break;
				default:
					Log.proxyValue("proxy_type");
					return true;
			}
			final int port = config.getInt("proxy_port");
			if(port < 0 || port > 65535) {
				Log.proxyValue("proxy_port");
				return true;
			}

			try {
				proxy = new Proxy(proxyType, new InetSocketAddress(config.getText("proxy_address"), port));
			} catch(final Exception e) {
				Log.proxyValue("proxy_address");
				return true;
			}
		} else
			proxy = null;

		final int interval = config.getInt("connection_interval");
		if(interval < 0) {
			Log.proxyValue("connection_interval");
			return true;
		}
		
		final int timeout = config.getInt("connection_timeout");
		if(timeout < 0) {
			Log.proxyValue("connection_timeout");
			return true;
		}

		networker.set(interval, proxy, timeout);

		return initAdditional();
	}
	
	protected abstract boolean initAdditional();
	
	public abstract void cStart();
	public abstract void cStop();
}