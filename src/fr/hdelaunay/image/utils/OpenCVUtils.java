package fr.hdelaunay.image.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import fr.hdelaunay.image.Main;

public class OpenCVUtils {
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * Cherche les visages sur l'image sélectionnée.
	 * <br>https://github.com/Itseez/opencv/tree/master/data (les différents xml possibles).
	 * 
	 * @param input L'image.
	 * 
	 * @return Le contour de ces visages.
	 */

	public static final Rect[] getFaces(final BufferedImage input) {
		return getTraits(input, "lbpcascade_frontalface.xml");
	}
	
	/**
	 * Cherche les traits du visage spécifié par le fichier xml.
	 * 
	 * @param input Le visage.
	 * @param xml Les traits.
	 * 
	 * @return Les traits de ce visage.
	 */
	
	public static final Rect[] getTraits(final BufferedImage input, final String xml) {
		final Mat image = new Mat(input.getWidth(), input.getHeight(), CvType.CV_8UC3);
		image.put(0, 0, ((DataBufferByte)input.getRaster().getDataBuffer()).getData());
		final MatOfRect faces = new MatOfRect();
		new CascadeClassifier(Main.class.getResource(Main.RES_PACKAGE + "opencv/" + xml).getPath().substring(1)).detectMultiScale(image, faces);
		return faces.toArray();
	}

}