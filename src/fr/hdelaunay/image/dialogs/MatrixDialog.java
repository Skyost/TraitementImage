package fr.hdelaunay.image.dialogs;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.hdelaunay.image.Main;
import fr.hdelaunay.image.frames.MainFrame;
import fr.hdelaunay.image.matrix.PredefinedMatrix;
import fr.hdelaunay.image.utils.Utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * La boîte de dialogue permettant de créer des matrices.
 * 
 * @author Hugo Delaunay.
 */

public class MatrixDialog extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/**
	 * La taille de cette matrice (on assume que cette taille est la même pour la hauteur comme pour la largeur).
	 */
	
	private final int size;
	
	/**
	 * Création d'une nouvelle instance de <i>MatrixDialog</i> (la boîte de dialogue permettant de créer des matrices, ici vierge).
	 * 
	 * @param parent Le parent (pour appliquer cette matrice).
	 * @param size La taille de cette matrice (on assume que cette taille est la même pour la hauteur comme pour la largeur).
	 */
	
	public MatrixDialog(final MainFrame parent, final int size) {
		this(parent, size, null);
	}
	
	/**
	 * Création d'une nouvelle instance de <i>MatrixDialog</i> (la boîte de dialogue permettant de créer des matrices).
	 * 
	 * @param parent Le parent (pour appliquer cette matrice).
	 * @param size La taille de cette matrice (on assume que cette taille est la même pour la hauteur comme pour la largeur).
	 * @param matrix La matrice qui sera chargée par la boîte de dialogue.
	 */
	
	public MatrixDialog(final MainFrame parent, final int size, final float[] matrix) {
		this.size = size;
		if(size != 3 && size != 5) {
			this.dispose();
		}
		this.setTitle("Appliquer une matrice");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource(Main.RES_PACKAGE + "icon_matrix.png")));
		this.setLocationRelativeTo(parent);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setType(Type.POPUP);
		this.setJMenuBar(this.createMenu(parent));
		final Container content = this.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(Box.createRigidArea(new Dimension(0, 5)));
		int index = 0;
		for(int i = 0; i != size; i++) {
			final JPanel line = new JPanel();
			for(int j = 0; j != size; j++) {
				final JTextField value = new JTextField();
				value.setText(String.valueOf(matrix == null ? 0f : matrix[index++]));
				value.setColumns(5);
				line.add(value);
			}
			content.add(line);
		}
		final JPanel buttons = new JPanel();
		final JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				parent.applyMatrix(getMatrix(), size);
			}
			
		});
		final JButton fermer = new JButton("Fermer");
		fermer.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
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
	
	/**
	 * Création du menu pour le GUI.
	 * 
	 * @param parent Le parent (pour appliquer cette matrice).
	 * 
	 * @return Le menu.
	 */
	
	private JMenuBar createMenu(final MainFrame parent) {
		final JMenuBar menu = new JMenuBar();
		final JMenu fichier = new JMenu("Fichier");
		final JMenuItem ouvrir = new JMenuItem("Ouvrir...");
		ouvrir.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				final JFileChooser chooser = Utils.showDialog(MatrixDialog.this, true, new FileNameExtensionFilter("Fichier de matrice (*.mtx)", "mtx"));
				if(chooser != null) {
					try {
						final List<String> lines = Files.readAllLines(Paths.get(chooser.getSelectedFile().getPath()), StandardCharsets.UTF_8);
						final int sqrt = (int)Math.sqrt(lines.size());
						if(sqrt != size) {
							final float[] matrix = new float[lines.size()];
							for(int i = 0; i != lines.size(); i++) {
								matrix[i] = Float.parseFloat(lines.get(i));
							}
							new MatrixDialog(parent, sqrt, matrix).setVisible(true);
							MatrixDialog.this.dispose();
						}
					}
					catch(Exception ex) {
						JOptionPane.showMessageDialog(MatrixDialog.this, "Impossible de lire ce fichier !", "Erreur !", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
				}
			}
			
		});
		ouvrir.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_open.png")));
		fichier.add(ouvrir);
		final JMenuItem enregistrerSous = new JMenuItem("Enregistrer sous...");
		enregistrerSous.addActionListener(new ActionListener() {
			
			@Override
			public final void actionPerformed(final ActionEvent event) {
				final JFileChooser chooser = Utils.showDialog(MatrixDialog.this, false, new FileNameExtensionFilter("Fichier de matrice (*.mtx)", "mtx"));
				if(chooser != null) {
					Utils.saveContent(MatrixDialog.this, chooser, Utils.joinFloats(System.lineSeparator(), getMatrix()));
				}
			}
		
		});
		enregistrerSous.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_saveas.png")));
		fichier.add(enregistrerSous);
		fichier.addSeparator();
		final JMenu matricesPredefinies = new JMenu("Matrices prédéfinies");
		for(final PredefinedMatrix matrix : PredefinedMatrix.getPredefinedMatrix()) {
			final JMenuItem menuMatrix = new JMenuItem(matrix.getName());
			menuMatrix.addActionListener(new ActionListener() {

				@Override
				public final void actionPerformed(final ActionEvent event) {
					new MatrixDialog(parent, matrix.getSize(), matrix.getMatrix()).setVisible(true);
					MatrixDialog.this.dispose();
				}
				
			});
			matricesPredefinies.add(menuMatrix);
		}
		fichier.add(matricesPredefinies);
		menu.add(fichier);
		return menu;
	}
	
	/**
	 * Retourne la matrice sous forme de tableau à une dimension.
	 * 
	 * @return La matrice sous forme de tableau à une dimension. 
	 */
	
	public final float[] getMatrix() {
		final float[] matrix = new float[size * size];
		int index = 0;
		for(final Component component : this.getContentPane().getComponents()) {
			if(!(component instanceof JPanel)) {
				continue;
			}
			for(final Component value : ((JPanel)component).getComponents()) {
				if(!(value instanceof JTextField)) {
					continue;
				}
				final Float valueFloat = Utils.toFloat(((JTextField)value).getText());
				if(valueFloat == null) {
					JOptionPane.showMessageDialog(MatrixDialog.this, "\"" + valueFloat + "\" n'est pas une valeur valide !", "Erreur !", JOptionPane.ERROR_MESSAGE);
				}
				matrix[index++] = valueFloat;
			}
		}
		return matrix;
	}
	
}