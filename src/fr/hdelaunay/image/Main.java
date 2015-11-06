package fr.hdelaunay.image;

import java.io.File;

import javax.swing.UIManager;

import fr.hdelaunay.image.frames.MainFrame;
import fr.hdelaunay.image.utils.Utils;

public class Main {
	
	public static final String RES_PACKAGE = "/fr/hdelaunay/image/res/";
	
	public static AppSettings settings;

	public static void main(String[] args) {
		try {
			settings = new AppSettings(new File(Utils.getParentFolder(), "settings.json"));
			settings.load();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new MainFrame().setVisible(true);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}

}