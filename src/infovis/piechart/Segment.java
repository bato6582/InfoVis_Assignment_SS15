package infovis.piechart;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;


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
	
	
	/*
	
	public void createPolygon (Point2D.Double center, double radius , double step_number, double angle, int number_labels, double next_radius) {		
		double rotation_angle = (360) / ( (double) step_number);
		
		Point2D.Double starting_point = new Point2D.Double(center.getX(), center.getY() - radius);
		Point2D.Double point = rotatePoint(starting_point, center, rotation_angle);

		int array_size = (int) ( (step_number + 2) * percent) + 1;
		int label_pos_to_be = (int) (array_size * 0.5);

		int[] xs = new int[array_size];
		int[] ys = new int[array_size];
		xs[0] = (int) center.getX();
		ys[0] = (int) center.getY();
		
		for (int i = 1; i < array_size - 1; i++) {
			// save polygon points
			point = rotatePoint(point, center, -rotation_angle);
			Point2D.Double point3 = rotatePoint(point, center, angle);
			xs[i] = (int) point3.getX();
			ys[i] = (int) point3.getY();
			
			// label pos
			if (i == label_pos_to_be) {
				double x = center.getX() - point3.getX();
				double y = center.getY() - point3.getY();
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
		
		point = rotatePoint(starting_point, center, (angle + (-360) * percent));
		xs[array_size - 1] = (int) point.getX();
		ys[array_size - 1] = (int) point.getY();
		

		
		poly = new Polygon(xs, ys, array_size);
	
	}*/
	
	public void createPolygon (Point2D.Double center, Point2D.Double start_pos, Point2D.Double end_pos, double radius, double whole_angle, int number_labels, double next_radius) {		
		int step_number = (int) (whole_angle / -angle);
		System.out.println("whole angle: " + whole_angle + " step_number: " + step_number);
		
		int array_size = step_number + 3;
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
