package infovis.piechart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.swing.JPanel;

public class View extends JPanel {

	public Rectangle2D timeline_rectangle = new Rectangle2D.Double(0, 0, 0, 0);

	private int width;
	private int height;	
	
	public int year = 2015;
	public boolean change_time = false;
	public boolean selection_chosen = false;
	public boolean ctrl_pressed = false;
	public boolean shift_pressed = false;
	private boolean categoric = false;

	public int timeline_x_start = 50;
	public int timeline_x_end= 0;
	public int timeline_y = 0;
	public double pixel_per_year = 0;
	
	public int max_level = 5;
	public int level = 0;
	
	private static HashMap<Integer, HashMap<String, Data>> data_map = new LinkedHashMap<>();
	public HashMap< Integer, ArrayList<Segment>> segments = new HashMap<Integer, ArrayList<Segment>>();
	public ArrayList<String> selected_segments = new ArrayList<>();
	public String[] labels;
	public double[] percentages;
	
	private static String path_birth = "data/Geburten_Monat_Sex_1950-2015.csv";
	

	private static Data root = null;
	private static String current_tree_path = "root/";
	
	public void initialize() throws IOException, ClassNotFoundException {
		width = getWidth();
		height = getHeight();
		
		timeline_y = height - 50;
		pixel_per_year = (width - 100) / (double) (2015 - 1950 + 1);
		
		timeline_rectangle.setRect(50 + pixel_per_year * (year - 1950), timeline_y - 10, pixel_per_year, 20);
		
		// check if datamap file exists:
		String datamap_path = "data/datamap.ser";
		File f = new File(datamap_path);
		if(f.exists()) {
			// read
		    InputStream buffer = new BufferedInputStream(new FileInputStream(datamap_path));
		    ObjectInput input = new ObjectInputStream (buffer);
		    data_map = new LinkedHashMap<>((HashMap<Integer, HashMap<String, Data>>) input.readObject());
		    //System.out.println(((HashMap<Integer, String>) input.readObject()).get(2015));
		} else {
			// save
			readData();
			DataMap serialize_object = new DataMap();
			serialize_object.data_map = new LinkedHashMap<>(data_map);
			//serialize_object.data_map = new HashMap<>(data_map);
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(datamap_path));
			os.writeObject(serialize_object.data_map);
			os.close();
		}
		
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

	// load years and create data (data will be filled recursively in Data)
	private static void readData() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path_birth));
		String line = null;
		
		int new_year = 0;
		int old_year = 0;
		
		while ((line = reader.readLine()) != null) {
			String data = line.split(";")[0];
			if (!data.equals("year")) {
				new_year = Integer.parseInt(data);
				if (new_year != old_year){
					HashMap<String, Data> map = new LinkedHashMap<>();
					map.put("birth", new Data(new_year, "birth"));
					map.put("death", new Data(new_year, "death"));
					data_map.put(new_year, map);
				}
				old_year = new_year;
			}
		}
		reader.close();
	}

	
	// paint method will be called whenever there is an event of the mouse controller or the keyboard controller
	@Override
	public void paint(Graphics g) {
		boolean size_changed = (width != getWidth() || height != getHeight());
		width = getWidth();
		height = getHeight();

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);		
		g2D.clearRect(0, 0, width, height);

		
		
		// ********** TIMELINE ********** //
		timeline_x_end = width - timeline_x_start;
		timeline_y = height - 50;
		pixel_per_year = (width - 100) / (double)(2015 - 1950 + 1);
		
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
		
		
		// ********** TIMELINE RECTANGLE ********** //
		if (size_changed) {
			timeline_rectangle.setRect(50 + pixel_per_year * (year - 1950), timeline_y - 10, pixel_per_year, 20);
		}
		g2D.setColor(Color.BLACK);
		g2D.fill(timeline_rectangle);
		g2D.draw(timeline_rectangle);

		
		// ********** YEAR ********** //
		year = (int) (((timeline_rectangle.getX() - 50  + pixel_per_year * 0.5) / pixel_per_year + 1950));
		g2D.setColor(Color.BLACK);
		g2D.drawString("" + year, width - 50, 15);		
		
		

		// ********** TABLE ********** //
		String[] dirs = current_tree_path.split("/");
		
		int x_left_column = width - 240;
		int x_right_column = width - 140;
		g2D.setColor(Color.BLACK);
		g2D.drawLine(x_left_column, 40, width - 40, 40);
		g2D.drawLine(x_left_column, 40, width - 240, 40 + 15 * dirs.length);
		g2D.drawLine(width - 40, 40, width - 40, 40 + 15 * dirs.length);
		g2D.drawLine(width - 140, 40, x_right_column, 40 + 15 * dirs.length);
		
		
		// ********** RADIUS ********** //
		double radius = min(width, height) * 0.5 - 100;
		double max_radius = radius;
		double next_radius = radius;

		
		// ********** SEGMENTS ********** //
		for (int i = dirs.length - 1; i >= 0; i--) {
			
			// draw the levels after each other, begin with the outer one to avoid overlaying
			String new_tree_path = "";
			for (int j = 0; j <= i; j++ ) {
				new_tree_path += dirs[j] +"/";
			}
			level = i;
			categoric = (level % 2 == 1);
			
			setPercentages(data_map.get(year), new_tree_path);
		
			Point2D.Double center = new Point2D.Double(width * 0.5, (height - 50) * 0.5);
			
			radius = next_radius;
			if (i == dirs.length - 1) {
				next_radius -= 30 + max_radius/(dirs.length);
			} else {
				next_radius -= max_radius/(dirs.length);
			}

			try {
				Ellipse2D.Double circle; 
				int border_offset = 10;
				if (i != dirs.length - 1) {
					circle = new Ellipse2D.Double(center.getX() - radius - border_offset, center.getY() - radius - border_offset, 2.0 * (radius + border_offset), 2.0 * (radius + border_offset));
					g2D.setColor(Color.BLACK);
					g2D.fill(circle);
				    g2D.draw(circle);
					// ********** SEPARATION CIRCLE ********** //
					int separation_offset = 8;
					circle = new Ellipse2D.Double(center.getX() - radius - separation_offset, center.getY() - radius - separation_offset, 2.0 * (radius + separation_offset), 2.0 * (radius + separation_offset));
					g2D.setColor(Color.WHITE);
					g2D.fill(circle);
				    g2D.draw(circle);
				}
				// ********** OUTER SEPARATION CIRCLE ********** //
				border_offset = 2;
				circle = new Ellipse2D.Double(center.getX() - radius - border_offset, center.getY() - radius - border_offset, 2.0 * (radius + border_offset), 2.0 * (radius + border_offset));
				g2D.setColor(Color.BLACK);
				g2D.fill(circle);
			    g2D.draw(circle);
			    if (i == dirs.length - 1) {
			    	drawOuterData(center, radius, g2D, next_radius);
			    } else {
			    	drawInnerData(center, radius, g2D, next_radius);			    	
			    }
			    for (Segment s : segments.get(level)) {
					g2D.setColor(s.color);
					g2D.fill(s.poly);
					g2D.drawPolygon(s.poly);
				}
			    drawLabels(g2D);
			
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// ********** TABLE CONTENT ********** //
			// Table lower edge per level and Strings
			g2D.setColor(Color.BLACK);
			g2D.drawLine(width - 40, 40 + 15 * (i + 1), x_left_column, 40 + 15 * (i + 1));
			if (i == 0) {
				g2D.drawString("Kategorie", x_left_column + 10, 38 + 15 * (i + 1));
				g2D.drawString("Wert", x_right_column + 10, 38 + 15 * (i + 1));
				
			} else {
				// fill table with content
				Segment seg = getSegmentOnLevel(i - 1, dirs[i]);
				g2D.drawString(dirs[i], x_left_column + 10, 38 + 15 * (i + 1));
				if (((i-1) % 2) == 0) {
					// only values that are not categoric
					g2D.drawString("" + (Math.round(seg.percent * 1000) / 10.0) + "%", x_right_column + 10, 38 + 15 * (i + 1));	
				}
			}
			
			
		}
		level = dirs.length - 1;
		categoric = (level % 2 == 1);
		
		

		// ********** DIAGRAM ********** //
		HashMap<String, double[]> category_numbers = new HashMap<String, double[]>();
		
		if (!categoric) {
			drawDiagramData(g2D, category_numbers, max_radius);
			
		} else {
			category_numbers.clear();
			// do nothing
			
		}
	}

	

	
	private void drawDiagramData(Graphics2D g2D, HashMap<String, double[]> category_numbers, double max_radius) {
		//stores values for all 65 years
		
		// ********** CALCULATE DATA FOR ALL YEARS ********** //
		double[] numbers_birth = new double[65];
		double[] numbers_death = new double[65];

		HashMap<String, Data> map = new HashMap<String, Data>();
		Data data;
		
		String[] keys = current_tree_path.split("/");
		// checks if data may be from a set, where we do not have data for every year
		// in this case we need to get data from a year, where we have data (e.g. 2012)
		if (keys[keys.length - 1].equals("age mother") || keys[keys.length - 1].equals("age")) {
			int old_year = year;
			year = 2012;
			map  = new HashMap<String, Data>(data_map.get(year));
			data = getRootData(current_tree_path);
			year = old_year;
		} else {
			map  = new HashMap<String, Data>(data_map.get(year));			
			data = getRootData(current_tree_path);
		}
			
			
		
		
		if (data != null) {
			
			
			Set<String> key_set = data.getValues().keySet();
			for (String key : key_set) {
				double[] numbers = new double[65];
			
				for (int i = 0; i < 65; i++) {
					map = new HashMap<String, Data>(data_map.get(i+1950));
					data = null;
					for (String k : keys) {
						if (map.get(k) != null) {
							data = map.get(k);
							map = data.getChildrenMap();
						}
					}
					// get current Data
					double sum = 0.0;
					//System.out.println(key_set);
//					percentages = new double[key_set.size()];
					
//					int iterator = 0;
//					for (String key : key_set) {
//						double number = data.getValues().get(key);
//						percentages[iterator] = number;
//						sum += number;
//						iterator++;
//					}
//								System.out.println("key: " + key);
					if (data.getValues().get(key) == null) {
						numbers[i] = 0;
						
					} else {
						numbers[i] = data.getValues().get(key);
						
					}
				}
//				System.out.println("key: " + key);
//				printArray(numbers);
				category_numbers.put(key, numbers);
			}
//			System.out.println("cat_nums: " + category_numbers);
		} else {
		for (int i = 0; i < 65; i++) {
			map = new HashMap<String, Data>(data_map.get(i+1950));
			if (current_tree_path.equals("root/")){
				
		//		System.out.println(map.get("birth").getValues().get("birth"));
		//		double num = deaths + births;
		//		deaths /= num;
		//		births /= num;
				
				
				// TODO dont set it every year dumbass
				numbers_birth [i] = map.get("birth").getValues().get("birth");
				numbers_death [i] = map.get("death").getValues().get("death");
				
			} 
		}
		category_numbers.put("birth", numbers_birth);
		category_numbers.put("death", numbers_death);
		}


			// ********** DRAW DIAGRAM ********** //
			int x_min = 75;
			int x_max = width / 4 - 25;

			int y_min = (int) (height * 0.5);
			int y_max = 25;

			if (width * 0.5 - max_radius - 10 <= x_max) {
				Point2D.Double corner_point = Segment.rotatePoint(new Point2D.Double(width * 0.5, height * 0.5 - max_radius), new Point2D.Double(width * 0.5, height * 0.5), -45.0);
				x_max = (int) (corner_point.getX() - 25);
				y_min = (int) (corner_point.getY() - 25);
			}
			
			Rectangle2D.Double bg_rect = new Rectangle2D.Double (x_min, y_max - 2, x_max - x_min, y_min - y_max + 2);
			g2D.setColor(new Color(180, 180, 180));
			g2D.fill(bg_rect);
			g2D.draw(bg_rect);
			
			
			int diagram_line_y = y_min;
			g2D.setColor(Color.BLACK);
			g2D.drawLine(x_min, diagram_line_y, x_max, diagram_line_y);
			g2D.drawString("1950", x_min - 12, diagram_line_y + 16);
			g2D.drawString("2015", x_max - 20, diagram_line_y + 16);
			

			double min = Integer.MAX_VALUE;
			double max = 0;
			
			// do this already when collecting data
			for (String key : category_numbers.keySet()) {
				
				for (double p : category_numbers.get(key)) {
					max = p > max ? p : max;
					min = p < min ? p : min;
				}
			}
			
			g2D.drawLine(x_min, diagram_line_y, x_min, 25);
			g2D.drawString("" + min, 10, y_min);		
			g2D.drawString("" + max, 10, y_max + 4);
			
			// ********** DRAW DATA LINES ********** //

			
//			max *= 100;
//			min *= 100;
			
			double pixel_per_min_max = ((y_min - y_max) / (max - min));
//			double pixel_per_min_max = ((y_min - y_max) / (max));
//			System.out.println("pixel_per_min_max: " + pixel_per_min_max);
//			System.out.println("(" + y_min + " - " + y_max +") / (" + max + " - " + min+")");
			
			double diagram_pixel_per_year = (x_max - x_min) / 64.0;
			
			Color clr = new Color(255, 128, 0);
//			Color clr = Color.RED;
			int color_gradient = (3 * 255) / (percentages.length + 1);
			int iter = 0;
//			System.out.println(category_numbers.keySet());
			for (String key : category_numbers.keySet()) {
//				System.out.println("key: " + key);
				
				int x_coord = x_min;
				int y_coord = diagram_line_y;

				int last_x = x_min;
				double[] tmp_numbers = category_numbers.get(key);

				
				int last_y = (int) (y_min - (pixel_per_min_max) * (tmp_numbers[0] - min)) ;
	//			System.out.println("length: " + TMP_numbers.length);
				for (Segment s : segments.get(level)){
	//				System.out.println("searching for: " + key + "		segment: " + s.label);
					if (s.label.equals(key)) {
	//					System.out.println("is this sleceted?: " + key);
						if(selected_segments.contains(key) || selected_segments.size() == 0) {
							clr = s.color;
	//						System.out.println("Found: " + key);
						} else {
							clr = new Color(180, 180, 180);
						}
					}
				}

					
//				System.out.println("LENGTH: " + tmp_numbers.length);
				for (int i = 1; i < tmp_numbers.length; i++) {
//				for (int i = 2; i <= tmp_numbers.length; i++) {
					x_coord = x_min + (int) ((i) * diagram_pixel_per_year);
					g2D.setColor(clr);
		//			last_x = (int) (percentages[i - 1] * 100);
		//			Data d = data_map.get("" + i).get(s.label);
					int y = (int) (y_min - (pixel_per_min_max) * (tmp_numbers[i] - min) );
//					int y = (int) (y_min - (pixel_per_min_max) * (tmp_numbers[i-1] - min) );
//					int y = (int) (y_min - (pixel_per_min_max) * (TMP_numbers[i]) );
		//			System.out.println("y: " + y);
		//			System.out.println(y_min + " - " + pixel_per_min_max + " * " + (numbers[i] - min));
					g2D.drawLine(x_coord, y, last_x, last_y);
//					System.out.println("y: " + y + " last y: " + last_y + " y min: " + y_min);
					last_x  = x_coord;
					last_y = y;
			
//					clr = new Color( min((i + 1) * color_gradient, 255), min((int) (0.5 * (i + 1) * color_gradient), 255), min((int) (0.33 * (i + 1) * color_gradient), 255));
				}
//				clr = new Color( min((iter + 1) * color_gradient, 255), min((int) (0.5 * (iter + 1) * color_gradient), 255), min((int) (0.33 * (iter + 1) * color_gradient), 255));
//				clr = new Color(min(clr.getRed() + 100, 255), min(clr.getGreen() + 50, 255), min(clr.getBlue() + 30, 255), 100);
				iter++;
			}

			// ********** DRAW YEAR LINE ********** //
			
			g2D.setColor(Color.BLUE);
			int x_year = x_min + (int) (diagram_pixel_per_year * (year - 1950 - 1));
			g2D.drawLine(x_year, y_min, x_year, y_max);
			g2D.setColor(Color.BLACK);

			
		
	}


	private void setPercentages(HashMap<String, Data> map, String new_current_tree_path){
		if (new_current_tree_path.equals("root/")){
			// init piechart
			double births = map.get("birth").getValues().get("birth");
			double deaths = map.get("death").getValues().get("death");		
			
			double num = deaths + births;
			deaths /= num;
			births /= num;
			
			percentages = new double[] {births, deaths};
			labels = new String[] {"birth", "death"};
			
		} else if (categoric) {			
			root = getRootData(new_current_tree_path); 
			
			Set<String> key_set = root.getChildrenMap().keySet();
			percentages = new double[key_set.size()];
			
			for (int i = 0; i < key_set.size(); i++) {
				percentages[i] = 1.0 / key_set.size();
			}
			labels = root.getChildrenMap().keySet().toArray(new String[root.getChildrenMap().keySet().size()]);
			
		} else {
			root = getRootData(new_current_tree_path);
			
			double sum = 0.0;
			Set<String> key_set = root.getValues().keySet();
			
			percentages = new double[key_set.size()];
			
			// sum up percentages
			int iterator = 0;
			for (String key : key_set) {
				double number = root.getValues().get(key);
				percentages[iterator] = number;
				sum += number;
				iterator++;
			}
			
			for (int i = 0; i < percentages.length; i++) {
				percentages[i] /= sum;
			}
			labels = key_set.toArray(new String[key_set.size()]);
			
		}
	}
	

	// go through tree path and return data at the end of the tree path
	private Data getRootData(String tree_path) {
		String[] keys = tree_path.split("/");
		HashMap<String, Data> m = new HashMap<String, Data>(data_map.get(year));
		Data data = null;
		
		for (String key : keys) {
			if (m.get(key) != null) {
				data = m.get(key);
				m = data.getChildrenMap();
			}
		}
		return data;
	}

	
	// draw segments that are in the inner part of the circle
	public void drawInnerData(Point2D.Double center, double radius, Graphics2D g2D, double prev_radius) throws IOException {
		ArrayList<Segment> segment_per_lvl = new ArrayList<Segment>();
		
		String[] dirs = current_tree_path.split("/");
		int color_gradient = (3 * 255) / (percentages.length + 1);
		
		// create polygons by rotating the points
		double pos = 0;
		for (int i = 0; i < percentages.length; i++) {

			// angle
			double angle = -360 * percentages[i];
			
			// color
			Color clr = new Color( min((i + 1) * color_gradient, 255), min((int) (0.5 * (i + 1) * color_gradient), 255), min((int) (0.33 * (i + 1) * color_gradient), 255));
			clr = new Color(clr.getRed(), clr.getRed(), clr.getRed() , (int) (clr.getAlpha() * 0.5));
			if (labels[i].equals(dirs[level + 1])) {// mark chosen parents as blue
				clr = new Color (0, 0, 102);
			}
			
			// points
			Point2D.Double start_pos = new Point2D.Double(center.getX(), center.getY() - radius);
			start_pos = Segment.rotatePoint(start_pos, center, pos);
			Point2D.Double end_pos = Segment.rotatePoint(start_pos, center, angle);
			
			Segment segment = new Segment(labels[i], root, clr, categoric, percentages[i]);
			segment.createPolygon(center, start_pos, end_pos, radius, angle, labels.length, prev_radius);
			
			segment_per_lvl.add(segment);
			pos += angle;
		}
		
		segments.put(level, segment_per_lvl);
	}
	
	
	public void drawOuterData(Point2D.Double center, double radius, Graphics2D g2D, double prev_radius) throws IOException {
		ArrayList<Segment> segment_per_lvl = new ArrayList<Segment>();
		
		Color clr = new Color(255, 128, 0);
		int color_gradient = (3 * 255) / (percentages.length + 1);

		double selected_perc = 0;
		double unselected_perc = 0;
		ArrayList<Integer> selected_index_list = new ArrayList<>();
		ArrayList<Integer> unselected_index_list = new ArrayList<>();
		
		double part_unselected = 60.0;
		
		// sum of selected/unselected percentages
		for (int i = 0; i < percentages.length; i++) {
			if ((selected_segments.contains(labels[i]) && !categoric && selection_chosen)) {
				selected_perc += percentages[i];
				selected_index_list.add(i);
			} else {
				unselected_perc += percentages[i];
				unselected_index_list.add(i);
			}
		}
		
		// if unselected parts are smaller than the place, decrease it to the actual size depending on the value * 360
		if (unselected_perc < part_unselected / 360) {
			part_unselected = unselected_perc * 360;
		}
		
		
		// calculate polygons for selected values
		double pos = 0;
		for (int i : selected_index_list) {
			
			// angle, if no unselected parts are left, use the usual angle
			double angle = -360 * percentages[i] / (double) selected_perc;			
			if (unselected_index_list.size() != 0) {		
				angle = -(360 - part_unselected) * percentages[i] / selected_perc;
			}
			
			// color
			clr = new Color( min((i + 1) * color_gradient, 255), min((int) (0.5 * (i + 1) * color_gradient), 255), min((int) (0.33 * (i + 1) * color_gradient), 255));

			// points
			Point2D.Double start_pos = new Point2D.Double(center.getX(), center.getY() - radius);
			start_pos = Segment.rotatePoint(start_pos, center, pos);
			Point2D.Double end_pos = Segment.rotatePoint(start_pos, center, angle);
			
			Segment segment = new Segment(labels[i], root, clr, categoric, percentages[i]);
			segment.createPolygon(center, start_pos, end_pos, radius, angle, labels.length, prev_radius);
			
			segment_per_lvl.add(segment);
			pos += angle;
		}
		
	
		// calculate polygons for unselected values
		for (int i : unselected_index_list) {
			
			// angle, if keys are presses use categoric angle and if no parts are selected, use the usual angle
			double angle = -360 * percentages[i];
			if (shift_pressed && ctrl_pressed) {
				angle = -360 / (double) labels.length;
			} else if (selected_index_list.size() != 0) {
				angle = -part_unselected * percentages[i] / (double) unselected_perc;
			}
			
			// color
			clr = new Color( min((i + 1) * color_gradient, 255), min((int) (0.5 * (i + 1) * color_gradient), 255), min((int) (0.33 * (i + 1) * color_gradient), 255));
			if (!selected_segments.contains(labels[i]) && ctrl_pressed ) {
				clr = new Color(clr.getRed(), clr.getRed(), clr.getRed() , (int) (clr.getAlpha() * 0.8));
			}
			
			// points
			Point2D.Double start_pos = new Point2D.Double(center.getX(), center.getY() - radius);
			start_pos = Segment.rotatePoint(start_pos, center, pos);
			Point2D.Double end_pos = Segment.rotatePoint(start_pos, center, angle);			

			Segment segment = new Segment(labels[i], root, clr, categoric, percentages[i]);
			segment.createPolygon(center, start_pos, end_pos, radius, angle, labels.length, prev_radius);
			
			segment_per_lvl.add(segment);
			pos += angle;
		}		
	
		segments.put(level, segment_per_lvl);		
	}
	
	private void drawLabels(Graphics2D g2D) {
		for (Segment s : segments.get(level)) {
			if(s.label_pos.x != 0 && s.label_pos.y != 0){
				//from http://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color, 23.08.2016, 15:40 answer from User Mark Ransom
				double fac = 1 / 255.0;
				double r = s.color.getRed() * fac;
				double g = s.color.getGreen() * fac;
				double b = s.color.getBlue() * fac;
				r = r <= 0.03928 ? r/12.92 : Math.pow((r + 0.055)/1.055, 2.4);
				g = g <= 0.03928 ? g/12.92 : Math.pow((g + 0.055)/1.055, 2.4);
				b = b <= 0.03928 ? b/12.92 : Math.pow((b + 0.055)/1.055, 2.4);
				double l = 0.2126 * r + 0.7152 * g + 0.0722 * b;
				
				String string = s.label;
				g2D.setColor(l > 0.179 ? Color.BLACK : Color.WHITE);
				g2D.drawString(string, (int) (s.label_pos.getX() - string.length() * 0.5 * 8), (int) (s.label_pos.getY() /*+ string.length() * 4*/));
				if (!categoric) {
					string = Math.round(s.percent*1000) / 10.0 + " %";
					g2D.drawString(string, (int) (s.label_pos.getX() - string.length() * 0.5 * 8), (int) (s.label_pos.getY() + 13));
				}
				g2D.setColor(s.color);
			}
		}
	}
	
	
	private int min(int i, int j) {
		return (i < j) ? i : j;
	}

	// if segment was clicked
	public void clicked(String label, int lvl) {
		String[] dirs = current_tree_path.split("/");
		
		if (lvl == dirs.length - 1) { // new level
			if (lvl < max_level - 1) {
				Data data = getRootData(current_tree_path);
				
				// check if data has children
				if (data == null) {
					current_tree_path += label + "/";
					level++;
				} else {
					if (data.children.size() != 0) {
						current_tree_path += label + "/";
						level++;							
					}
				}
			}
		} else {
			// jump back to chosen former level
			current_tree_path = "";
			for (int i = 0; i <= lvl; i++) {
				current_tree_path += dirs[i] + "/";
			}
			level = lvl;
		}
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
}
