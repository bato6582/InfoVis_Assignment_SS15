package infovis.piechart;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Data {
	
	private int year = 0;
	String name = "";
	private String path = "";
	private int level = 0;
	ArrayList<String> parent_list = new ArrayList<>();
	private static String[] months_ordered = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
	
	private ArrayList<String> all_mother_ages = new ArrayList<>();
	
	public HashMap<String, Data> children = new LinkedHashMap<>();
	private HashMap<String, Integer> values = new LinkedHashMap<>();
	
	BufferedReader reader;
	BufferedReader birth_age_reader = new BufferedReader(new FileReader("data/birth_age.csv"));
	
	public Data(int y, String n) throws IOException {
		year = y;
		name = n;
		
		if (name.equals("birth")) {
			path = "data/Geburten_Monat_Sex_1950-2015.csv";
		} else if (name.equals("death")) { 
			path = "data/Tode_Monat_Sex_1950-2015.csv";
		}
		reader = new BufferedReader(new FileReader(path));
		readData();
		reader.close();
		
		reader = new BufferedReader(new FileReader(path));
		createChildren();
		reader.close();
	}
	
	public Data(int y, int lvl, String n, String p, ArrayList<String> p_list) throws IOException {
//		System.out.println("");
//		System.out.println("new Data");
		
		year = y;
		level = lvl + 1;
		name = n;
		path = p;
		parent_list = p_list;
		
		if (name.equals("birth")) {
			path = "data/Geburten_Monat_Sex_1950-2015.csv";
		} else if (name.equals("death")) { 
			path = "data/Tode_Monat_Sex_1950-2015.csv";
		}
		reader = new BufferedReader(new FileReader(path));
		readData();
		reader.close();
		
		if (level < 4) {
			reader = new BufferedReader(new FileReader(path));
			createChildren();
			reader.close();
		}
	}
	
	private void createChildren() throws IOException {
//		System.out.println("Create Children path " + path);
//		System.out.println("Name: " + name + " Parent_list: " + parent_list);
		
//		for (String month : months_ordered) {
//			if (parent_list.contains(month)) {
//			
//		}
		if (addName()) {			
			ArrayList<String> new_parent_list = new ArrayList<String>(parent_list);		
			new_parent_list.add(name);
			
			String line = reader.readLine();
			String[] words = line.split(";");
			for (String word : words) {
				ArrayList<Data> data = getChildrenData(word, new_parent_list);
				
				for (Data entry : data) {
					if (entry != null) {
						children.put(entry.name, entry);						
					}							
				}
			}
		}
	}
	

	private ArrayList<Data> getChildrenData(String word, ArrayList<String> new_parent_list) throws IOException {
		ArrayList<Data> data = new ArrayList<>();
		if (word.equals("year") || word.equals("number")) {
			return data;
		} else if (name.equals("sex")) {
			if (word.equals("male")) {
				data.add(new Data(year, level, "male", path, new_parent_list));								
			} else if (word.equals("female")) {
				data.add(new Data(year, level, "female", path, new_parent_list));
			}
		} else if ((!name.equals("sex")) && word.equals("female") && !name.equals("female") && !name.equals("male")) {
			data.add(new Data(year, level, "sex", path, new_parent_list));
		
		} else if ((!name.equals("sex")) && word.equals("male") && !name.equals("male") && !name.equals("female") ) {
			
		} else if ((name.equals("male")) && word.equals("female") ) {
			
		} else if ((name.equals("female")) && word.equals("male") ) {

		} else if ((name.equals("month"))) {
			for (String month : months_ordered) {
				data.add(new Data(year, level, month, path, new_parent_list));
			}	
			
			
		} else if (!name.equals("birth") && word.equals("age mother")) {
			
		} else {
			if (!name.equals(word) && !parent_list.contains(word)) {
				data.add(new Data(year, level, word, path, new_parent_list));
			}
		}
		return data;
	}

	private boolean addName() {
		boolean add = false;
		if (parent_list.contains("age mother")) {
			System.out.println(parent_list);
		}
			
		if (parent_list.contains(name)) {
		} else if (name.equals("male") && parent_list.contains("female")) {
		} else if (name.equals("female") && parent_list.contains("male")) {
		} else if (parent_list.size() > 1 && parent_list.get(parent_list.size() - 2) == "name") {
		} else if (parent_list.contains("age mother")) {
		} else if (name.equals("age mother")) {
		} else {
			add = true;
		}
		//System.out.println(add);
		return add;
	}

	private void readData() throws IOException {
		
		if (name.equals("birth")) {
			readBirth();
		} else if (name.equals("death")) { 
			readDeath();
		} else if (name.equals("month")) { 
			readMonth();
		} else if (name.equals("sex")) {
			readSex();
		} else if (name.equals("age mother")) {
			readAgeMother();
		} else {
			//...
		}
//		System.out.println(name + path);
				
	}
	
	
	
	private void readBirth() throws NumberFormatException, IOException {
		String line = null;
		String[] header = null;
		
		int number = 0;
		
		while ((line = reader.readLine()) != null) {
			
			String[] words = line.split(";");
			if (words[0].equals("year")) {
				header = words;
				
			} else {
				if (words[0].equals(year + "")) {
//					System.out.println("CHECK");
					number += Integer.parseInt(getValueOfCell(header, words, "number"));
				}
			}
		}
		values.put("birth", number);
		
	}
	
	private void readDeath() throws NumberFormatException, IOException {
		String line = null;
		String[] header = null;
		
		int number = 0;
		
		while ((line = reader.readLine()) != null) {
			
			String[] words = line.split(";");
			if (words[0].equals("year")) {
				header = words;
				
			} else {
				if (words[0].equals(year + "")) {
					number += Integer.parseInt(getValueOfCell(header, words, "number"));
				}
			}
		}
		values.put("death", number);
	}
	
	
	
	private void readSex() throws NumberFormatException, IOException {
		String line = null;
		String[] header = null;
		
		int number_male = 0;
		int number_female = 0;
		
		while ((line = reader.readLine()) != null) {
			
			String[] words = line.split(";");
			if (words[0].equals("year")) {
				header = words;
				
			} else {
				if (words[0].equals(year + "")) {
					number_male += Integer.parseInt(getValueOfCell(header, words, "male"));
					number_female += Integer.parseInt(getValueOfCell(header, words, "female"));
				}
			}
		}
		values.put("male", number_male);
		values.put("female", number_female);
	}
	

	private void readMonth() throws IOException {
		String line = null;
		String[] header = null;
		
		while ((line = reader.readLine()) != null) {
			
			String[] words = line.split(";");
			if (words[0].equals("year")) {
				header = words;
				
			} else {
				if (words[0].equals(year + "")) {
					String value = getValueOfCell(header, words, "month");
					int number = Integer.parseInt(getValueOfCell(header, words, "number"));
					values.put(value, number);
				}
			}
		}
	}
	
	
	private void readAgeMother() throws NumberFormatException, IOException {
		String line = null;
		String[] header = null;
		
		while ((line = birth_age_reader.readLine()) != null) {
			String[] words = line.split(";");
			if (words[0].equals("year")) {
				header = words;
				
			} else {
				if (words[0].equals(year + "")) {
					String age = words[1];
					int number = Integer.parseInt(words[2]);
					values.put(age, number);
					if (!all_mother_ages.contains(age)) {
						all_mother_ages.add(age);
					}
				}
			}
		}
	}
	

	private String getValueOfCell(String[] header, String[] words, String name) {
//		System.out.println("header " + header);
//		System.out.println("words " + words);
//		System.out.println("name " + name);
		for (int i = 0; i < header.length; i++) {
			if (header[i].equals(name)) {
				return words[i];
			}
		}
		return null;
	}
	
	public HashMap<String, Integer> getValues() {
		return values;
	}
	
	public HashMap<String, Data> getChildrenMap() {
		return children;
	}
	
	
	public String print() {
		String print = "Name: " + name + " Values: " + values;
		for (String key : children.keySet()) {
			Data child = children.get(key);
			if (child != null) {
				print += "	" + child.print();				
			}
		}
		return print;
	}
	
	
}
