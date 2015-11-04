package fr.hdelaunay.image.utils;

public class Utils {
	
	public static final Integer toInt(final String value) {
		try {
			return Integer.parseInt(value);
		}
		catch(final Exception ex) {}
		return null;
	}

}