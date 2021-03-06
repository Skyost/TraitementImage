package fr.hdelaunay.image;

import java.io.File;

import javax.swing.UIManager;

import fr.hdelaunay.image.frames.MainFrame;
import fr.hdelaunay.image.utils.Utils;

public class Main {
	
	public static final String APP_NAME = "Traitement d'images";
	public static final String APP_VERSION = "0.1";
	
	public static final String RES_PACKAGE = "/fr/hdelaunay/image/res/";
	
	public static AppSettings settings;

	/**
	 * Première méthode éxecutée par le programme.
	 * 
	 * @param args Liste des arguments possibles :
	 * <br><table><tr><td><b>Index</b></td><td><b>Explications</b></td></tr>
	 * <tr><td>1</td><td>Le fichier à ouvrir.</td></tr>
	 * </table>
	 */
	
	public static void main(final String[] args) {
		try {
			settings = new AppSettings(new File(Utils.getParentFolder(), "settings.json"));
			settings.load();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			final MainFrame main = new MainFrame();
			main.setVisible(true);
			if(args.length > 0) {
				final File file = new File(args[0]);
				if(file.exists()) {
					main.open(file);
				}
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}

}