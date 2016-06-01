package Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import Server.ClinicServer;

/**
 * Class with methods for testing.
 * @author Hao
 *
 */
public class ServerTest implements Runnable{
	public String rmiAddr = "rmi://localhost:2020/DSMSatMontreal";
	public int choice;
	public String LastName = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public String lname = "";
	public ClinicServer server;
	public String recID;
	String[] idList = {"NR00010", "NR00011", "NR00012", "NR00013", "NR00014", "NR00015", "NR00016", "NR00017", "NR00018", "NR00019", 
			"NR00020", "NR00021", "NR00022", "NR00023", "NR00024", "NR00025", "NR00026", "NR00027", "NR00028", "NR00029", "NR00030", 
			"NR00031", "NR00032", "NR00033", "NR00034", "NR00035"
	};
	
	public ServerTest(int choice, ClinicServer server){
		this.choice = choice;
		this.server = server;
	}
	
	@Override
	public void run(){
		try{
			switch(choice){
			case 1:
				testCreateDRecord();
				break;
			case 2:
				testCreateNRecord();
				break;
			case 3:
				testEditRecord();
				break;
			case 4:
				testSync();
				break;
			}
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	/**
	 * Test "createDRecord" method.
	 * @throws IOException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws FileNotFoundException 
	 */
	public void testCreateDRecord() throws FileNotFoundException, InterruptedException, ExecutionException, IOException{
		for(int i = 0; i < 26; i++){
			server.createDRecord("Jason", LastName.substring(i,i+1), "885 Maisonneuve", "9720", "dentist", "Montreal");
		}
	}
	
	/**
	 * Test "createNRecord" method.
	 */
	public void testCreateNRecord() throws FileNotFoundException, InterruptedException, ExecutionException, IOException{
		for(int i = 0; i < 26; i++){
			server.createNRecord("Jason", LastName.substring(i,i+1), "Junior", "Active", "01/01/2001");
		}
	}
	
	/**
	 * Test "editRecord" method
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public void testEditRecord() throws FileNotFoundException, InterruptedException, ExecutionException, IOException{
		//for(int i = 0; i < 26; i++){
		System.out.println(server.editRecord(recID, "status", "Terminated"));
		//}
	}
	
	/**
	 * Test sync when multiple thread editing the same record.
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public void testSync() throws FileNotFoundException, InterruptedException, ExecutionException, IOException{
		int i = 0;
		while(i < 10000){
			server.editRecord("NR00001", "status", "Terminated");
			i++;
		}
	}
}
