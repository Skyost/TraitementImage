package fr.hdelaunay.image;

import javax.swing.UIManager;

import fr.hdelaunay.image.frames.MainFrame;

public class Main {
	
	public static final String RES_PACKAGE = "/fr/hdelaunay/image/res/";

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new MainFrame().setVisible(true);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}

}