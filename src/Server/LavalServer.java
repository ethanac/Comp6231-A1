package Server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

/**
 * Laval Server
 * @author Hao
 *
 */
public class LavalServer extends ClinicServer{
	private String[] managerList = {
			"LVLone","LVLtwo","LVLthree","LVLfour","LVLfive"
	};

	/**
	 * Constructor.
	 * @throws RemoteException 
	 */
	public LavalServer() throws RemoteException {
		super(Location.Laval, 2019);
		for(String s: managerList)
			pwList.add(s);
		
		try {
			createDRecord("LVL0007", "Jack","Molson","Maison","1234","dentist","Montreal");
			createDRecord("LVL0007", "Mark","Zac","Facebook","1111","surgeon","Laval");
			createDRecord("LVL0007", "Bill","Gates","Micros","2222","surgeon","DDO");

			createNRecord("LVL0007", "Mary","Christma","Junior","Active","01/22/2014");
			createNRecord("LVL0007", "Larry","Page","Senior","Active","05/02/2011");
			createNRecord("LVL0007", "Thierry","Henry","Junior","Terminated","08/17/2009");
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
	 * Instantiate the laval server.
	 */
	public static void main(String[] args){
		try{
			LavalServer server = new LavalServer();
			AnsCounts ans = new AnsCounts(server, ClinicServer.PORT_NO_LAVAL);
			ans.cs = server;
			server.exportServer();
			(new Thread(ans)).start();
			(new ServerLog()).systemLog();
			System.out.println("Server is up and running!");	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
