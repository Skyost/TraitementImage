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

	/**
	 * 
	 * <br>https://github.com/Itseez/opencv/tree/master/data.
	 * 
	 * @param input
	 * @return
	 */

	public static final Rect[] getFaces(final BufferedImage input) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		final Mat image = new Mat(input.getWidth(), input.getHeight(), CvType.CV_8UC3);
		image.put(0, 0, ((DataBufferByte)input.getRaster().getDataBuffer()).getData());
		final MatOfRect faces = new MatOfRect();
		new CascadeClassifier(Main.class.getResource(Main.RES_PACKAGE + "lbpcascade_frontalface.xml").getPath().substring(1)).detectMultiScale(image, faces);
		return faces.toArray();
	}

}