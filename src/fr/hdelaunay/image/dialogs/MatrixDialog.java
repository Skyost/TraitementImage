package fr.hdelaunay.image.dialogs;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import fr.hdelaunay.image.Main;
import fr.hdelaunay.image.frames.MainFrame;
import fr.hdelaunay.image.utils.Utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MatrixDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	public MatrixDialog(final MainFrame parent, final int size) {
		this.setTitle("Appliquer une matrice");
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		this.setResizable(false);
		this.setJMenuBar(this.createMenu());
		final Container content = this.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(Box.createRigidArea(new Dimension(0, 5)));
		for(int i = 0; i != size; i++) {
			final JPanel line = new JPanel();
			for(int j = 0; j != size; j++) {
				final JPanel panel = new JPanel();
				final JSpinner spinner = new JSpinner();
				spinner.setValue(0);
				panel.add(spinner);
				line.add(panel);
			}
			content.add(line);
		}
		final JPanel buttons = new JPanel();
		final JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				final float[] matrix = new float[size * size];
				int index = 0;
				for(final Component component : content.getComponents()) {
					if(!(component instanceof JPanel)) {
						continue;
					}
					for(final Component spinner : ((JPanel)component).getComponents()) {
						if(!(spinner instanceof JSpinner)) {
							continue;
						}
						final Integer value = Utils.toInt(((JSpinner)spinner).getValue().toString());
						if(value == null) {
							JOptionPane.showMessageDialog(MatrixDialog.this, "\"" + value + "\" n'est pas une valeur valide !", "Erreur !", JOptionPane.ERROR_MESSAGE);
						}
						matrix[index++] = (float)value;
					}
				}
				parent.applyMatrix(matrix, size);
			}
		});
		final JButton fermer = new JButton("Fermer");
		fermer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(final ActionEvent event) {
				MatrixDialog.this.dispose();
			}
			
		});
		buttons.add(Box.createRigidArea(new Dimension(0, 5)));
		buttons.add(ok);
		buttons.add(Box.createRigidArea(new Dimension(0, 5)));
		buttons.add(fermer);
		buttons.add(Box.createRigidArea(new Dimension(0, 5)));
		content.add(buttons);
		this.pack();
	}
	
	private JMenuBar createMenu() {
		final JMenuBar menu = new JMenuBar();
		final JMenu fichier = new JMenu("Fichier");
		final JMenuItem ouvrir = new JMenuItem("Ouvrir...");
		ouvrir.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_open.png")));
		fichier.add(ouvrir);
		final JMenuItem enregistrerSous = new JMenuItem("Enregistrer sous...");
		enregistrerSous.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_saveas.png")));
		fichier.add(enregistrerSous);
		menu.add(fichier);
		return menu;
	}
	
}