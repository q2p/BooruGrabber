package q2p.boorugrabber.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import q2p.boorugrabber.help.Assist;

public final class Storage {
	public static final void initDirectory(final String path) {
		initDirectory(new File(path));
	}
	public static final void initDirectory(final File file) {
		try {
			if(file.exists()) {
				if(!file.isDirectory())
					Assist.abort(file, "Файл не является директорией");
			} else
				file.mkdirs();
		} catch(final Exception e) {
			Assist.abort(file, "Не удалось инициализировать директорию", e);
		}
	}
	public static final FileOutputStream initWrite(final String path) {
		try {
			return new FileOutputStream(path);
		} catch(final Exception e) {
			Assist.abort("Не удалось записать в файл\n"+path+'\n'+e.getMessage());
			return null;
		}
	}
	public static final FileOutputStream initWrite(final File file) {
		try {
			final File parent = file.getParentFile();
			if(!parent.exists())
				parent.mkdirs();
			return new FileOutputStream(file);
		} catch(final Exception e) {
			Assist.abort("Не удалось записать в файл\n"+file.getAbsolutePath()+'\n'+e.getMessage());
			return null;
		}
	}
	public static final FileInputStream initRead(final String path) {
		try {
			return new FileInputStream(path);
		} catch(final Exception e) {
			Assist.abort("Не удалось прочитать файл\n"+path+'\n'+e.getMessage());
			return null;
		}
	}
	public static final FileInputStream initRead(final File file) {
		try {
			return new FileInputStream(file);
		} catch(final Exception e) {
			Assist.abort("Не удалось прочитать файл\n"+file.getAbsolutePath()+'\n'+e.getMessage());
			return null;
		}
	}

	public static final void copy(final File from, final File to) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);

			final byte[] buffer = new byte[1024*1024];

			int length;
			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
				out.flush();
			}

			in.close();
			out.close();
		} catch(final Exception e) {
			Assist.safeClose(in);
			Assist.safeClose(out);
			Assist.abort("Не удалось скопировать файл \""+from+"\" в \""+to+"\"\n"+e.getMessage());
		}
	}
	public static final void copy(final String from, final String to) {
		copy(new File(from), new File(to));
	}

	public static final void backup(final String path) {
		final int idx = path.lastIndexOf('/')+1;
		if(idx == 0)
			Assist.abort("Не удалось создать копию файла \""+path+"\"");

		backup(path.substring(0, idx), path.substring(idx));
	}
	public static final void backup(final String directory, final String name) {
		copy(directory+name, pickNewCountableName(directory, "backup_"+name));
	}

	public static final String pickNewCountableName(final String folder, String prefix) {
		initDirectory(folder);

		prefix += '-';

		int max = 0;
		for(final String name : new File(folder).list())
			if(name.startsWith(prefix) && name.length() >= prefix.length()) try {
				max = Math.max(Integer.parseInt(name.substring(prefix.length(), name.length())), max);
			} catch(final NumberFormatException e) {
				continue;
			}

		return folder + '/' + prefix + (max+1);
	}

	public static final String readString(final DataInputStream dis) throws Exception {
		final byte[] buff = new byte[dis.readInt()];
		dis.read(buff);
		return new String(buff, StandardCharsets.UTF_8);
	}
	public static final void readStrings(final DataInputStream dis, final List<String> container) throws Exception {
		for(int i = dis.readInt(); i != 0; i--)
			container.add(readString(dis));
	}
	public static final void readInts(final DataInputStream dis, final List<Integer> container) throws Exception {
		for(int i = dis.readInt(); i != 0; i--)
			container.add(dis.readInt());
	}
	public static final int[] readInts(final DataInputStream dis) throws Exception {
		final int[] integers = new int[dis.readInt()];
		for(int i = 0; i != integers.length; i++)
			integers[i] = dis.readInt();

		return integers;
	}
	public static final byte[] readBytes(final DataInputStream dis) throws Exception {
		final byte[] buff = new byte[dis.readInt()];
		dis.read(buff);
		return buff;
	}
	public static final void writeString(final DataOutputStream dos, final String string) throws IOException {
		final byte[] buff = string.getBytes(StandardCharsets.UTF_8);
		dos.writeInt(buff.length);
		dos.write(buff);
	}
	public static final void writeStrings(final DataOutputStream dos, final List<String> strings) throws IOException {
		dos.writeInt(strings.size());
		for(final String s : strings)
			writeString(dos, s);
	}
	public static final void writeInts(final DataOutputStream dos, final List<Integer> integers) throws IOException {
		dos.writeInt(integers.size());
		for(final Integer i : integers)
			dos.writeInt(i);
	}
	public static final void writeInts(final DataOutputStream dos, final int[] integers) throws IOException {
		dos.writeInt(integers.length);
		for(final int i : integers)
			dos.writeInt(i);
	}
	public static final void write(final DataOutputStream dos, final LinkedList<? extends Writable> writable) throws IOException {
		dos.writeInt(writable.size());
		for(final Writable w : writable)
			w.write(dos);
	}
	public static final void writeBytes(final DataOutputStream dos, final byte[] bytes) throws IOException {
		dos.writeInt(bytes.length);
		dos.write(bytes);
	}
}