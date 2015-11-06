package fr.hdelaunay.image.matrix;

public class Embossed extends PredefinedMatrix {

	@Override
	public final String getName() {
		return "Relief";
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