package fr.hdelaunay.image.frames;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.hdelaunay.image.filters.Filter;
import fr.hdelaunay.image.filters.Lissage;
import fr.hdelaunay.image.filters.Sobel;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final Stack<BufferedImage> images = new Stack<BufferedImage>();
	private final JLabel lblPreview = new JLabel();
	private final JButton btnSobel = new JButton("Filtre Sobel");
	private final JButton btnLissage = new JButton("Lissage");
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
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(this.createMenu());
		final JScrollPane scrollBar = new JScrollPane(lblPreview);
		btnSobel.setEnabled(false);
		btnSobel.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				applyFilter(new Sobel());
			}

		});
		btnLissage.setEnabled(false);
		btnLissage.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				applyFilter(new Lissage());
			}

		});
		btnAnnuler.addActionListener(undo);
		btnAnnuler.setEnabled(false);
		final Container pane = this.getContentPane();
		final GroupLayout groupLayout = new GroupLayout(pane);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(scrollBar, GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.TRAILING)
										.addGroup(
												groupLayout
														.createParallelGroup(Alignment.LEADING, false)
														.addComponent(btnLissage, Alignment.TRAILING,
																GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(btnSobel, Alignment.TRAILING))
										.addComponent(btnAnnuler, GroupLayout.PREFERRED_SIZE, 89,
												GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addComponent(scrollBar, GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
										.addGroup(
												groupLayout
														.createSequentialGroup()
														.addComponent(btnSobel)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(btnLissage)
														.addPreferredGap(ComponentPlacement.RELATED, 444,
																Short.MAX_VALUE).addComponent(btnAnnuler)))
						.addContainerGap()));
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
						final BufferedImage image = ImageIO.read(chooser.getSelectedFile());
						if(image == null) {
							return;
						}
						images.clear();
						lblPreview.setIcon(new ImageIcon(images.push(image)));
						btnSobel.setEnabled(true);
						btnLissage.setEnabled(true);
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}

		});
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
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}

		});
		fichier.addSeparator();
		fichier.add(enregistrerSous);
		menu.add(fichier);
		final JMenu edition = new JMenu("Edition");
		final JMenuItem annuler = new JMenuItem("Annuler");
		annuler.addActionListener(undo);
		edition.add(annuler);
		menu.add(edition);
		return menu;
	}

	private final void applyFilter(final Filter filter) {
		lblPreview.setIcon(new ImageIcon(images.push(filter.filter(images.peek()))));
		btnAnnuler.setEnabled(true);
	}

}