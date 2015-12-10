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
				if(eyes[0] != null) {
					drawRect(graphics, eyes[0]);
				}
				if(eyes[1] != null) {
					drawRect(graphics, eyes[1]);
				}
				final Rectangle nose = face.getNose();
				if(nose != null) {
					drawRect(graphics, nose);
				}
				final Rectangle mouth = face.getMouth();
				if(mouth != null) {
					drawRect(graphics, mouth);
				}
			}
		}
	}
	
	private final void drawRect(final Graphics graphics, final Rectangle rectangle) {
		graphics.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
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