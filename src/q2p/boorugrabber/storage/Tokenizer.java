package q2p.boorugrabber.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import q2p.boorugrabber.help.Assist;

public final class Tokenizer {
	public static final void flush(final String path, final int id, final Temporary temporary) {
		final File directory = new File(path);
		final File file = new File(path+id);

		if(directory.exists()) {
			if(!directory.isDirectory())
				Assist.abort(directory, "Файл не является директорией");
		} else
			try {
				if(!directory.mkdirs())
					Assist.abort(directory, "Не удалось инициализировать директорию");
			} catch(final Exception e) {
				Assist.abort(directory, "Не удалось инициализировать директорию", e);
			}

		if(file.exists() && !file.isFile())
			Assist.abort(file, "Файл является директорией");

		final DataOutputStream dos;
		try {
			dos = new DataOutputStream(new FileOutputStream(file));
		} catch(final Exception e) {
			Assist.abort(file, "Не удалось изменить файл", e);
			return;
		}

		try {
			temporary.write(dos);
			dos.flush();
			dos.close();
		} catch(final Exception e) {
			Assist.safeClose(dos);
			Assist.abort(file, "Не удалось изменить файл", e);
		}
	}
	
	public static final void safeRead(final String directory, final String name, final Token token) {
		final File folder = new File(directory);
		final File dat = new File(directory+name);
		final File twr = new File(directory+name+".twr");

		if(folder.exists()) {
			if(!folder.isDirectory())
				Assist.abort(folder, "Файл не является директорией");
		} else {
			token.generate();
			return;
		}
		
		if(twr.exists()) if(twr.isFile())
			Assist.abort(twr, "Наличие файла свидетельствует о не удачном завершении записи, исправьте ошибку в ручную");
		else
			Assist.abort(twr, "Файл является директорией");

		if(!dat.exists()) {
			token.generate();
			return;
		}
		if(!dat.isFile())
			Assist.abort(dat, "Файл является директорией");

		final DataInputStream dis;
		try {
			dis = new DataInputStream(new FileInputStream(dat));
		} catch(final Exception e) {
			Assist.abort(dat, "Не удалось прочитать файл", e);
			return;
		}

		try {
			token.read(dis);
			dis.close();
		} catch(final Exception e) {
			Assist.safeClose(dis);
			Assist.abort(dat, "Не удалось прочитать файл", e);
		}
	}

	public static final void safeWrite(final String directory, final String name, final Token token) {
		final File folder = new File(directory);
		final File dat = new File(directory+name);
		final File twr = new File(directory+name+".twr");

		if(folder.exists()) {
			if(!folder.isDirectory())
				Assist.abort(folder, "Файл не является директорией");
		} else
			folder.mkdirs();
		
		if(twr.exists()) if(twr.isFile())
			Assist.abort(twr, "Наличие файла свидетельствует о не удачном завершении записи, исправьте ошибку в ручную");
		else
			Assist.abort(twr, "Файл является директорией");

		if(dat.exists()) {
			if(!dat.isFile())
				Assist.abort(dat, "Файл является директорией");

			Storage.copy(dat, twr);
		}

		final DataOutputStream dos;
		try {
			dos = new DataOutputStream(new FileOutputStream(dat));
		} catch(final Exception e) {
			Assist.abort(dat, "Не удалось изменить файл", e);
			return;
		}

		try {
			token.write(dos);
			dos.flush();
			dos.close();
		} catch(final Exception e) {
			Assist.safeClose(dos);
			Assist.abort(dat, "Не удалось изменить файл", e);
		}
		
		try {
			twr.delete();
		} catch(final Exception e) {
			Assist.abort(twr, "Не удалось удалить файл", e);
		}
	}
}