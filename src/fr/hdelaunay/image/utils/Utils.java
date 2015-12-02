package fr.hdelaunay.image.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.hdelaunay.image.Main;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Utils {

	/**
	 * Conversion d'un <i>String</i> en <i>Integer</i>.
	 * 
	 * @param value La chaîne <i>String</i>.
	 * 
	 * @return Le nombre <i>Integer</i>.
	 */

	public static final Integer toInt(final String value) {
		try {
			return Integer.parseInt(value);
		}
		catch(final Exception ex) {}
		return null;
	}

	/**
	 * Conversion d'un <i>String</i> en <i>Float</i>.
	 * 
	 * @param value La chaîne <i>String</i>.
	 * 
	 * @return Le nombre à virgule flottante <i>Float</i>.
	 */

	public static final Float toFloat(final String value) {
		try {
			return Float.parseFloat(value);
		}
		catch(final Exception ex) {}
		return null;
	}

	/**
	 * Joint des <i>float</i> par un caractère et retourne la chaîne ainsi créée.
	 * 
	 * @param joiner Le(s) caractère(s).
	 * @param floats Les <i>float</i>.
	 * 
	 * @return La chaîne de <i>float</i>.
	 */

	public static final String joinFloats(final String joiner, final float... floats) {
		final StringBuilder builder = new StringBuilder();
		for(final float floatt : floats) {
			builder.append(floatt + joiner);
		}
		builder.setLength(builder.length() - joiner.length());
		return builder.toString();
	}

	/**
	 * Provient de https://github.com/Skyost/Algogo/.
	 * 
	 * @return Le répertoire dans lequel le programme est éxecuté.
	 * 
	 * @throws URISyntaxException Si la destination est invalide.
	 */

	public static final File getParentFolder() throws URISyntaxException {
		return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
	}

	/**
	 * Retourne une couleur aléatoire.
	 * 
	 * @return Une couleur aléatoire.
	 */

	public static final Color randomColor() {
		final Random random = new Random();
		return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
	}

	/**
	 * Affiche un dialog (JFileChooser).
	 * 
	 * @param parent Le parent.
	 * @param openDialog <b>true</b> Si le dialog doit ouvrir un fichier.
	 * <br><b>false</b> Si le dialog doit enregistrer un fichier.
	 * @param filters Les filtres.
	 * 
	 * @return Le dialog, si validé.
	 */

	public static final JFileChooser showDialog(final Component parent, final boolean openDialog, final FileNameExtensionFilter... filters) {
		final JFileChooser chooser = new JFileChooser();
		if(filters != null && filters.length > 0) {
			chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
			chooser.setFileFilter(filters[0]);
			for(final FileNameExtensionFilter filter : filters) {
				chooser.addChoosableFileFilter(filter);
			}
		}
		chooser.setMultiSelectionEnabled(false);
		if(openDialog) {
			return chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION ? chooser : null;
		}
		return chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION ? chooser : null;
	}

	/**
	 * Enregistre du contenu.
	 * 
	 * @param parent Le parent.
	 * @param chooser Le JFileChooser.
	 * @param content Le contenu.
	 * 
	 * @return Le fichier, si enregistré avec succès.
	 */

	public static final File saveContent(final Component parent, final JFileChooser chooser, final Object content) {
		return saveContent(parent, chooser.getSelectedFile(), ((FileNameExtensionFilter)chooser.getFileFilter()).getExtensions()[0], content);
	}

	/**
	 * Enregistre du contenu.
	 * 
	 * @param parent Le parent.
	 * @param file Le fichier.
	 * @param extension L'extension du fichier (sera vérifiée).
	 * @param content Le contenu.
	 * 
	 * @return Le fichier, si enregistré avec succès.
	 */

	public static final File saveContent(final Component parent, File file, final String extension, final Object content) {
		try {
			String path = file.getPath();
			if(!path.endsWith("." + extension)) {
				path += "." + extension;
			}
			file = new File(path);
			if(file.exists()) {
				file.delete();
			}
			if(content instanceof BufferedImage) {
				if(!ImageIO.write((BufferedImage)content, extension.toUpperCase(), file)) {
					throw new Exception("Impossible d'enregistrer l'image.");
				}
			}
			else {
				Files.write(Paths.get(path), content.toString().getBytes(StandardCharsets.UTF_8));
			}
			return file;
		}
		catch(final Exception ex) {
			JOptionPane.showMessageDialog(parent, "<html>Impossible d'enregistrer !<br>" + ex.getClass().getName() + "</html>", "Erreur !", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
		return null;
	}

	public static final Object serializableFromString(final String string) throws IOException, ClassNotFoundException {
		final byte[] data = new BASE64Decoder().decodeBuffer(string);
		final ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(data));
		final Object object = input.readObject();
		input.close();
		return object;
	}

	public static final String serializableToString(final Serializable serializable) throws IOException {
		final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
		objectOutputStream.writeObject(serializable);
		objectOutputStream.close();
		return new BASE64Encoder().encode(byteOutputStream.toByteArray());
	}

	public static final BufferedImage imageFromBase64(final String string) throws IOException {
		final ByteArrayInputStream input = new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(string));
		final BufferedImage image = ImageIO.read(input);
		input.close();
		return image;
	}

	public static final String imageToBase64(final BufferedImage image, final String type) throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, type, output);
		final String imageString = new BASE64Encoder().encode(output.toByteArray());
		output.close();
		return imageString;
	}

}