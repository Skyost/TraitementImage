package fr.hdelaunay.image.frames;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.util.Stack;

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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.hdelaunay.image.Main;
import fr.hdelaunay.image.dialogs.MatrixDialog;
import fr.hdelaunay.image.utils.Utils;
import java.awt.Toolkit;
import javax.swing.JSlider;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private final Stack<BufferedImage> images = new Stack<BufferedImage>();
	private final JLabel lblPreview = new JLabel();
	private final JButton btnMatrice = new JButton("Appliquer matrice...");
	private final JSlider slrZoom = new JSlider();
	private final JButton btnAnnuler = new JButton("Annuler");

	private final ActionListener undo = new ActionListener() {

		@Override
		public final void actionPerformed(final ActionEvent event) {
			if(images.size() <= 1) {
				return;
			}
			images.pop();
			lblPreview.setIcon(new ImageIcon(images.peek()));
			if(images.size() == 1) {
				btnAnnuler.setEnabled(false);
			}
		}

	};

	public MainFrame() {
		this.setTitle("Traitement image");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource(Main.RES_PACKAGE + "icon_app.png")));
		this.setSize(680, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(this.createMenu());
		final JScrollPane scrollBar = new JScrollPane(lblPreview);
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
		final JLabel lblZoom = new JLabel("Zoom (0%) :");
		slrZoom.addChangeListener(new ChangeListener() {

			@Override
			public final void stateChanged(final ChangeEvent event) {
				try {
					int zoomLevel = slrZoom.getValue();
					lblZoom.setText("Zoom (" + zoomLevel + "%) :");
					final BufferedImage image = images.pop();
					final int newImageWidth = image.getWidth() * ++zoomLevel;
					final int newImageHeight = image.getHeight() * zoomLevel;
					final BufferedImage resizedImage = new BufferedImage(newImageWidth , newImageHeight, image.getType());
					final Graphics2D graphics = resizedImage.createGraphics();
					graphics.drawImage(image, 0, 0, newImageWidth , newImageHeight , null);
					graphics.dispose();
					lblPreview.setIcon(new ImageIcon(images.push(resizedImage)));
				}
				catch(final Exception ex) {
					ex.printStackTrace();
				}
			}
			
		});
		slrZoom.setValue(0);
		slrZoom.setEnabled(false);
		btnAnnuler.addActionListener(undo);
		btnAnnuler.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_undo.png")));
		btnAnnuler.setEnabled(false);
		final Container pane = this.getContentPane();
		final GroupLayout groupLayout = new GroupLayout(pane);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollBar, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addComponent(btnMatrice, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
							.addGroup(Alignment.TRAILING, groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblZoom)
								.addComponent(slrZoom, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btnAnnuler, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
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
							.addGap(18)
							.addComponent(lblZoom)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(slrZoom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 408, Short.MAX_VALUE)
							.addComponent(btnAnnuler)))
					.addContainerGap())
		);
		pane.setLayout(groupLayout);
	}

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
					try {
						final File file = chooser.getSelectedFile();
						final BufferedImage image = ImageIO.read(file);
						if(image == null) {
							return;
						}
						images.clear();
						lblPreview.setIcon(new ImageIcon(images.push(image)));
						btnMatrice.setEnabled(true);
						slrZoom.setEnabled(true);
						MainFrame.this.setTitle("Traitement image - " + file.getPath());
					}
					catch(final Exception ex) {
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
				if(images.size() == 0) {
					return;
				}
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("Fichier bitmap (*.bmp)", "bmp"));
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
				chooser.setMultiSelectionEnabled(false);
				if(chooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					try {
						String path = chooser.getSelectedFile().getPath();
						if(!path.endsWith(".bmp")) {
							path += ".bmp";
						}
						final File file = new File(path);
						if(file.exists()) {
							file.delete();
						}
						final BufferedImage image = new BufferedImage(lblPreview.getWidth(), lblPreview.getHeight(), BufferedImage.TYPE_INT_ARGB);
						final Graphics2D graphics = image.createGraphics();
						lblPreview.printAll(graphics);
						graphics.dispose();
						ImageIO.write(image, "BMP", file);
						MainFrame.this.setTitle("Traitement image - " + path);
					}
					catch(final Exception ex) {
						ex.printStackTrace();
					}
				}
			}

		});
		enregistrerSous.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_saveas.png")));
		fichier.addSeparator();
		fichier.add(enregistrerSous);
		menu.add(fichier);
		final JMenu edition = new JMenu("Édition");
		final JMenuItem annuler = new JMenuItem("Annuler");
		annuler.addActionListener(undo);
		annuler.setIcon(new ImageIcon(Main.class.getResource(Main.RES_PACKAGE + "icon_undo.png")));
		edition.add(annuler);
		menu.add(edition);
		return menu;
	}

	public final void applyMatrix(final float[] matrix, final int size) {
		try {
			if(size != 3 && size != 5) {
				return;
			}
			lblPreview.setIcon(new ImageIcon(images.push(new ConvolveOp(new Kernel(size, size, matrix)).filter(images.peek(), null))));
			btnAnnuler.setEnabled(true);
		}
		catch(final Exception ex) {
			JOptionPane.showMessageDialog(this, "<html>Impossible d'appliquer cette matrice !<br>" + ex.getClass().getName() + "</html>", "Erreur !", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
}