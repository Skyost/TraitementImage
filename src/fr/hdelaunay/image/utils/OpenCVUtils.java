package fr.hdelaunay.image.utils;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
		final Rect[] detectedFaces = getFirstFacesNonNull(input);
		if(detectedFaces == null) {
			return null;
		}
		for(final Rect face : detectedFaces) {
			final List<Rect> detectedEyes = new ArrayList<Rect>(Arrays.asList(getTraits(input, "eyes.xml")));
			final List<Rect> detectedNoses = new ArrayList<Rect>(Arrays.asList(getTraits(input, "nose.xml")));
			final List<Rect> detectedMouths = new ArrayList<Rect>(Arrays.asList(getTraits(input, "mouth.xml")));
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
			faces.add(new Face(face, detectedEyes.toArray(new Rect[detectedEyes.size()]), detectedNoses.size() > 0 ? detectedNoses.get(0) : null, detectedMouths.size() > 0 ? detectedMouths.get(0) : null));
		}
		return faces.toArray(new Face[faces.size()]);
	}
	
	private static final Rect[] getFirstFacesNonNull(final BufferedImage input) {
		for(int i = 1; i != 6; i++) {
			final Rect[] face = getTraits(input, "face_" + i + ".xml");
			if(face != null && face.length > 0) {
				return face;
			}
		}
		return null;
	}
	
	public static final Rect[] getTraits(final BufferedImage input, final String xml) {
		/*final BufferedImage gray = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		final Graphics2D graphics = gray.createGraphics();
		graphics.drawImage(input, 0, 0, null);*/
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
		
		private final Color color;
		private final Rectangle bounds;
		private final Rectangle[] eyes;
		private final Rectangle nose;
		private final Rectangle mouth;
		
		public Face(final Rect bounds, final Rect[] eyes, final Rect nose, final Rect mouth) {
			this(bounds, eyes, nose, mouth, Utils.randomColor());
		}
		
		public Face(final Rect bounds, final Rect[] eyes, final Rect nose, final Rect mouth, final Color color) {
			this(new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height), new Rectangle[]{eyes.length > 0 ? new Rectangle(eyes[0].x, eyes[0].y, eyes[0].width, eyes[0].height) : null, eyes.length > 1 ? new Rectangle(eyes[1].x, eyes[1].y, eyes[1].width, eyes[1].height) : null}, nose != null ? new Rectangle(nose.x, nose.y, nose.width, nose.height) : null, mouth != null ? new Rectangle(mouth.x, mouth.y, mouth.width, mouth.height) : null, color);
		}
		
		public Face(final Rectangle bounds, final Rectangle[] eyes, final Rectangle nose, final Rectangle mouth, final Color color) {
			this.bounds = bounds;
			this.eyes = eyes;
			this.nose = nose;
			this.mouth = mouth;
			this.color = color;
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
		
		public final Document toXML(final BufferedImage faceImage) throws ParserConfigurationException, DOMException, IOException {
			final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			final Element root = document.createElement("content");
			final Element color = document.createElement("color");
			color.setAttribute("value", String.valueOf(this.color.getRGB()));
			root.appendChild(color);
			final Element face = document.createElement("face");
			final Element bounds = document.createElement("bounds");
			bounds.setAttribute("value", Utils.serializableToString(this.bounds));
			face.appendChild(bounds);
			final Element eyes = document.createElement("eyes");
			for(final Rectangle eye : this.eyes) {
				Element domEye = document.createElement("eye");
				domEye.setAttribute("value", Utils.serializableToString(eye));
				eyes.appendChild(domEye);
			}
			face.appendChild(eyes);
			final Element nose = document.createElement("nose");
			nose.setAttribute("value", Utils.serializableToString(this.nose));
			face.appendChild(nose);
			final Element mouth = document.createElement("mouth");
			mouth.setAttribute("value", Utils.serializableToString(this.mouth));
			face.appendChild(mouth);
			root.appendChild(face);
			final Element image = document.createElement("image");
			image.setAttribute("value", Utils.imageToBase64(faceImage, "PNG"));
			root.appendChild(image);
			document.appendChild(root);
			return document;
		}
		
		public static final Object[] fromXML(final Document document) throws ClassNotFoundException, DOMException, IOException {
			final Element root = document.getDocumentElement();
			final List<Rectangle> eyes = new ArrayList<Rectangle>();
			final NodeList face = root.getElementsByTagName("face").item(0).getChildNodes();
			final NodeList domEyes = face.item(1).getChildNodes();
			for(int i = 0; i != domEyes.getLength(); i++) {
				eyes.add((Rectangle)Utils.serializableFromString(domEyes.item(i).getAttributes().getNamedItem("value").getNodeValue()));
			}
			return new Object[]{
							new Face(
											(Rectangle)Utils.serializableFromString(face.item(0).getAttributes().getNamedItem("value").getNodeValue()),
											eyes.toArray(new Rectangle[eyes.size()]),
											(Rectangle)Utils.serializableFromString(face.item(2).getAttributes().getNamedItem("value").getNodeValue()),
											(Rectangle)Utils.serializableFromString(face.item(3).getAttributes().getNamedItem("value").getNodeValue()),
											Color.decode(root.getElementsByTagName("color").item(0).getAttributes().getNamedItem("value").getNodeValue())
							),
							Utils.imageFromBase64(root.getElementsByTagName("image").item(0).getAttributes().getNamedItem("value").getNodeValue())
			};
		}
		
	}

}