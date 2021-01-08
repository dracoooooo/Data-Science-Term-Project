/* 
 * 	$Id: Board.java,v 1.6 2007/11/28 16:30:18 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package mirroruniverse.sim;

import java.awt.geom.Point2D;


/**
 * @author Jon Bell
 * 
 */
public final class MUBoard {
	public static final int pixels_per_meter = 360;
	public static double pixels_per_pixel = 10;
	private int width;
	private int height;

	public static Point2D toScreenSpace(Point2D point2d) {
		Point2D clone = new Point2D.Double();
		clone.setLocation(toScreenSpace(point2d.getX()),
				toScreenSpace(point2d.getY()));
		return clone;
	}
	public static double fromScreenSpace(double v) {
		return (v * pixels_per_pixel / pixels_per_meter) - MUGameConfig.d;
	}

	public static double toScreenSpace(double v) {
		return (MUGameConfig.d+ v) * pixels_per_meter / pixels_per_pixel;
	}
	public static double toScreenSpaceNoOffset(double v) {
		return (v) * pixels_per_meter / pixels_per_pixel;
	}
	public static Point2D fromScreenSpace(Point2D p) {
		Point2D r = new Point2D.Double();
		r.setLocation(fromScreenSpace(p.getX()), fromScreenSpace(p.getY()));
		return r;
	}

	public MUBoard() {
		this(100,100);

	}

	public MUBoard(int width, int height) {
//		this.width = width;
//		this.height = height;
		init();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	private void init() {

	}

	public boolean inBounds(int x, int y) {
		return (x >= 0 && x < width) && (y >= 0 && y < height);
	}

}
