package q2p.boorugrabber.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import q2p.boorugrabber.help.Parser;

public final class Config {
	private final Field[] fields;
	
	public final String getText(final String field) {
		for(final Field f : fields)
			if(f.name.equals(field))
				return f.text;

		return null;
	}
	public final int getInt(final String field) {
		for(final Field f : fields)
			if(f.name.equals(field))
				return f.digit;

		return 0;
	}
	public final boolean getBool(final String field) {
		for(final Field f : fields)
			if(f.name.equals(field))
				return f.digit == 1;

		return false;
	}

	public Config(final Field[] fields) {
		this.fields = new Field[fields.length+7];
		this.fields[0] = new Field("proxy_enabled", Primitive.BOOL);
		this.fields[1] = new Field("proxy_type", Primitive.TEXT);
		this.fields[2] = new Field("proxy_address", Primitive.TEXT);
		this.fields[3] = new Field("proxy_port", Primitive.INT);
		this.fields[4] = new Field("threads", Primitive.INT);
		this.fields[5] = new Field("connection_interval", Primitive.INT);
		this.fields[6] = new Field("connection_timeout", Primitive.INT);

		for(int i = fields.length-1; i != -1; i--)
			this.fields[i+7] = fields[i];
	}

	public final String load(final FileInputStream fis) throws Exception {
		byte[] buff = new byte[fis.available()];
		fis.read(buff);
		final LinkedList<String> lines = Parser.split(new String(buff, StandardCharsets.UTF_8), '\n');
		buff = null;
		while(!lines.isEmpty()) {
			final String line = lines.removeFirst();

			if(line.trim().length()==0)
				continue;

			final int idx = line.indexOf('=');
			if(idx == -1)
				return "Поле "+line+" без значения";

			final String name = line.substring(0, idx);

			final String value = line.substring(idx+1);

			Field field = null;

			for(final Field f : fields)
				if(f.name.equals(name)) {
					field = f;
					break;
				}

			if(field == null)
				continue;

			if(field.text != null)
				return "Поле "+name+" использованно дважды";

			field.text = value;

			if(field.primitive == Primitive.INT)
				try {
					field.digit = Integer.parseInt(value);
				} catch(final Exception e) {
					return "Поле "+name+" должно содержать целочисленное значение";
				}
			else if(field.primitive == Primitive.BOOL) if(value.equals("true"))
				field.digit = 1;
			else if(value.equals("false")) field.digit = 0;
			else
				return "Поле "+name+" должно содержать булево значение";
		}

		final StringBuilder err = new StringBuilder("В файле конфигурации не объявлены следующие поля:\n");
		boolean found = false;
		for(final Field field : fields)
			if(field.text == null) {
				err.append(field.name);
				err.append('\n');
				found = true;
			}
		if(found)
			return err.toString();

		return null;
	}

	public final void write(final FileOutputStream fos) throws IOException {
		final StringBuilder out = new StringBuilder();

		boolean needNewLine = false;
		for(final Field field : fields) {
			if(needNewLine)
				out.append('\n');
			else
				needNewLine = true;

			out.append(field.name);
			out.append('=');
		}

		fos.write(out.toString().getBytes(StandardCharsets.UTF_8));
		fos.flush();
	}

	public static final class Field {
		private final String name;
		private final Primitive primitive;
		private String text = null;
		private int digit = 0;

		public Field(final String name, final Primitive primitive) {
			this.name = name;
			this.primitive = primitive;
		}
	}

	public enum Primitive {
		BOOL, INT, TEXT;
	}
}