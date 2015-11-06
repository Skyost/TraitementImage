package fr.hdelaunay.image.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Utils {
	
	public static final Integer toInt(final String value) {
		try {
			return Integer.parseInt(value);
		}
		catch(final Exception ex) {}
		return null;
	}
	
	public static final String joinFloats(final String joiner, final float[] floats) {
		final StringBuilder builder = new StringBuilder();
		for(final Object floatt : floats) {
			builder.append(floatt.toString() + joiner);
		}
		builder.setLength(builder.length() - joiner.length());
		return builder.toString();
	}
	
	public static final File getParentFolder() throws UnsupportedEncodingException {
		return new File(URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), StandardCharsets.UTF_8.toString()));
	}

}