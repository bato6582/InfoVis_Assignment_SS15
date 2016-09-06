package infovis.piechart;
import infovis.piechart.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* stores the HashMap dataMap with all values. Once, the data was processed, the map will be stored in the file "data/datamap.ser"
 * The next time, the system starts, it will read the object out of this file to avoid processing time.
*/
public class DataMap implements Serializable{
	public HashMap<Integer, HashMap<String, Data>> data_map = new LinkedHashMap<>();
}
