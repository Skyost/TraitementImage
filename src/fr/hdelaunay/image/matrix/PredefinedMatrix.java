package fr.hdelaunay.image.matrix;

public abstract class PredefinedMatrix {
	
	public abstract String getName();
	
	public final int getSize() {
		return (int)Math.sqrt(getMatrix().length);
	}
	
	public abstract float[] getMatrix();

}