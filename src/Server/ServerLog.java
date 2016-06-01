package Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Log class, responsible for saving log to a file.
 * @author Hao
 *
 */
public class ServerLog {
	public String user;
	public String operation;
	public String record = "No record created or updated.";
	public String field = "No field";
	public String newValue = "new value";
	public long time;
	
	/**
	 * Default constructor.
	 */
	public ServerLog(){
		
	}
	/**
	 * Constructor.
	 */
	public ServerLog(String user, String operation, String record){
		this.user = user;
		this.operation = operation;
		if(!record.equals(""))
			this.record = record;
	}
	
	/**
	 * Constructor with arguments.
	 * @param user
	 * @param operation
	 * @param record
	 * @param field
	 * @param newValue
	 */
	public ServerLog(String user, String operation, String record, String field, String newValue){
		this.user = user;
		this.operation = operation;
		this.record = record;
		this.field = field;
		this.newValue = newValue;
	}
	
	public void userLog() throws FileNotFoundException, IOException{
		time = (new Date()).getTime();
		String fileName = user+".txt";
		String line = time + ": " + user + " did " + operation + " operation. \n--Detail: " + record + " " + operation + "d. " + field + " has been set to "+newValue;
		File f = new File(fileName);
		if(f.exists()) { 
		    writeToFile(fileName, line, true);
		}
		else{
			writeToFile(fileName, line, false);
		}
		
	}
	
	public void systemLog() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		time = (new Date()).getTime();
		String fileName = "sysLog.txt";
		String line = time + ": Server is up and running!";
		File f = new File(fileName);
		if(f.exists()) { 
		    writeToFile(fileName, line, true);
		}
		else{
			writeToFile(fileName, line, false);
		}
	}
	
	public void writeToFile(String fileName, String line, boolean exist){
		BufferedWriter bw = null;
		try {
	         // APPEND MODE SET HERE
	         bw = new BufferedWriter(new FileWriter(fileName, exist));
	         bw.write(line);
	         bw.newLine();
	         bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {                       // always close the file
			if (bw != null) try{
				bw.close();
			} catch(IOException e2){}
		}
	}
}
