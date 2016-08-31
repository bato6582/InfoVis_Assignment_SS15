package infovis.piechart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JPanel;

public class View extends JPanel {

	public Rectangle2D timeline_rectangle = new Rectangle2D.Double(0, 0, 0, 0);
	//private Rectangle2D markerRectangle = new Rectangle2D.Double(0, 0, 0, 0);

	private int width;
	private int height;	
	
	public int year = 2015;
	public boolean change_time = false;

	public int timeline_x_start = 50;
	public int timeline_x_end= 0;
	public int timeline_y = 0;
	public double pixel_per_year = 0;
	
	public int max_level = 5;
	public int level = 0;
	
	private static HashMap<Integer, HashMap<String, Data>> data_map = new HashMap<>();
	public HashMap< Integer, ArrayList<Segment>> segments = new HashMap<Integer, ArrayList<Segment>>();
	public ArrayList<String> selected_segments = new ArrayList<>();
	public String[] labels;
	public double[] percentages;
	
	private static String path_birth = "data/Geburten_Monat_Sex_1950-2015.csv";
	

	private static Data root = null;
	private static String current_tree_path = "root/";
	
	private static String[] months_ordered = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
	
	public void initialize() throws IOException {
		width = getWidth();
		height = getHeight();
		
		timeline_y = height - 50;
		pixel_per_year = (width - 100) / (double) (2015 - 1950 + 1);
		
		timeline_rectangle.setRect(50 + pixel_per_year * (year - 1950), timeline_y - pixel_per_year, pixel_per_year, 2 * pixel_per_year);
		
		readData();
		//printData();
	}
	

	private void printData() {
		for (int year: data_map.keySet()) {
			HashMap<String, Data> map = data_map.get(year);
			System.out.println("year: " + year);
			
			for (String key : map.keySet()){
				Data data = map.get(key);
				if (data != null) {
					System.out.println(data.print());	
				}				
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
			System.out.print("]");
		}	
	}

	private static void readData() throws IOException {
		// load years and create data (data will be filled recursively in Data)
		BufferedReader reader = new BufferedReader(new FileReader(path_birth));
		String line = null;
		
		int new_year = 0;
		int old_year = 0;
		
		while ((line = reader.readLine()) != null) {
			String data = line.split(";")[0];
			if (!data.equals("year")) {
				new_year = Integer.parseInt(data);
				if (new_year != old_year){
					HashMap<String, Data> map = new HashMap<>();
					map.put("birth", new Data(new_year, "birth"));
					map.put("death", new Data(new_year, "death"));
					data_map.put(new_year, map);
				}
				old_year = new_year;
			}
		}
		reader.close();
	}

	
	@Override
	public void paint(Graphics g) {
		boolean size_changed = (width != getWidth() || height != getHeight());
		width = getWidth();
		height = getHeight();

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);		
		g2D.clearRect(0, 0, width, height);

		timeline_x_end = width - timeline_x_start;
		timeline_y = height - 50;
		pixel_per_year = (width - 100) / (double)(2015 - 1950 + 1);
//		System.out.println(width);
//		System.out.println(height);
		
//
//		g2D.setColor(Color.RED);
//		g2D.fill(dataPoint);
		
		
		// ********** TIMELINE ********** //
		g2D.setColor(Color.BLACK);
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
			// TODO: Richtig, dass das alte Jahr verwendet wird???
			timeline_rectangle.setRect(50 + pixel_per_year * (year - 1950), timeline_y - pixel_per_year, pixel_per_year, 2 * pixel_per_year);
		}

		
		// ********** YEAR ********** //
		g2D.setColor(Color.BLACK);
		year = (int) (((timeline_rectangle.getX() - 50  + pixel_per_year * 0.5) / pixel_per_year + 1950));
		g2D.drawString("" + year, width - 50, 15);		
		
		
		String[] dirs = current_tree_path.split("/");

		// ********** TABLE ********** //
		g2D.setColor(Color.BLACK);
		int x_left_column = width - 240;
		int x_right_column = width - 140;
		g2D.drawLine(x_left_column, 40, width - 40, 40);
		g2D.drawLine(x_left_column, 40, width - 240, 40 + 15 * dirs.length);
		g2D.drawLine(width - 40, 40, width - 40, 40 + 15 * dirs.length);
		g2D.drawLine(width - 140, 40, x_right_column, 40 + 15 * dirs.length);
		
		
		// ********** RADIUS ********** //
		double radius = min(width, height) * 0.5 - 100;
		double max_radius = radius;
		double next_radius = radius;

		
		// ********** SEGMENTS ********** //
		System.out.println("Path: \"" + current_tree_path + "\"; Level: " + level);
		for (int i = dirs.length - 1; i >= 0; i--) {
			// draw segments that are bigger at first to avoid overlaying information
			String new_tree_path = "";
			for (int j = 0; j <= i; j++ ) {
				new_tree_path += dirs[j] +"/";
			}
			level = i;
			//System.out.println("level " + level);
			setPercentages(data_map.get(year), new_tree_path); //changes colors
			
//			printArray(labels);
//			printArray(percentages);
		
			Point2D.Double center = new Point2D.Double(width * 0.5, (height - 50) * 0.5);
			
			radius = next_radius;
			if(i == dirs.length - 1){
				next_radius -= 30 + max_radius/(dirs.length);
			} else {
				next_radius -= max_radius/(dirs.length);
			}
			
			//System.out.println("radius " + radius);
			try {
				g2D.setColor(Color.BLACK);
				Ellipse2D.Double circle = new Ellipse2D.Double(center.getX() - radius - 3, center.getY() - radius - 3, 2.0 * (radius + 3), 2.0 * (radius + 3));
				g2D.fill(circle);
			    g2D.draw(circle);
			    if (i == dirs.length - 1) {
			    	drawOuterData(center, radius, g2D, next_radius);
			    } else {
			    	drawInnerData(center, radius, g2D, next_radius);			    	
			    }
			
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Table lower edge per level and Strings
			g2D.setColor(Color.BLACK);
			g2D.drawLine(width - 40, 40 + 15 * (i + 1), x_left_column, 40 + 15 * (i + 1));
			if (i == 0) {
				g2D.drawString("Kategorie", x_left_column + 10, 38 + 15 * (i + 1));
				g2D.drawString("Wert", x_right_column + 10, 38 + 15 * (i + 1));
				
			} else {
				Segment seg = getSegmentOnLevel(i - 1, dirs[i]);
				g2D.drawString(dirs[i], x_left_column + 10, 38 + 15 * (i + 1));
				if (((i-1) % 2) == 0) { // not categoric
//					g2D.drawString("" + (Math.round(seg.percent * 1000) / 10.0) + "%", (int) seg.label_pos.getX(), (int) seg.label_pos.getY());	
					g2D.drawString("" + (Math.round(seg.percent * 1000) / 10.0) + "%", x_right_column + 10, 38 + 15 * (i + 1));	
				}				
//				Color c = seg.color;
//				int r = (int) (c.getRed() * 0.5);
//				int gr = (int) (c.getGreen() * 0.5);
//				int b = (int) (c.getBlue() * 0.5);
//				c = new Color (r, gr, b);
//				g2D.setColor(c);
//				g2D.fill(seg.poly);
//				g2D.drawPolygon(seg.poly);
				
			}
			
		}
		level = dirs.length - 1;
		
		g2D.setColor(Color.BLACK);
		g2D.fill(timeline_rectangle);
		g2D.draw(timeline_rectangle);
		
		// marker rectangle
//		g2D.setColor(Color.GREEN);
//		g2D.draw(markerRectangle);

	}

	

	
	private void setPercentages(HashMap<String, Data> map, String new_current_tree_path){
		//System.out.println("New Path: " + new_current_tree_path);
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
			percentages = new double[key_set.size()];
			
			for (int i = 0; i < key_set.size(); i++) {
				percentages[i] = 1.0 / key_set.size();
			}
			

			if (root == null) {
				labels = new String[] {"birth", "death"};
			} else {
				labels = root.getChildrenMap().keySet().toArray(new String[root.getChildrenMap().keySet().size()]);
			}
			
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
				
				percentages = new double[] {births, deaths};
				
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
				//System.out.println(key_set);
				percentages = new double[key_set.size()];
				
				int iterator = 0;
				for (String key : key_set) {
					double number = data.getValues().get(key);
					percentages[iterator] = number;
					sum += number;
					iterator++;
				}
				
				for (int i = 0; i < percentages.length; i++) {
					percentages[i] /= sum;
					
				}
				
				//printArray(percentages);
				
				labels = key_set.toArray(new String[key_set.size()]);
				
			}
		}
	}
	

	public Point2D.Double rotatePoint(Point2D.Double point, Point2D.Double center, double angle) {
		angle *= Math.PI / 180;
		double x = center.getX() + (point.getX() -  center.getX()) * Math.cos(angle) - (point.getY() - center.getY()) * Math.sin(angle);
		double y = center.getY() + (point.getX() -  center.getX()) * Math.sin(angle) + (point.getY() - center.getY()) * Math.cos(angle);
		return new Point2D.Double(x, y);
	}
	

	/*
	public void drawData(Point2D.Double center, double radius, double step_number, Graphics2D g2D, boolean categoric, double[] percentages, String[] labels, double prev_radius) throws IOException {
		ArrayList<Segment> segment_per_lvl = new ArrayList<Segment>();
		
		Color clr = new Color(255, 128, 0);
		int color_gradient = (3 * 255) / (percentages.length + 1);
		
		double selected_perc = 0;
		double unselected_perc = 0;
		for (int i = 0; i < percentages.length; i++) {
			if (i == current_tree_path.split("/").length - 1) {
				if (selected_segments.contains(labels[i])) {
					selected_perc += percentages[i];
				} else {
					unselected_perc += percentages[i];				
				}
			}
		}
		
		System.out.println("selected: " + selected_perc + " unselected: " + unselected_perc);
		double last_percentage = 0;
		for (int i = 0; i < percentages.length; i++) {
			clr = new Color( min((i + 1) * color_gradient, 255), min((int) (0.5 * (i + 1) * color_gradient), 255), min((int) (0.33 * (i + 1) * color_gradient), 255));
			double perc;
			if (selected_segments.size() == 0 || i != (current_tree_path.split("/").length - 1) ) {
				perc = percentages[i];				
			} else if (selected_segments.contains(labels[i])) {
				perc = last_percentage * 0.9 / selected_perc;				
			} else {
				perc = last_percentage * 0.1 / unselected_perc;
			}
			
			segment_per_lvl.add(new Segment(labels[i], root, clr, categoric, perc));
			
			double angle = -360 * perc;
			double pos_angle = -360 * last_percentage;
			
			Point2D.Double start_pos = new Point2D.Double(center.getX(), center.getY() - radius);
			start_pos = Segment.rotatePoint(start_pos, center, pos_angle);
			Point2D.Double end_pos = Segment.rotatePoint(start_pos, center, (percentages[i] * -360));
			segment_per_lvl.get(i).createPolygon(center, start_pos, end_pos, radius, angle, labels.length, prev_radius);
			
			last_percentage += perc;
		}
		
	
		segments.put(level, segment_per_lvl);

		String[] dirs = current_tree_path.split("/");
		
		for (Segment s : segments.get(level)) {
			if(level < dirs.length - 1){
				// ************* Change Color for marking the chosen path **************
				if (s.label.equals(dirs[level + 1])){
//					s.color = (new Color( (int) (s.color.getRed() * 0.5), (int) (s.color.getGreen() * 0.5), (int) (s.color.getBlue() * 0.5)));
//					s.color = Color.GREEN;
					s.color = new Color (0,0,102);
					
				}else{
					s.color = new Color(s.color.getRed(), s.color.getRed(), s.color.getRed() , (int) (s.color.getAlpha() * 0.5));
				}
//				System.out.println("Label: " + s.label + "  Path: " + dirs[level]);			
			}
			g2D.setColor(s.color);
			g2D.fill(s.poly);
			g2D.drawPolygon(s.poly);
		}
		
		
		for (Segment s : segments.get(level)) {
			//from http://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color, 23.08.2016, 15:40 answer from User Mark Ransom
			double fac = 1 / 255.0;
			double r = s.color.getRed() * fac;
			double g = s.color.getGreen() * fac;
			double b = s.color.getBlue() * fac;
			r = r <= 0.03928 ? r/12.92 : Math.pow((r + 0.055)/1.055, 2.4);
			g = g <= 0.03928 ? g/12.92 : Math.pow((g + 0.055)/1.055, 2.4);
			b = b <= 0.03928 ? b/12.92 : Math.pow((b + 0.055)/1.055, 2.4);
			double l = 0.2126 * r + 0.7152 * g + 0.0722 * b;
			g2D.setColor(l > 0.179 ? Color.BLACK : Color.WHITE);
			
			
			
//			g2D.setColor(new Color(Math.abs(255 - (int) (s.color.getRed() * 1.5)), Math.abs(255 - (int) (s.color.getGreen() * 1.5)), Math.abs(255 - (int) (s.color.getBlue() * 1.5))));
			String string = s.label;
			int left_or_right = center.getX() - s.label_pos.getX() > 0 ? 1 : -1;
			g2D.drawString(string, (int) (s.label_pos.getX() - string.length() * 0.5 * 8), (int) (s.label_pos.getY() /*+ string.length() * 4*//*));
			string = Math.round(s.percent*1000) / 10.0 + " %";
			g2D.drawString(string, (int) (s.label_pos.getX() - string.length() * 0.5 * 8), (int) (s.label_pos.getY() + 13));
			g2D.setColor(s.color);
		}
	}
	*/
	
	
	public void drawInnerData(Point2D.Double center, double radius, Graphics2D g2D, double prev_radius) throws IOException {
		boolean categoric = (level % 2) == 0 ? false : true;
		ArrayList<Segment> segment_per_lvl = new ArrayList<Segment>();
		
		Color clr = new Color(255, 128, 0);
		int color_gradient = (3 * 255) / (percentages.length + 1);
		
		double last_percentage = 0;
		for (int i = 0; i < percentages.length; i++) {
			clr = new Color( min((i + 1) * color_gradient, 255), min((int) (0.5 * (i + 1) * color_gradient), 255), min((int) (0.33 * (i + 1) * color_gradient), 255));
			segment_per_lvl.add(new Segment(labels[i], root, clr, categoric, percentages[i]));
			
			double angle = -360 * percentages[i];
			double pos_angle = -360 * last_percentage;
			
			Point2D.Double start_pos = new Point2D.Double(center.getX(), center.getY() - radius);
			start_pos = Segment.rotatePoint(start_pos, center, pos_angle);
			Point2D.Double end_pos = Segment.rotatePoint(start_pos, center, angle);
			segment_per_lvl.get(i).createPolygon(center, start_pos, end_pos, radius, angle, labels.length, prev_radius);
			
			last_percentage += percentages[i];
		}
		
		segments.put(level, segment_per_lvl);

		String[] dirs = current_tree_path.split("/");
		
		for (Segment s : segments.get(level)) {
			if (level < dirs.length - 1) {
				// ************* Change Color for marking the chosen path **************
				if (s.label.equals(dirs[level + 1])){
//					s.color = (new Color( (int) (s.color.getRed() * 0.5), (int) (s.color.getGreen() * 0.5), (int) (s.color.getBlue() * 0.5)));
//					s.color = Color.GREEN;
					s.color = new Color (0,0,102);
					
				} else {
					s.color = new Color(s.color.getRed(), s.color.getRed(), s.color.getRed() , (int) (s.color.getAlpha() * 0.5));
				}
//				System.out.println("Label: " + s.label + "  Path: " + dirs[level]);			
			}
			g2D.setColor(s.color);
			g2D.fill(s.poly);
			g2D.drawPolygon(s.poly);
		}
		
		
		for (Segment s : segments.get(level)) {
			//from http://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color, 23.08.2016, 15:40 answer from User Mark Ransom
			double fac = 1 / 255.0;
			double r = s.color.getRed() * fac;
			double g = s.color.getGreen() * fac;
			double b = s.color.getBlue() * fac;
			r = r <= 0.03928 ? r/12.92 : Math.pow((r + 0.055)/1.055, 2.4);
			g = g <= 0.03928 ? g/12.92 : Math.pow((g + 0.055)/1.055, 2.4);
			b = b <= 0.03928 ? b/12.92 : Math.pow((b + 0.055)/1.055, 2.4);
			double l = 0.2126 * r + 0.7152 * g + 0.0722 * b;
			g2D.setColor(l > 0.179 ? Color.BLACK : Color.WHITE);
			
			
			
//			g2D.setColor(new Color(Math.abs(255 - (int) (s.color.getRed() * 1.5)), Math.abs(255 - (int) (s.color.getGreen() * 1.5)), Math.abs(255 - (int) (s.color.getBlue() * 1.5))));
			String string = s.label;
			int left_or_right = center.getX() - s.label_pos.getX() > 0 ? 1 : -1;
			g2D.drawString(string, (int) (s.label_pos.getX() - string.length() * 0.5 * 8), (int) (s.label_pos.getY() /*+ string.length() * 4*/));
			string = Math.round(s.percent*1000) / 10.0 + " %";
			g2D.drawString(string, (int) (s.label_pos.getX() - string.length() * 0.5 * 8), (int) (s.label_pos.getY() + 13));
			g2D.setColor(s.color);
		}
	}
	
	
	public void drawOuterData(Point2D.Double center, double radius, Graphics2D g2D, double prev_radius) throws IOException {
		boolean categoric = (level % 2) == 0 ? false : true;
		ArrayList<Segment> segment_per_lvl = new ArrayList<Segment>();
		
		Color clr = new Color(255, 128, 0);
		int color_gradient = (3 * 255) / (percentages.length + 1);

		double selected_perc = 0;
		ArrayList<Integer> selected_index_list = new ArrayList<>();
		double unselected_perc = 0;
		ArrayList<Integer> unselected_index_list = new ArrayList<>();
		
		for (int i = 0; i < percentages.length; i++) {
			if (selected_segments.contains(labels[i])) {
				selected_index_list.add(i);
				selected_perc += percentages[i];
			} else {
				unselected_index_list.add(i);
				unselected_perc += percentages[i];
			}
		}
		
		double pos = 0;
		for (int i : selected_index_list) {
			double angle = -360 * percentages[i] / selected_perc;
			if (unselected_index_list.size() != 0) {				
				angle = 360 * -(300.0/360.0) * percentages[i] / selected_perc;
			}

			//System.out.println("Angle: " + angle);
			Point2D.Double start_pos = new Point2D.Double(center.getX(), center.getY() - radius);
			start_pos = Segment.rotatePoint(start_pos, center, pos);
			Point2D.Double end_pos = Segment.rotatePoint(start_pos, center, angle);
			clr = new Color( min((i + 1) * color_gradient, 255), min((int) (0.5 * (i + 1) * color_gradient), 255), min((int) (0.33 * (i + 1) * color_gradient), 255));
			
			Segment segment = new Segment(labels[i], root, clr, categoric, -angle/(300.0/360.0));
			segment.createPolygon(center, start_pos, end_pos, radius, angle, labels.length, prev_radius);
			
			segment_per_lvl.add(segment);
			pos += angle;
			
		}
		
	
		for (int i : unselected_index_list) {
			double angle = -360 * percentages[i];
			if (selected_index_list.size() != 0) {
				angle = 360 * -(60.0/360.0) * percentages[i] / unselected_perc;				
			}
			//System.out.println(angle);
			Point2D.Double start_pos = new Point2D.Double(center.getX(), center.getY() - radius);
			start_pos = Segment.rotatePoint(start_pos, center, pos);
			Point2D.Double end_pos = Segment.rotatePoint(start_pos, center, angle);
			clr = new Color( min((i + 1) * color_gradient, 255), min((int) (0.5 * (i + 1) * color_gradient), 255), min((int) (0.33 * (i + 1) * color_gradient), 255));
			
			Segment segment = new Segment(labels[i], root, clr, categoric, percentages[i]);
			segment.createPolygon(center, start_pos, end_pos, radius, angle, labels.length, prev_radius);
			
			segment_per_lvl	.add(segment);
			pos += angle;
		}
		
	
		segments.put(level, segment_per_lvl);
		System.out.println(level);
		
		for (Segment s : segment_per_lvl) {
			g2D.setColor(s.color);
			g2D.fill(s.poly);
			g2D.drawPolygon(s.poly);
		}
		
		
		for (Segment s : segments.get(level)) {
			//from http://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color, 23.08.2016, 15:40 answer from User Mark Ransom
			double fac = 1 / 255.0;
			double r = s.color.getRed() * fac;
			double g = s.color.getGreen() * fac;
			double b = s.color.getBlue() * fac;
			r = r <= 0.03928 ? r/12.92 : Math.pow((r + 0.055)/1.055, 2.4);
			g = g <= 0.03928 ? g/12.92 : Math.pow((g + 0.055)/1.055, 2.4);
			b = b <= 0.03928 ? b/12.92 : Math.pow((b + 0.055)/1.055, 2.4);
			double l = 0.2126 * r + 0.7152 * g + 0.0722 * b;
			g2D.setColor(l > 0.179 ? Color.BLACK : Color.WHITE);
			
			
			
//			g2D.setColor(new Color(Math.abs(255 - (int) (s.color.getRed() * 1.5)), Math.abs(255 - (int) (s.color.getGreen() * 1.5)), Math.abs(255 - (int) (s.color.getBlue() * 1.5))));
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
		//System.out.println("PATH before: " + current_tree_path + "		LEVEL before: " + level);
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
		
		//System.out.println("PATH after: " + current_tree_path + "		LEVEL after: " + level);
		System.out.println();
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


	//public Rectangle2D getMarkerRectangle() {
	//	return markerRectangle;
	//}
	




}
