package fr.hdelaunay.image.filters;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class Sobel extends Filter {
	
	private static final Kernel FIRST = new Kernel(3, 3, new float[]{
																		1f, 0f, -1f,
																		2f, 0f, -2f,
																		1f, 0f, -1f
																	});
	private static final Kernel SECOND = new Kernel(3, 3, new float[] {
																		1f, 2f, 1f,
																		0f, 0f, 0f,
																		-1f, -2f, -1f
																	});

	@Override
	public final BufferedImage filter(final BufferedImage image) {
		final BufferedImage result = new ConvolveOp(FIRST).filter(image, null);
		return new ConvolveOp(SECOND).filter(result, null);
	}

}