package fr.hdelaunay.image.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Utils {
	
	/**
	 * Conversion d'un <i>String</i> en <i>Integer</i>.
	 * 
	 * @param value La chaîne <i>String</i>.
	 * 
	 * @return Le nombre <i>Integer</i>.
	 */
	
	public static final Integer toInt(final String value) {
		try {
			return Integer.parseInt(value);
		}
		catch(final Exception ex) {}
		return null;
	}
	
	/**
	 * Conversion d'un <i>String</i> en <i>Float</i>.
	 * 
	 * @param value La chaîne <i>String</i>.
	 * 
	 * @return Le nombre à virgule flottante <i>Float</i>.
	 */
	
	public static final Float toFloat(final String value) {
		try {
			return Float.parseFloat(value);
		}
		catch(final Exception ex) {}
		return null;
	}
	
	/**
	 * Joint des <i>float</i> par un caractère et retourne la chaîne ainsi créée.
	 * 
	 * @param joiner Le(s) caractère(s).
	 * @param floats Les <i>float</i>.
	 * 
	 * @return La chaîne de <i>float</i>.
	 */
	
	public static final String joinFloats(final String joiner, final float... floats) {
		final StringBuilder builder = new StringBuilder();
		for(final float floatt : floats) {
			builder.append(floatt + joiner);
		}
		builder.setLength(builder.length() - joiner.length());
		return builder.toString();
	}
	
	/**
	 * Provient de https://github.com/Skyost/Algogo/.
	 * 
	 * @return Le répertoire dans lequel le programme est éxecuté.
	 * 
	 * @throws UnsupportedEncodingException Si l'encodage n'est pas supporté.
	 */
	
	public static final File getParentFolder() throws UnsupportedEncodingException {
		return new File(URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), StandardCharsets.UTF_8.toString()));
	}

}