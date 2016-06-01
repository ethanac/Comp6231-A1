package Server;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * The database of a server. A hashmap is used to contruct the database.
 * @author Hao
 *
 */
public class ClinicDatabase {
	private LinkedList<String> recList = null;
	private HashMap<Character, LinkedList<String>> map = new HashMap<Character, LinkedList<String>>();
	private HashMap<String, Character> dIDtoName = new HashMap<String, Character>();
	private HashMap<String, Character> nIDtoName = new HashMap<String, Character>();
	//private ArrayList<Character> dIDtoName = new ArrayList<Character>();
	//private ArrayList<Character> nIDtoName = new ArrayList<Character>();
	private final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/**
	 * Constructor
	 */
	public ClinicDatabase(){
		for(int i=0; i < LETTERS.length(); i++){
			map.put(LETTERS.charAt(i), new LinkedList<String>());
		}
	}
	
	/**
	 * Add a record to the hashmap.
	 * @param key
	 * @param record
	 */
	public void addRecord(Character key, String record){
		recList = map.get(key);
		recList.add(record);
		map.put(key, recList);
		if(record.charAt(0) == 'D')
			dIDtoName.put(record.split(",")[0], record.split(",")[2].charAt(0));
		else if(record.charAt(0) == 'N')
			nIDtoName.put(record.split(",")[0], record.split(",")[2].charAt(0));
		System.out.println("Record has been created. The record is: ");
		System.out.println(record);
	}
	
	/**
	 * Get a record according to the intial letter of the last name of a record.
	 * @param recordID
	 * @return
	 */
	public LinkedList<String> getRecord(String recordID){
		char type = recordID.charAt(0);
		if(type == 'D')
			return map.get(dIDtoName.get(recordID));
		else
			return map.get(nIDtoName.get(recordID));
	}
	
	/**
	 * Set a new record to the old one for editing.
	 * @param key
	 * @param position
	 * @param newRecord
	 */
	public void editRecord(Character key, int position, String newRecord){
		recList = map.get(key);
		recList.set(position, newRecord);		
		map.put(key, recList);
	}
	
}
