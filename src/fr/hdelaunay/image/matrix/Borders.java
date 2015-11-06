package fr.hdelaunay.image.matrix;

public class Borders extends PredefinedMatrix {

	@Override
	public final String getName() {
		return "Bordures";
	}
	
	@Override
	public final float[] getMatrix() {
		return new float[]{
				0f, 1f, 0f,
				1f, -4f, 1f,
				0f, 1f, 0f
		};
	}

}