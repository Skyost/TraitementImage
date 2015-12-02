package fr.hdelaunay.image.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import fr.hdelaunay.image.utils.OpenCVUtils.Face;

public class JLabelPreview extends JLabel {

	private static final long serialVersionUID = 1L;
	
	private final Stack<BufferedImage> images = new Stack<BufferedImage>();
	private final Set<Face> faces = new HashSet<Face>();
	
	private boolean paintFaces = true;
	
	@Override
	public final void setIcon(final Icon icon) {}
	
	@Override
	public final void paintComponent(final Graphics graphics) {
		super.paintComponent(graphics);
		if(paintFaces) {
			for(final Face face : faces) {
				graphics.setColor(face.getBoundsColor());
				final Rectangle bounds = face.getBounds();
				graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
				final Rectangle[] eyes = face.getEyes();
				graphics.drawRect(eyes[0].x, eyes[0].y, eyes[0].width, eyes[0].height);
				graphics.drawRect(eyes[1].x, eyes[1].y, eyes[1].width, eyes[1].height);
				final Rectangle nose = face.getNose();
				graphics.drawRect(nose.x, nose.y, nose.width, nose.height);
				final Rectangle mouth = face.getMouth();
				graphics.drawRect(mouth.x, mouth.y, mouth.width, mouth.height);
			}
		}
	}
	
	public final void setIcon(final BufferedImage image, final boolean pushToStack) {
		super.setIcon(new ImageIcon(pushToStack ? this.pushToStack(image) : image));
	}
	
	public final BufferedImage getAsBufferedImage(final boolean withFaces) {
		return getAsBufferedImage(withFaces, null);
	}
	
	public final BufferedImage getAsBufferedImage(final boolean withFaces, final Rectangle rectangle) {
		final BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		paintFaces = withFaces;
		this.printAll(graphics);
		graphics.dispose();
		paintFaces = true;
		return rectangle == null ? image : image.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}
	
	public final BufferedImage pushToStack(final BufferedImage image) {
		return images.push(image);
	}
	
	public final BufferedImage peekFromStack() {
		return images.peek();
	}
	
	public final BufferedImage popFromStack() {
		return images.pop();
	}
	
	public final void clearStack() {
		images.clear();
	}
	
	public final int stackSize() {
		return images.size();
	}
	
	public final void addFaces(final Face... faces) {
		this.faces.addAll(Arrays.asList(faces));
	}
	
	public final Set<Face> getFacesAt(final Point point) {
		final Set<Face> faces = new HashSet<Face>();
		for(final Face face : this.faces) {
			if(face.getBounds().contains(point)) {
				faces.add(face);
			}
		}
		return faces;
	}
	
	public final void clearFaces() {
		faces.clear();
	}
	
}