package fr.hdelaunay.image.frames;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Rect;

import fr.hdelaunay.image.Main;
import fr.hdelaunay.image.dialogs.MatrixDialog;
import fr.hdelaunay.image.dialogs.WaitingDialog;
import fr.hdelaunay.image.utils.JLabelPreview;
import fr.hdelaunay.image.utils.OpenCVUtils;
import fr.hdelaunay.image.utils.Utils;

import javax.swing.JCheckBox;

/**
 * La fenêtre principale.
 * 
 * @author Hugo Delaunay.
 */

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Non nul si l'antialiasing est activé et que le zoom est supérieur à zéro.
	 * <br>Si non nul, contient l'image sans filtre d'antialiasing.
	 */
	
	private BufferedImage antialiasing;
	
	/**
	 * Le zoom actuel (0 <= zoom <= 100).
	 */
	
	private short zoom = 0;
	
	/*
	 * Différents composants du GUI.
	 */
	
	private final JMenu fichiersRecents = new JMenu("Fichiers récents");
	private final JLabelPreview lblPreview = new JLabelPreview();
	private final JButton btnMatrice = new JButton("Appliquer matrice...");
	private final JButton btnReconnaissanceFaciale = new JButton("Reconnaissance faciale...");
	private final JLabel lblZoom = new JLabel("Zoom (" + zoom + "%) :");
	private final JButton btnPlus = new JButton("Plus");
	private final JButton btnMoins = new JButton("Moins");
	private final JCheckBox chckbxAntialiasing = new JCheckBox("Anti-crénelage");
	private final JButton btnAnnuler = new JButton("Annuler");

	/**
	 * Le code éxecuté lorsque l'on cliqué sur le bouton Annuler ou sur le menu Édition → Annuler.
	 */
	
	private final ActionListener undo = new ActionListener() {

		@Override
		public final void actionPerformed(final ActionEvent event) {
			if(lblPreview.stackSize() <= 1) {
				return;
			}
			zoom(0);
			lblPreview.popFromStack();
			lblPreview.setIcon(lblPreview.peekFromStack(), false);
			if(lblPreview.stackSize() == 1) {
				btnAnnuler.setEnabled(false);
			}
		}

	};
	
	/**
	 * Création d'une nouvelle instance de <i>MainFrame</i> (la fenêtre principale).
	 */

	public MainFrame() {
		this.setTitle("Traitement image");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource(Main.RES_PACKAGE + "icon_app.png")));
		this.setSize(680, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(this.createMenu());
		final JScrollPane scrollBar = new JScrollPane(lblPreview);
		lblPreview.addMouseListener(new MouseListener() {

			@Override
			public final void mouseClicked(final MouseEvent event) {
				if(event.isPopupTrigger()) {
					return;
				}
				if(event.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				final Rectangle[] rectangles = lblPreview.getRectanglesAt(event.getPoint());
				if(rectangles.length == 0) {
					return;
				}
				if(rectangles.length > 1) {
					JOptionPane.showMessageDialog(MainFrame.this, "Ne pas cliquer sur deux visages en même temps !", "Erreur !", JOptionPane.ERROR_MESSAGE);
					return;
				}
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("Fichier PNG (*.png)", "png"));
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
				chooser.setMultiSelectionEnabled(false);
				if(chooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					try {
						File file = chooser.getSelectedFile();
						String path = file.getPath();
						if(!path.endsWith(".png")) {
							path += ".png";
						}
						file = new File(path);
						if(file.exists()) {
							file.delete();
						}
						ImageIO.write(lblPreview.getAsBufferedImage(rectangles[0]), "PNG", file);
					}
					catch(final Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			@Override
			public final void mouseEntered(final MouseEvent event) {}

			@Override
			public final void mouseExited(final MouseEvent event) {}

			@Override
			public final void mousePressed(final MouseEvent event) {}

			@Override
			public final void mouseReleased(final MouseEvent event) {}
			
		});
		lblPreview.addMouseMotionListener(new MouseMotionListener() {
			
			private int current = lblPreview.getCursor().getType();
			
			@Override
		    public final void mouseMoved(final MouseEvent event) {
				if(lblPreview.getRectanglesAt(event.getPoint()).length > 0) {
					if(current != Cursor.HAND_CURSOR) {
						lblPreview.setCursor(Cursor.getPredefinedCursor(current = Cursor.HAND_CURSOR));
					}
				}
				else {
					if(current != Cursor.DEFAULT_CURSOR) {
						lblPreview.setCursor(Cursor.getPredefinedCursor(current = Cursor.DEFAULT_CURSOR));
					}
				}
			}

			@Override
			public final void mouseDragged(final MouseEvent event) {}
			
		});
		lblPreview.setVerticalAlignment(JLabel.TOP);
		btnMatrice.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				final JSpinner spinner = new JSpinner();
				spinner.setValue(3);
				final JLabel size = new JLabel("3 x 3");
				spinner.addChangeListener(new ChangeListener() {

					@Override
					public final void stateChanged(final ChangeEvent event) {
						final Integer value = getSpinnerValue(spinner);
						size.setText(value == null ? "Valeur invalide !" : value + " x " + value);
					}

				});
				while(true) {
					if(JOptionPane.showConfirmDialog(MainFrame.this, new Component[]{spinner, size}, "Taille de la matrice", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
						final Integer value = getSpinnerValue(spinner);
						if(value == null) {
							continue;
						}
						new MatrixDialog(MainFrame.this, value).setVisible(true);
					}
					break;
				}
			}

			private final Integer getSpinnerValue(final JSpinner spinner) {
				final Integer value = Utils.toInt(spinner.getValue().toString());
				return value != null && (value == 3 || value == 5) ? value : null;
			}

		});
		btnMatrice.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_matrix.png")));
		btnMatrice.setEnabled(false);
		btnReconnaissanceFaciale.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				final WaitingDialog dialog = new WaitingDialog(MainFrame.this);
				dialog.setVisible(true);
				new Thread() {
					
					@Override
					public final void run() {
						final Rect[] faces = OpenCVUtils.getFaces(lblPreview.peekFromStack());
						if(faces.length == 0) {
							JOptionPane.showMessageDialog(MainFrame.this, "Pas de visage sur cette image !");
						}
						else {
							lblPreview.clearRectangles();
							for(final Rect face : faces) {
								lblPreview.addRectangle(new Rectangle(face.x, face.y, face.width, face.height));
							}
							lblPreview.paintComponent(lblPreview.getGraphics());
							// TODO Dialogue avec ouverture d'une autre image et comparaison (+ annotations).
						}
						dialog.close();
					}
					
				}.start();
			}
			
		});
		btnReconnaissanceFaciale.setEnabled(false);
		btnPlus.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				zoom(zoom + 10);
			}

		});
		btnPlus.setEnabled(false);
		btnMoins.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				zoom(zoom - 10);
			}

		});
		btnMoins.setEnabled(false);
		chckbxAntialiasing.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				applyAntialiasing(chckbxAntialiasing.isSelected());
			}
			
		});
		chckbxAntialiasing.setEnabled(false);
		btnAnnuler.addActionListener(undo);
		btnAnnuler.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_undo.png")));
		btnAnnuler.setEnabled(false);
		final Container pane = this.getContentPane();
		final GroupLayout groupLayout = new GroupLayout(pane);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollBar, GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(btnMatrice, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
							.addComponent(btnAnnuler, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
							.addComponent(btnReconnaissanceFaciale, Alignment.LEADING))
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
							.addComponent(btnMoins, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnPlus, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
						.addComponent(chckbxAntialiasing)
						.addComponent(lblZoom, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollBar, GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnMatrice)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnReconnaissanceFaciale)
							.addGap(18)
							.addComponent(lblZoom)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnPlus)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnMoins)
							.addGap(18)
							.addComponent(chckbxAntialiasing)
							.addPreferredGap(ComponentPlacement.RELATED, 309, Short.MAX_VALUE)
							.addComponent(btnAnnuler)))
					.addContainerGap())
		);
		pane.setLayout(groupLayout);
	}
	
	/**
	 * Création du menu pour le GUI.
	 * 
	 * @return Le menu.
	 */

	private final JMenuBar createMenu() {
		final JMenuBar menu = new JMenuBar();
		final JMenu fichier = new JMenu("Fichier");
		final JMenuItem ouvrir = new JMenuItem("Ouvrir...");
		ouvrir.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("Fichier bitmap (*.bmp)", "bmp"));
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
				chooser.setMultiSelectionEnabled(false);
				if(chooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					open(chooser.getSelectedFile());
				}
			}

		});
		ouvrir.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_open.png")));
		fichier.add(ouvrir);
		final JMenuItem enregistrerSous = new JMenuItem("Enregistrer sous...");
		enregistrerSous.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				if(lblPreview.stackSize() == 0) {
					return;
				}
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("Fichier bitmap (*.bmp)", "bmp"));
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
				chooser.setMultiSelectionEnabled(false);
				if(chooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					save(chooser.getSelectedFile());
				}
			}

		});
		enregistrerSous.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_saveas.png")));
		fichier.addSeparator();
		fichier.add(enregistrerSous);
		fichier.addSeparator();
		fichier.add(fichiersRecents);
		refreshPaths();
		menu.add(fichier);
		final JMenu edition = new JMenu("Édition");
		final JMenuItem annuler = new JMenuItem("Annuler");
		annuler.addActionListener(undo);
		annuler.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_undo.png")));
		edition.add(annuler);
		menu.add(edition);
		return menu;
	}
	
	/**
	 * Création du menu pour le label de prévisualisation.
	 * 
	 * @return Le menu.
	 */
	
	public final JPopupMenu createLabelMenu() {
		final JPopupMenu menu = new JPopupMenu();
		final JMenuItem enregistrerPrevisualisation = new JMenuItem("Enregistrer la prévisualisation...");
		enregistrerPrevisualisation.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("Fichier PNG (*.png)", "png"));
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
				chooser.setMultiSelectionEnabled(false);
				if(chooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					try {
						File file = chooser.getSelectedFile();
						String path = file.getPath();
						if(!path.endsWith(".png")) {
							path += ".png";
						}
						file = new File(path);
						if(file.exists()) {
							file.delete();
						}
						ImageIO.write(lblPreview.getAsBufferedImage(), "PNG", file);
					}
					catch(final Exception ex) {
						JOptionPane.showMessageDialog(MainFrame.this, "<html>Impossible d'enregistrer la prévisualisation !<br>" + ex.getClass().getName() + "</html>", "Erreur !", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
				}
			}
			
		});
		enregistrerPrevisualisation.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_open.png")));
		menu.add(enregistrerPrevisualisation);
		return menu;
	}
	
	/**
	 * Ouvre un fichier (doit être une image BMP).
	 * 
	 * @param file Le fichier.
	 */
	
	public final void open(final File file) {
		try {
			if(!file.exists()) {
				saveToHistory(file.getPath());
				return;
			}
			final BufferedImage image = ImageIO.read(file);
			if(image == null) {
				return;
			}
			lblPreview.clearStack();
			lblPreview.setIcon(image, true);
			btnMatrice.setEnabled(true);
			final String path = file.getPath();
			MainFrame.this.setTitle("Traitement image - " + path);
			saveToHistory(path);
			zoom(0);
			lblPreview.setComponentPopupMenu(this.createLabelMenu());
		}
		catch(final Exception ex) {
			JOptionPane.showMessageDialog(this, "<html>Impossible d'appliquer ce fichier !<br>" + ex.getClass().getName() + "</html>", "Erreur !", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	/**
	 * Enregistrement de l'élément qui se situe au dessus de la pile (champ <i>images</i>) dans un fichier.
	 * 
	 * @param file Le fichier.
	 */
	
	public final void save(File file) {
		try {
			String path = file.getPath();
			if(!path.endsWith(".bmp")) {
				path += ".bmp";
			}
			file = new File(path);
			if(file.exists()) {
				file.delete();
			}
			ImageIO.write(lblPreview.peekFromStack(), "BMP", file);
			MainFrame.this.setTitle("Traitement image - " + path);
			saveToHistory(path);
		}
		catch(final Exception ex) {
			JOptionPane.showMessageDialog(MainFrame.this, "<html>Impossible d'enregistrer ce fichier !<br>" + ex.getClass().getName() + "</html>", "Erreur !", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	/**
	 * Application ou non d'un filtre d'antialisating.
	 * 
	 * @param apply </i>true</i> Application du filtre.
	 * <br><i>false</i> Enlèvement de l'antialiasing.
	 */
	
	public final void applyAntialiasing(final boolean apply) {
		if(apply) {
			antialiasing = lblPreview.getAsBufferedImage();
			/* https://code.google.com/p/raytraceplusplus/wiki/AntiAliasing */
			lblPreview.setIcon(new ConvolveOp(new Kernel(3, 3, new float[]{
					0f, .2f, 0f,
					.2f, .2f, .2f,
					0f, .2f, 0f
			})).filter(antialiasing, null), false);
		}
		else {
			lblPreview.setIcon(antialiasing, false);
			antialiasing = null;
		}
	}
	
	/**
	 * Enregistre le chemin dans l'historique.
	 * 
	 * @param path Le chemin.
	 */
	
	private final void saveToHistory(final String path) {
		boolean needSoSave = false;
		if(Main.settings.lastFiles.contains(path)) {
			Main.settings.lastFiles.removeAll(Collections.singleton(path));
			needSoSave = true;
		}
		if(new File(path).exists()) {
			Main.settings.lastFiles.add(0, path);
			needSoSave = true;
		}
		if(needSoSave) {
			try {
				Main.settings.save();
			}
			catch(final Exception ex) {
				ex.printStackTrace();
			}
		}
		refreshPaths();
	}
	
	/**
	 * Rafraichissement des chemins (et donc du menu).
	 */
	
	private final void refreshPaths() {
		boolean needToSave = false;
		fichiersRecents.removeAll();
		for(final String lastFile : new ArrayList<String>(Main.settings.lastFiles)) {
			final File file = new File(lastFile);
			if(!file.exists()) {
				Main.settings.lastFiles.removeAll(Collections.singleton(lastFile));
				needToSave = true;
				continue;
			}
			final JMenuItem lastFileItem = new JMenuItem(lastFile);
			lastFileItem.addActionListener(new ActionListener() {

				@Override
				public final void actionPerformed(final ActionEvent event) {
					open(file);
				}
					
			});
			fichiersRecents.add(lastFileItem);
		}
		if(needToSave) {
			try {
				Main.settings.save();
			}
			catch(final Exception ex) {
				ex.printStackTrace();
			}
		}
		if(fichiersRecents.getMenuComponentCount() > 0) {
			fichiersRecents.addSeparator();
			final JMenuItem vider = new JMenuItem("Vider la liste");
			vider.addActionListener(new ActionListener() {
				
				@Override
				public final void actionPerformed(final ActionEvent event) {
					try {
						Main.settings.lastFiles.clear();
						Main.settings.save();
						refreshPaths();
					}
					catch(final Exception ex) {
						ex.printStackTrace();
					}
				}
				
			});
			fichiersRecents.add(vider);
		}
	}
	
	/**
	 * Application d'une matrice.
	 * 
	 * @param matrix La matrice.
	 * @param size La taille de cette matrice (on assume que cette taille est la même pour la hauteur comme pour la largeur).
	 */
	
	public final void applyMatrix(final float[] matrix, final int size) {
		try {
			if(size != 3 && size != 5) {
				return;
			}
			zoom(0);
			lblPreview.setIcon(new ConvolveOp(new Kernel(size, size, matrix)).filter(lblPreview.peekFromStack(), null), true);
			btnAnnuler.setEnabled(true);
		}
		catch(final Exception ex) {
			JOptionPane.showMessageDialog(this, "<html>Impossible d'appliquer cette matrice !<br>" + ex.getClass().getName() + "</html>", "Erreur !", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	/**
	 * Zoom sur la prévisualisation et application de l'antialiasing en conséquence (si sélectionné).
	 * 
	 * @param zoom Le zoom (0 <= zoom <= 100).
	 */

	public final void zoom(final int zoom) {
		if(zoom < 0 || zoom > 100) {
			return;
		}
		this.zoom = (short)zoom;
		lblZoom.setText("Zoom (" + zoom + "%) :");
		if(zoom == 100) {
			btnPlus.setEnabled(false);
			btnMoins.setEnabled(false);
		}
		else if(zoom == 0) {
			btnReconnaissanceFaciale.setEnabled(true);
			btnPlus.setEnabled(true);
			btnMoins.setEnabled(false);
			if(antialiasing != null) {
				applyAntialiasing(false);
			}
			chckbxAntialiasing.setEnabled(false);
			chckbxAntialiasing.setSelected(false);
			lblPreview.setIcon(lblPreview.peekFromStack(), false);
			return;
		}
		if(zoom > 0) {
			btnMoins.setEnabled(true);
			chckbxAntialiasing.setEnabled(true);
			btnReconnaissanceFaciale.setEnabled(false);
		}
		if(zoom < 100) {
			btnPlus.setEnabled(true);
		}
		final AffineTransform transform = new AffineTransform();
		transform.scale(zoom * .15f, zoom * .15f);
		lblPreview.setIcon(new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(lblPreview.peekFromStack(), null), false);
		if(antialiasing != null) {
			applyAntialiasing(true);
		}
	}
}