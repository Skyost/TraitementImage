package fr.hdelaunay.image.matrix;

public abstract class PredefinedMatrix {
	
	private static final PredefinedMatrix[] PREDEFINED_MATRIX = new PredefinedMatrix[]{new Blurred(), new Borders(), new Contrast(), new Embossed()};
	
	public abstract String getName();
	
	public final int getSize() {
		return (int)Math.sqrt(getMatrix().length);
	}
	
	public abstract float[] getMatrix();
	
	public static final PredefinedMatrix[] getPredefinedMatrix() {
		return PREDEFINED_MATRIX;
	}

}