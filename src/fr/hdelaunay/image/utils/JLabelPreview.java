package fr.hdelaunay.image.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class JLabelPreview extends JLabel {

	private static final long serialVersionUID = 1L;
	
	private final Stack<BufferedImage> images = new Stack<BufferedImage>();
	private final Set<Rectangle> rectangles = new HashSet<Rectangle>();
	
	@Override
	public final void setIcon(final Icon icon) {}
	
	@Override
	public final void paintComponent(final Graphics graphics) {
		super.paintComponent(graphics);
		for(final Rectangle rectangle : rectangles) {
			graphics.setColor(Utils.randomColor());
			graphics.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		}
	}
	
	public final void setIcon(final BufferedImage image, final boolean pushToStack) {
		super.setIcon(new ImageIcon(pushToStack ? this.pushToStack(image) : image));
	}
	
	public final BufferedImage getAsBufferedImage() {
		return getAsBufferedImage(null);
	}
	
	public final BufferedImage getAsBufferedImage(final Rectangle rectangle) {
		final BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		this.printAll(graphics);
		graphics.dispose();
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
	
	public final void addRectangle(final Rectangle rectangle) {
		rectangles.add(rectangle);
	}
	
	public final Rectangle[] getRectanglesAt(final Point point) {
		final List<Rectangle> rectangles = new ArrayList<Rectangle>();
		for(final Rectangle rectangle : this.rectangles) {
			if(rectangle.contains(point)) {
				rectangles.add(rectangle);
			}
		}
		return rectangles.toArray(new Rectangle[rectangles.size()]);
	}
	
	public final void clearRectangles() {
		rectangles.clear();
	}
	
}