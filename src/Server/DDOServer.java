package Server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

/**
 * DDO server.
 * @author Hao
 *
 */
public class DDOServer extends ClinicServer{
	private String[] managerList = {
			"DDOone","DDOtwo","DDOthree","DDOfour","DDOfive"
	};
	
	/**
	 * Constructor.
	 * @throws RemoteException 
	 */
	public DDOServer() throws RemoteException{
		super(Location.DDO, 2018);
		for(String s: managerList)
			pwList.add(s);
		
		try {
			createDRecord("DDO0007", "Jack","Molson","Maison","1234","dentist","Montreal");
			createDRecord("DDO0007", "Mark","Zac","Facebook","1111","surgeon","Laval");
			createDRecord("DDO0007", "Bill","Gates","Micros","2222","surgeon","DDO");
			createDRecord("DDO0007", "Bill","Clinton","California","4444","surgeon","Montreal");

			createNRecord("DDO0007", "Mary","Christma","Junior","Active","01/22/2014");
			createNRecord("DDO0007", "Larry","Page","Senior","Active","05/02/2011");
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Instantiate DDO server.
	 */
	public static void main(String[] args){
		try{
			DDOServer server = new DDOServer();
			AnsCounts ans = new AnsCounts(server, ClinicServer.PORT_NO_DDO); 
			ans.cs = server;
			server.exportServer();
			(new Thread(ans)).start();   //Create a thread for UDP server.
			(new ServerLog()).systemLog();
			System.out.println("Server is up and running!");	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
