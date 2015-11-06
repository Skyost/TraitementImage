package fr.hdelaunay.image.matrix;

public class Contrast extends PredefinedMatrix {

	@Override
	public final String getName() {
		return "Contraste";
	}
	
	@Override
	public final float[] getMatrix() {
		return new float[]{
				0f, -1f, 0f,
				-1f, 5f, -1f,
				0f, -1f, 0f
		};
	}

}