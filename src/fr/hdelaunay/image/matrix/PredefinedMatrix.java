package fr.hdelaunay.image.matrix;

/**
 * Une matrice pr�d�finie.
 * 
 * @author Hugo Delaunay.
 */

public abstract class PredefinedMatrix {
	
	/**
	 * Les matrices pr�d�finies disponibles.
	 */
	
	private static final PredefinedMatrix[] PREDEFINED_MATRIX = new PredefinedMatrix[]{new Blurred(), new Borders(), new Contrast(), new Embossed()};
	
	/**
	 * Retourne le nom de cette matrice.
	 * 
	 * @return Le nom de cette matrice.
	 */
	
	public abstract String getName();
	
	/**
	 * Retourne la taille de cette matrice.
	 * 
	 * @return La taille de cette matrice.
	 */
	
	public final int getSize() {
		return (int)Math.sqrt(getMatrix().length);
	}
	
	/**
	 * Retourne la matrice sous forme de tableau � une entr�e.
	 * 
	 * @return La matrice sous forme de tableau � une entr�e.
	 */
	
	public abstract float[] getMatrix();
	
	/**
	 * Retourne les matrices pr�d�finies disponibles.
	 * 
	 * @return Les matrices pr�d�finies disponibles.
	 */
	
	public static final PredefinedMatrix[] getPredefinedMatrix() {
		return PREDEFINED_MATRIX;
	}

}