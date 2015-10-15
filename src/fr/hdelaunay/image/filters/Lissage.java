package fr.hdelaunay.image.filters;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class Lissage extends Filter {
	
	private static final Kernel KERNEL = new Kernel(5, 5, new float[]{
											4/1344f, 18/1344f, 19/1344f, 18/1344f, 4/1344f,
											18/1344f, 80/1344f, 132/1344f, 80/1344f, 18/1344f,
											29/1344f, 132/1344f, 218/1344f, 132/1344f, 29/1344f,
											18/1344f, 80/1344f, 132/1344f, 80/1344f, 18/1344f,
											4/1344f, 18/1344f, 29/1344f, 18/1344f, 4/1344f
										});

	@Override
	public final BufferedImage filter(final BufferedImage image) {
		return new ConvolveOp(KERNEL).filter(image, null);
	}

}