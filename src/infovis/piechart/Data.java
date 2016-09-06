package infovis.piechart;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Data implements Serializable {
	
	private int year = 0;
	String name = "";
	private String path = "";
	private int level = 0;
	ArrayList<String> parent_list = new ArrayList<>();
	private static String[] months_ordered = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
	
	public HashMap<String, Data> children = new LinkedHashMap<>();
	private HashMap<String, Integer> values = new LinkedHashMap<>();

	// initial constructor
	public Data(int y, String n) throws IOException {
		year = y;
		name = n;
		
		if (name.equals("birth")) {
			path = "data/Geburten_Monat_Sex_1950-2015.csv";
		} else if (name.equals("death")) { 
			path = "data/Tode_Monat_Sex_1950-2015.csv";
		}
		
		readData();
		
		createChildren();
	}
	
	// constructor for children
	public Data(int y, int lvl, String n, String p, ArrayList<String> p_list) throws IOException {
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
		
		readData();
		
		// no more levels than 5
		if (level < 4) {
			createChildren();
		}
	}
	
	private void createChildren() throws IOException {
		if (addName()) {			
			ArrayList<String> new_parent_list = new ArrayList<String>(parent_list);		
			new_parent_list.add(name);
			
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
			String[] words = line.split(";");
			
			// read data and add children
			for (String word : words) {
				ArrayList<Data> data = null;
				if (!word.equals("year") && !word.equals("number")) {
					data = getChildrenData(word, new_parent_list);
				}
				
				if (data != null) {
					for (Data entry : data) {
						if (entry != null) {
							children.put(entry.name, entry);						
						}							
					}
				}
			}
		}
	}
	

	private ArrayList<Data> getChildrenData(String word, ArrayList<String> new_parent_list) throws IOException {
		ArrayList<Data> data = new ArrayList<>();
		
		if (name.equals("female") || parent_list.contains("female")) {
			if (word.equals("male")) {
				// if female is chosen, male can not be chosen
			} else {
				if (!parent_list.contains("month")) {
					// if sex is already chosen, only month is possible children
					data.add(new Data(year, level, "month", path, new_parent_list));
				}
			}
			
		} else if (name.equals("male") || parent_list.contains("male")) {
			if (word.equals("female")) {
				// if male is chosen, female can not be chosen
			} else {
				if (!parent_list.contains("month")) {
					// if sex is already chosen, only month is possible children
					data.add(new Data(year, level, "month", path, new_parent_list));
				}
			}
		} else {
			// add female / male
			if (name.equals("sex")) {
				if (word.equals("male")) {
					data.add(new Data(year, level, "male", path, new_parent_list));						
				} else if (word.equals("female")) {
					data.add(new Data(year, level, "female", path, new_parent_list));
				}
				
			// add january - december
			} else if (name.equals("month")) {
				for (String month : months_ordered) {
					data.add(new Data(year, level, month, path, new_parent_list));
				}
				
			// add sex
			} else if ((!name.equals("sex")) && word.equals("female")) {
				data.add(new Data(year, level, "sex", path, new_parent_list));
			} else if ((!name.equals("sex")) && word.equals("male")) {
				// sex is already added by female
			
			} else if (!name.equals("birth") && word.equals("age mother")) {
				// age of mother is only a child of birth
			} else if (!name.equals("death") && word.equals("age")) {
				// age is only a child of death
				
			} else {
				if (!name.equals(word) && !parent_list.contains(word)) {
					data.add(new Data(year, level, word, path, new_parent_list));
				}
			}	
		}
		return data;
		
	}

	private boolean addName() {
		boolean add = false;			
		if (parent_list.contains(name)) {
		} else if (name.equals("male") && parent_list.contains("female")) {
		} else if (name.equals("female") && parent_list.contains("male")) {
		} else if (parent_list.size() > 1 && parent_list.get(parent_list.size() - 2) == "name") {
		} else if (parent_list.contains("age mother")) {
		} else if (name.equals("age mother")) {
		} else if (parent_list.contains("age")) {
		} else if (name.equals("age")) {
		} else {
			add = true;
		}
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
		} else if (name.equals("age")) {
			readAge();
		} else {
			//...
		}
	}
	
	
	// get values of birth
	private void readBirth() throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
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
		values.put("birth", number);
		reader.close();
	}
	
	// get values of death
	private void readDeath() throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
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
		reader.close();
	}
	
	
	private void readSex() throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = null;
		String[] header = null;
		
		
		if (parent_list.contains("month")) {
			String month = null;
			for (String mo : months_ordered) {
				if (parent_list.contains(mo)) {
					month = mo;					
				}
			}
			// if month is in parent list --> make sex values dependend on the specific month
			if (month != null) {
				while ((line = reader.readLine()) != null) {
					
					String[] words = line.split(";");
					if (words[0].equals("year")) {
						header = words;
						
					} else {
						if (words[0].equals(year + "")) {
							String m = getValueOfCell(header, words, "month");
							if (month.equals(m)) {
								values.put("male", Integer.parseInt(getValueOfCell(header, words, "male")));
								values.put("female", Integer.parseInt(getValueOfCell(header, words, "female")));
								reader.close();
								return;
							}
						}
					}
				}
			}
		}
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
		
		reader.close();
	}
	

	private void readMonth() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
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
		reader.close();
	}
	
	
	private void readAgeMother() throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader("data/birth_age.csv"));
		String line = null;
		
		String age = "";
		
		while ((line = reader.readLine()) != null) {
			String[] words = line.split(";");
			if (!words[0].equals("year")) { // not first row
				if (words[0].equals(year + "")) { // right year
					age = words[1];
					int number = Integer.parseInt(words[2]);
					if (number != 0) {
						values.put(age, number);
					}	
				}
			}
		}
		if (age.equals("")) {
			values.put("No values for this year", 1);
		}
		reader.close();
	}
	
	private void readAge() throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader("data/death_age.csv"));
		String line = null;
		
		String age = "";
		
		while ((line = reader.readLine()) != null) {
			String[] words = line.split(";");
			if (!words[0].equals("year")) { // not first row
				if (words[0].equals(year + "")) { // right year
					age = words[1];
					int number = Integer.parseInt(words[2]);
					values.put(age, number);
				}
			}
		}
		if (age.equals("")) {
			values.put("No values for this year", 1);
		}
		reader.close();
	}
	

	private String getValueOfCell(String[] header, String[] words, String name) {
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
