package infovis.piechart;

import sun.nio.ch.SelChImpl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import javax.swing.JPanel;

public class View extends JPanel {


	public Rectangle2D timeline_rectangle = new Rectangle2D.Double(0, 0, 0, 0);
	private Rectangle2D markerRectangle = new Rectangle2D.Double(0, 0, 0, 0);

	public Rectangle2D.Double selectedRectangle;

	private int width;
	private int height;
	
	
	public boolean change_time = false;
	
	public int year = 2015;

	
	public boolean new_category = false;
	public String[] labels;
	

	public double xOffset = 0.0;
	public double oldXOffset = 0.0;
	public String draggedLabel = "";
	public boolean dragLabel = false;

	public int timeline_x_start = 50;
	public int timeline_x_end= 0;
	public int timeline_y = 0;
	public double pixel_per_year = 0;
	
	public int max_level = 5;
	public int level = 0;
	
	//<level, segments>
	public HashMap< Integer, ArrayList<Segment>> segments = new HashMap<Integer, ArrayList<Segment>>();
	
	private static String path_birth = "data/Geburten_Monat_Sex_1950-2015.csv";
	private static String path_death = "data/Tode_Monat_Sex_1950-2015.csv";
	private static HashMap<Integer, HashMap<String, Data>> data_map = new HashMap<>();
	
	private static Data root = null;

	private static String current_tree_path = "root/";
	
	
	private static String[] months_ordered = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
	
	public void initialize() throws IOException {
		readFile();
		
		
		width = getWidth();
		height = getHeight();
		timeline_y = height - 50;
		
		
		int timeline_y = height - 50;
		pixel_per_year = (width - 100) / (double) (2015 - 1950 + 1);
		timeline_rectangle.setRect(50 + pixel_per_year * (year - 1950), timeline_y - pixel_per_year, pixel_per_year, 2 * pixel_per_year);

		
		
		
	}
	

	private void printData() {
//		for (int key = 1950; key < 2016; key++) {
		for (int key: data_map.keySet()) {
			HashMap<String, Data> map = data_map.get(key);
			System.out.println("year: " + key);
			
			for (String month : months_ordered){
//			for (String month : map.keySet()){
				//TODO: Catch if month missing
				Data data = map.get(month);
//				System.out.println("Monat: " + month + ": " + data.print());
				
			}
		}
	}
	
	
	private void printArray(double[] percentages) {
		System.out.print("[");
		for (double number : percentages) {
			System.out.print(number + ", ");
		}
		System.out.println("]");
	}
	
	private void printArray(String[] percentages) {
		if (percentages != null) {
			
			System.out.print("[");
			for (String number : percentages) {
				System.out.print(number + ", ");
			}
			System.out.println("]");
		}	
	}

	private static void readFile() throws IOException {
				
		// load birth data
		BufferedReader reader = new BufferedReader(new FileReader(path_birth));
		String line = null;
		
		int year_new = 0;
		int old_year = 0;
		while ((line = reader.readLine()) != null) {
			String[] words = line.split(";");
			if (words[0].equals("year")) {
				//
			} else {
				year_new = Integer.parseInt(words[0]);
				if(year_new != old_year){
					HashMap<String, Data> map = new HashMap<>();
					Data birth = new Data(year_new, "birth");
					map.put("birth", birth);
					Data death = new Data(year_new, "death");
					map.put("death", death);
					data_map.put(year_new,map);
				}
				old_year = year_new;
			}
		}

		reader.close();

	}

	@Override
	public void paint(Graphics g) {

		timeline_x_end = width - timeline_x_start;

		boolean size_changed = (width != getWidth() || height != getHeight());
		width = getWidth();
		height = getHeight();
		timeline_y = height - 50;
		pixel_per_year = (width - 100) / (double)(2015 - 1950 + 1);
		System.out.println((width - 100) +  " " + pixel_per_year);

		
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2D.clearRect(0, 0, width, height);
//
//		g2D.setColor(Color.RED);
//		g2D.fill(dataPoint);
		
		// timeline
		g2D.setColor(Color.BLACK);
//		System.out.println(width);
//		System.out.println(height);
		g2D.drawLine(50, timeline_y, width - 50, timeline_y);
		for (int i = 1950; i < 2017; i++) {
			int x = (int) ( 50 + pixel_per_year * (i - 1950) );
			if(i % 5 == 0){
				g2D.drawLine(x, timeline_y - 10, x, timeline_y + 10);
				g2D.drawString("" + i, (int) (timeline_x_start + (i - 1950) * pixel_per_year - 2 * 7), timeline_y + 22);		
			} else {
				g2D.drawLine(x, timeline_y - 5, x, timeline_y + 5);				
			}
		}
		

		
		if (size_changed) {
			timeline_rectangle.setRect(50 + pixel_per_year * (year - 1950), timeline_y - pixel_per_year, pixel_per_year, 2 * pixel_per_year);

		}
		

		// durch daten gehen
		year = (int) (((timeline_rectangle.getX() - 50  + pixel_per_year * 0.5) / pixel_per_year + 1950));
		g2D.drawString("" + year, width - 50, 15);
//		System.out.println("year: " + year);
		
		
		// DATA ****************
		String[] dirs = current_tree_path.split("/");
		double radius = min(width, height) * 0.5 - 100;
		double max_radius = radius;
		
		//Table
		int x_left_column = width - 240;
		int x_right_column = width - 140;
		g2D.drawLine(x_left_column, 40, width - 40, 40);
		g2D.drawLine(x_left_column, 40, width - 240, 40 + 15 * dirs.length);
		g2D.drawLine(width - 40, 40, width - 40, 40 + 15 * dirs.length);
		g2D.drawLine(width - 140, 40, x_right_column, 40 + 15 * dirs.length);
		
		

		double next_radius = radius;
		

		System.out.println("path " + current_tree_path);

		for (int i = dirs.length - 1; i >= 0; i--) {
			
			
			String new_tree_path = "";
			for (int j = 0; j <= i; j++ ) {
				new_tree_path += dirs[j] +"/";
			}
			level = i;
			System.out.println("level " + level);
			double[] percentages = getPercentages(data_map.get(year), new_tree_path); //changes colors
			
			if (i == 0) {
				System.out.println("Treepath: " + new_tree_path);
				
			}
			System.out.println("Labels: ");
			printArray(labels);

			
			printArray(percentages);
		
			
			Point2D.Double center = new Point2D.Double(width * 0.5, (height - 50) * 0.5);
			double stepNumber = 360;
			int fac = dirs.length - 1 - i;

			
			radius = next_radius;
			if(i == dirs.length - 1){
				next_radius -= 30 + max_radius/(dirs.length);
			} else {
				next_radius -= max_radius/(dirs.length);
			}
			
			System.out.println("radius " + radius);
			try {
				boolean categoric = (level % 2) == 0 ? false : true;
				g2D.setColor(Color.BLACK);
				Ellipse2D.Double circle = new Ellipse2D.Double(center.getX() - radius - 3, center.getY() - radius - 3, 2.0 * (radius + 3), 2.0 * (radius + 3));
				g2D.fill(circle);
			    g2D.draw(circle);
				drawData(center, radius, stepNumber, g2D, categoric, percentages, labels, next_radius);
			
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Table lover edge per level and Strings
			g2D.setColor(Color.BLACK);
			g2D.drawLine(width - 40, 40 + 15 * (i + 1), x_left_column, 40 + 15 * (i + 1));
			if (i == 0) {
				g2D.drawString("Kategorie", x_left_column + 10, 38 + 15 * (i + 1));
				g2D.drawString("Wert", x_right_column + 10, 38 + 15 * (i + 1));
				
			} else {
				g2D.drawString(dirs[i], x_left_column + 10, 38 + 15 * (i + 1));
				g2D.drawString("" + (Math.round(getSegmentOnLevel(i - 1, dirs[i]).percent * 1000) / 10.0) + "%", x_right_column + 10, 38 + 15 * (i + 1));
				
				
			}
			
		}
		level = dirs.length - 1;
		System.out.println("HIERRRRRR " + level);
		
		g2D.setColor(Color.BLACK);
		g2D.fill(timeline_rectangle);
		g2D.draw(timeline_rectangle);
		
		// marker rectangle
		g2D.setColor(Color.GREEN);
		g2D.draw(markerRectangle);

	}

	

	
	private double[] getPercentages(HashMap<String, Data> map, String new_current_tree_path){
		System.out.println("New Path: " + new_current_tree_path);
		if (level % 2 == 1) {
			String[] keys = new_current_tree_path.split("/");
			
			HashMap<String, Data> m = new HashMap<String, Data>(data_map.get(year)); // von dataMap
			Data data = null;
			
			for (String key : keys) {
				if (m.get(key) != null) {
					data = m.get(key);
					m = data.getChildrenMap();
				}
			}
			
			root = data;
			
			Set<String> key_set = data.getChildrenMap().keySet();
			double[] percent = new double[key_set.size()];
			
			for (int i = 0; i < key_set.size(); i++) {
				percent[i] = 1.0 / key_set.size();
			}
			

			if (root == null) {
				labels = new String[] {"birth", "death"};
			} else {
				labels = root.getChildrenMap().keySet().toArray(new String[root.getChildrenMap().keySet().size()]);
			}
			
			return percent;
			
		} else {
		
			if (new_current_tree_path.equals("root/")){
				double births = map.get("birth").getValues().get("birth");
				double deaths = map.get("death").getValues().get("death");
	//			System.out.print("births: " + births);			
	//			System.out.print("deaths: " + deaths);			
				
				double num = deaths + births;
				deaths /= num;
				births /= num;
	//			root = dataMap.get(year).get
				
				labels = new String[] {"birth", "death"};
				
				return new double[] {births, deaths};
				
			} else {
				// get current Data
				String[] keys = new_current_tree_path.split("/");
				
				HashMap<String, Data> m = new HashMap<String, Data>(data_map.get(year)); // von dataMap
				Data data = null;
				
				// key != key of m, key is part of treepath
				for (String key : keys) {
					if (m.get(key) != null) {
						data = m.get(key);
						m = data.getChildrenMap();
					}
				}
				
		
				root = data;
				
				// get Percentages
				double sum = 0.0;
				Set<String> key_set = data.getValues().keySet();
				System.out.println(key_set);
				double[] numbers = new double[key_set.size()];
				
				int iterator = 0;
				for (String key : key_set) {
					double number = data.getValues().get(key);
					numbers[iterator] = number;
					sum += number;
					iterator++;
				}
				
				for (int i = 0; i < numbers.length; i++) {
					numbers[i] /= sum;
					
				}
				
				printArray(numbers);
				
				labels = key_set.toArray(new String[key_set.size()]);
				
				return numbers;
			}
		}

	}
	

	public Point2D.Double rotatePoint(Point2D.Double point, Point2D.Double center, double angle) {
		angle *= Math.PI / 180;
		double x = center.getX() + (point.getX() -  center.getX()) * Math.cos(angle) - (point.getY() - center.getY()) * Math.sin(angle);
		double y = center.getY() + (point.getX() -  center.getX()) * Math.sin(angle) + (point.getY() - center.getY()) * Math.cos(angle);
		return new Point2D.Double(x, y);
	}
	

	
	public void drawData(Point2D.Double center, double radius, double step_number, Graphics2D g2D, boolean categoric, double[] percentages, String[] labels, double prev_radius) throws IOException {


		ArrayList<Segment> tmp = new ArrayList<Segment>();
		Color clr = new Color(255, 128, 0);
		
		// 765 = 3 * 255
		int color_gradient = (3 * 255) / (percentages.length + 1);
//		System.out.println("Color: " + color_gradient);
		
		double last_percentage = 0;
		for (int i = 0; i < percentages.length; i++) {
			clr = new Color( min((i + 1) * color_gradient, 255), min((int) (0.5 * (i + 1) * color_gradient), 255), min((int) (0.33 * (i + 1) * color_gradient), 255));
			tmp.add(new Segment(labels[i], root, clr, categoric, percentages[i]));
			double angle = -360 * last_percentage;
			tmp.get(i).createPolygon(center, radius, step_number, angle, labels.length, prev_radius);
			last_percentage += percentages[i];
		}
		
	

		segments.put(level, tmp);

		for (Segment s : segments.get(level)) {
			g2D.setColor(s.color);
			g2D.fill(s.poly);
			g2D.drawPolygon(s.poly);
		}
		
		for (Segment s : segments.get(level)) {
			g2D.setColor(new Color(255 - s.color.getRed(), 255 - s.color.getGreen(), 255 - s.color.getBlue()));
			String string = s.label;
			int left_or_right = center.getX() - s.label_pos.getX() > 0 ? 1 : -1;
			g2D.drawString(string, (int) (s.label_pos.getX() - string.length() * 0.5 * 8), (int) (s.label_pos.getY() /*+ string.length() * 4*/));
			string = Math.round(s.percent*1000) / 10.0 + " %";
			g2D.drawString(string, (int) (s.label_pos.getX() - string.length() * 0.5 * 8), (int) (s.label_pos.getY() + 13));
			g2D.setColor(s.color);
		}
	}
	
	private int min(int i, int j) {
		return (i < j) ? i : j;
	}

	public void clicked(String label, int lvl) {
		System.out.println("									PATH before: " + current_tree_path + "		LEVEL before: " + level);
		String[] dirs = current_tree_path.split("/");
		
		if (lvl == dirs.length - 1) { // new level
			if (lvl < max_level - 1) {
				current_tree_path += label + "/";
				level++;				
			}
		} else { // jump back to chosen former level
			current_tree_path = "";
			for (int i = 0; i <= lvl; i++) {
				current_tree_path += dirs[i] + "/";
			}
			level = lvl;
		}
		
		System.out.println("									PATH after: " + current_tree_path + "		LEVEL after: " + level);
	}
	
	
	public Segment getSegmentOnLevel (int lvl, String label) {
		for (Segment seg : segments.get(lvl)) {
			if (seg.label.equals(label)) {
				return seg;				
			}
			
		}
		
		System.out.println("Failed to find segment");
		System.exit(0);
		return new Segment();
	}
	
	

	@Override
	public void update(Graphics g) {
		paint(g);
	}


	public Rectangle2D getMarkerRectangle() {
		return markerRectangle;
	}



}
