package Client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Save log to a file for client.
 * @author Hao
 *
 */
public class ClientLog {
	public String user;
	public String operation;
	public long time;
	
	/**
	 * Default constructor.
	 */
	public ClientLog(){
		
	}
	/**
	 * Constructor with arguments.
	 * @param user
	 * @param operation
	 * @param record
	 */
	public ClientLog(String user, String operation){
		this.user = user;
		this.operation = operation;
	}
	
	/**
	 * Create a log.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void userLog() throws FileNotFoundException, IOException{
		time = (new Date()).getTime();
		String fileName = user + "_client"+".txt";
		String line = time + ": " + user + " " + operation;
		File f = new File(fileName);
		if(f.exists()) { 
		    writeToFile(fileName, line, true);
		}
		else{
			writeToFile(fileName, line, false);
		}
		
	}
	
	/**
	 * Write a log to the log file.
	 * @param fileName
	 * @param line
	 * @param exist
	 */
	private void writeToFile(String fileName, String line, boolean exist){
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
