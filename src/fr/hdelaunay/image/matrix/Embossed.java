package fr.hdelaunay.image.matrix;

public class Embossed implements PredefinedMatrix {

	@Override
	public final String getName() {
		return "Relief";
	}
	
	@Override
	public final short getSize() {
		return 3;
	}

	@Override
	public final float[] getMatrix() {
		return new float[]{
				-2f, -1f, 0f,
				-1f, 1f, 1f,
				0f, 1f, 2f
		};
	}

}