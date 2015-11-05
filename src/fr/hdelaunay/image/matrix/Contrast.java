package fr.hdelaunay.image.matrix;

public class Contrast implements PredefinedMatrix {

	@Override
	public final String getName() {
		return "Contraste";
	}
	
	@Override
	public final short getSize() {
		return 3;
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