package fr.hdelaunay.image.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Utils {
	
	/**
	 * Conversion d'un <i>String</i> en <i>Integer</i>.
	 * 
	 * @param value La cha�ne <i>String</i>.
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
	 * @param value La cha�ne <i>String</i>.
	 * 
	 * @return Le nombre � virgule flottante <i>Float</i>.
	 */
	
	public static final Float toFloat(final String value) {
		try {
			return Float.parseFloat(value);
		}
		catch(final Exception ex) {}
		return null;
	}
	
	/**
	 * Joint des <i>float</i> par un caract�re et retourne la cha�ne ainsi cr��e.
	 * 
	 * @param joiner Le(s) caract�re(s).
	 * @param floats Les <i>float</i>.
	 * 
	 * @return La cha�ne de <i>float</i>.
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
	 * @return Le r�pertoire dans lequel le programme est �xecut�.
	 * 
	 * @throws UnsupportedEncodingException Si l'encodage n'est pas support�.
	 */
	
	public static final File getParentFolder() throws UnsupportedEncodingException {
		return new File(URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), StandardCharsets.UTF_8.toString()));
	}

}