package fr.hdelaunay.image.matrix;

public class Blurred extends PredefinedMatrix {

	@Override
	public final String getName() {
		return "Flou";
	}
	
	@Override
	public final float[] getMatrix() {
		return new float[]{
						0f, 0f, 0f, 0f, 0f,
						0f, 1f, 1f, 1f, 0f,
						0f, 1f, 1f, 1f, 0f,
						0f, 1f, 1f, 1f, 0f,
						0f, 0f, 0f, 0f, 0f
		};
	}

}