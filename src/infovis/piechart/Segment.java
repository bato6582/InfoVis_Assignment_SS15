package infovis.piechart;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Point2D;


public class Segment {
	public String label;
	public Polygon poly;
	public Data data;
	public Point2D.Double label_pos;
	public Color color;
	public boolean categoric;
	public double percent;
	private double angle = 0.3;
	
	public Segment() {
		label = "dummie";
		poly = null;
		data = null;
		label_pos = new Point2D.Double(0,0);
		color = Color.BLACK;
		categoric = false;
		percent = 0.0;
	}
	
	
	public Segment (String lbl,  Data dt, Color clr, boolean cat, double perc) {
		label = lbl;
		poly = null;
		data = dt;
		label_pos = new Point2D.Double(0,0);
		color = clr;
		categoric = cat;
		percent = perc;
	}
	
	
	
	public static Point2D.Double rotatePoint(Point2D.Double point, Point2D.Double center, double angle) {
		angle *= Math.PI / 180;
		double x = center.getX() + (point.getX() -  center.getX()) * Math.cos(angle) - (point.getY() - center.getY()) * Math.sin(angle);
		double y = center.getY() + (point.getX() -  center.getX()) * Math.sin(angle) + (point.getY() - center.getY()) * Math.cos(angle);
		return new Point2D.Double(x, y);
	}
	
	
	public void createPolygon (Point2D.Double center, Point2D.Double start_pos, Point2D.Double end_pos, double radius, double whole_angle, int number_labels, double next_radius) {		
		int array_size = (int) (whole_angle / -angle) + 3;
		int[] xs = new int[array_size];
		int[] ys = new int[array_size];
		
		xs[0] = (int) center.getX();
		ys[0] = (int) center.getY();
		
		xs[1] = (int) start_pos.getX();
		ys[1] = (int) start_pos.getY();
		
		Point2D.Double point = rotatePoint(start_pos, center, -angle);

		int label_pos_to_be = (int) (array_size * 0.5);
		//System.out.println(whole_angle + " " + array_size);
		for (int i = 2; i < array_size - 1; i++) {
			// save polygon points
			xs[i] = (int) point.getX();
			ys[i] = (int) point.getY();
			
			point = rotatePoint(point, center, -angle);
			
			// label pos
			if (i == label_pos_to_be) {
				double x = center.getX() - point.getX();
				double y = center.getY() - point.getY();
				double length = Math.sqrt(x*x + y*y);
				x /= length;
				y /= length;
				double distance_from_center = next_radius >= 0 ? (0.5*(radius + next_radius)) : 0.5 * radius;
				//System.out.println(label + ": X: " + x + "  Y: " + y + "  distance: " + distance_from_center);
				//System.out.println("radius: " + radius + "  prev_radius:" + next_radius);
				x = center.getX() - (x) * distance_from_center;
				y = center.getY() - (y) * distance_from_center;
				
				label_pos = new Point2D.Double(x, y);
			}
		}
		
		xs[array_size - 1] = (int) end_pos.getX();
		ys[array_size - 1] = (int) end_pos.getY();
		
		poly = new Polygon(xs, ys, array_size);
	}
	
	
}
