package fr.hdelaunay.image.utils;

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

}