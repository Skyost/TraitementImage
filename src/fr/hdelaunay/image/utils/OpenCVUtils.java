package fr.hdelaunay.image.utils;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	 * @return Les visages.
	 */

	public static final Face[] getFaces(final BufferedImage input) {
		final List<Face> faces = new ArrayList<Face>();
		for(final Rect face : getTraits(input, "lbpcascade_frontalface.xml")) {
			final List<Rect> detectedEyes = new ArrayList<Rect>(Arrays.asList(getTraits(input, "haarcascade_eye.xml")));
			final List<Rect> detectedNoses = new ArrayList<Rect>(Arrays.asList(getTraits(input, "haarcascade_nose.xml")));
			final List<Rect> detectedMouths = new ArrayList<Rect>(Arrays.asList(getTraits(input, "haarcascade_mouth.xml")));
			for(final Rect eye : new ArrayList<Rect>(detectedEyes)) {
				if(!isInBounds(face, eye)) {
					detectedEyes.remove(eye);
				}
			}
			for(final Rect nose : new ArrayList<Rect>(detectedNoses)) {
				if(!isInBounds(face, nose)) {
					detectedNoses.remove(nose);
				}
			}
			for(final Rect mouth : new ArrayList<Rect>(detectedMouths)) {
				if(!isInBounds(face, mouth)) {
					detectedMouths.remove(mouth);
				}
			}
			faces.add(new Face(face, detectedEyes.toArray(new Rect[detectedEyes.size()]), detectedNoses.get(0), detectedMouths.get(0)));
		}
		return faces.toArray(new Face[faces.size()]);
	}
	
	public static final Rect[] getTraits(final BufferedImage input, final String xml) {
		final Mat image = new Mat(input.getWidth(), input.getHeight(), CvType.CV_8UC3);
		image.put(0, 0, ((DataBufferByte)input.getRaster().getDataBuffer()).getData());
		final MatOfRect faces = new MatOfRect();
		new CascadeClassifier(Main.class.getResource(Main.RES_PACKAGE + "opencv/" + xml).getPath().substring(1)).detectMultiScale(image, faces);
		return faces.toArray();
	}
	
	public static final boolean isInBounds(final Rect bounds, final Rect rect) {
		return bounds.contains(rect.tl()) && bounds.contains(rect.br());
	}
	
	public static class Face {
		
		private final Color color = Utils.randomColor();
		
		private final Rectangle bounds;
		private final Rectangle[] eyes;
		private final Rectangle nose;
		private final Rectangle mouth;
		
		public Face(final Rect bounds, final Rect[] eyes, final Rect nose, final Rect mouth) {
			this.bounds = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
			this.eyes = new Rectangle[]{new Rectangle(eyes[0].x, eyes[0].y, eyes[0].width, eyes[0].height), new Rectangle(eyes[1].x, eyes[1].y, eyes[1].width, eyes[1].height)};
			this.nose = new Rectangle(nose.x, nose.y, nose.width, nose.height);
			this.mouth = new Rectangle(mouth.x, mouth.y, mouth.width, mouth.height);
		}
		
		public final Color getBoundsColor() {
			return color;
		}
		
		public final Rectangle getBounds() {
			return bounds;
		}
		
		public final Rectangle[] getEyes() {
			return eyes;
		}
		
		public final Rectangle getNose() {
			return nose;
		}
		
		public final Rectangle getMouth() {
			return mouth;
		}
		
	}

}