package infovis.piechart;

import sun.nio.ch.SelChImpl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

public class View extends JPanel {
	private Rectangle2D cell = new Rectangle2D.Double(0, 0, 0, 0);
	private Rectangle2D dataPoint = new Rectangle2D.Double(0, 0, 0, 0);
	private Rectangle2D markerRectangle = new Rectangle2D.Double(0, 0, 0, 0);
	private boolean[] chosen;
	private Map<String, Rectangle2D.Double[]> axes = new HashMap<String, Rectangle2D.Double[]>();
	public Map<String, Rectangle2D.Double> labelSelectionRectMap = new HashMap<String, Rectangle2D.Double>();
	public Rectangle2D.Double selectedRectangle;

	private double width;
	private double height;


	public double xOffset = 0.0;
	public double oldXOffset = 0.0;
	public String draggedLabel = "";
	public boolean dragLabel = false;
	
	private String path = "data/Geburten_Monat_Sex_2011-2015.csv";
	private static HashMap<Integer, HashMap<String, Data>> dataMap = new HashMap<>();

	public void initialize() throws IOException {
		readFile(path);
		/*
		for (int key : dataMap.keySet()) {
			HashMap<String, Data> map = dataMap.get(key);
			System.out.println("Jahr: " + key + "\n");
			
			for (String month : map.keySet()){
				System.out.println("Monat: " + month + ": " + map.get(month).print());
				
			}
		}*/
		
		
		
		
	}
	
	private static void readFile(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = null;
		
		HashMap<String, Data> year = new HashMap<>();
		String[] values = null;
		while ((line = reader.readLine()) != null) {
			
			if (values != null) {
				String[] prevValues = values.clone();
				values = line.split(";");
				if (!prevValues[0].equals(values[0]) ) {
					HashMap<String, Data> newYear = new HashMap<String, Data>(year); // clone
					dataMap.put(Integer.parseInt(prevValues[0]), newYear);
					year = new HashMap<>();
				}
			}
				
			values = line.split(";");
			String[] valuesData = values.clone();
			
			Data data = new Data();
			data.setBirth(Integer.parseInt(valuesData[2]), Integer.parseInt(valuesData[3]), Integer.parseInt(valuesData[4]));
			year.put(valuesData[1], data);
		}
		dataMap.put(Integer.parseInt(values[0]), year);
		reader.close();
		
		reader = new BufferedReader(new FileReader(path));
		line = null;
		
		year = new HashMap<>();
		values = null;
		while ((line = reader.readLine()) != null) {
			String[] prevValues = values;
			values = line.split(";");
			
			if (prevValues != null && (!prevValues[0].equals(values[0]))) {
				dataMap.put(Integer.parseInt(prevValues[0]), year);
			}
			year = dataMap.get(Integer.parseInt(values[0]));

			Data birthData = year.get(values[1]);
			birthData.setDeath(Integer.parseInt(values[2]), Integer.parseInt(values[3]), Integer.parseInt(values[4]));
			year.put(values[1], birthData);
			
		}
		reader.close();
		
		for (int key : dataMap.keySet()) {
			HashMap<String, Data> map = dataMap.get(key);
			System.out.println("Jahr: " + key);
			
			for (String month : map.keySet()){
				Data data = map.get(month);
				System.out.println("Monat: " + month + ": " + data.print());
				
			}
		}

	}

	@Override
	public void paint(Graphics g) {
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.clearRect(0, 0, getWidth(), getHeight());
//
//		g2D.setColor(Color.RED);
//		g2D.fill(dataPoint);


		// durch daten gehen
		
		
		Point2D.Double center = new Point2D.Double(250, 250);
		double stepNumber = 400;
		double radius = 200;
		drawCircle(center, radius, stepNumber, g2D);
		
		// marker rectangle
		g2D.setColor(Color.GREEN);
		g2D.draw(markerRectangle);

	}

	public Point2D.Double rotatePoint(Point2D.Double point, Point2D.Double center, double angle) {
		angle *= Math.PI / 180;
		double x = center.getX() + (point.getX() -  center.getX()) * Math.cos(angle) - (point.getY() - center.getY()) * Math.sin(angle);
		double y = center.getY() + (point.getX() -  center.getX()) * Math.sin(angle) + (point.getY() - center.getY()) * Math.cos(angle);
		return new Point2D.Double(x, y);
	}
	
	public void drawCircle(Point2D.Double center, double radius, double stepNumber, Graphics2D g2D) {
		double angle = 360 / stepNumber;
		Point2D.Double point1;
		Point2D.Double point2 = new Point2D.Double(center.getX(), center.getY() - radius);
		point2 = rotatePoint(point2, center, -angle);
		
		for (int i = 0; i < stepNumber - 1; i++) {
			point1 = rotatePoint(point2, center, angle);
			point2 = rotatePoint(point1, center, -2 * angle);
			int[] xs = {(int) center.getX(), (int) point1.getX(), (int) point2.getX()};
			int[] ys = {(int) center.getY(), (int) point1.getY(), (int) point2.getY()};
			//System.out.println(xs[0] + " " + xs[1] + " " + xs[2] + " " + ys);
			Polygon poly = new Polygon(xs, ys, 3);
			g2D.drawPolygon(poly);
			g2D.fill(poly);
			//g2D.drawLine((int) center.getX(), (int) center.getY(), (int) point.getX(), (int) point.getY());
		}
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}


	public Rectangle2D getMarkerRectangle() {
		return markerRectangle;
	}



}
