package fr.hdelaunay.image.filters;

import java.awt.image.BufferedImage;

public abstract class Filter {
	
	public abstract BufferedImage filter(final BufferedImage image);
	
}