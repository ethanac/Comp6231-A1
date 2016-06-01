package Server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

/**
 * Montreal Server
 * @author Hao
 *
 */
public class MTLServer extends ClinicServer{
	private String[] managerList = {
			"MTLone","MTLtwo","MTLthree","MTLfour","MTLfive"
	};
	
	/**
	 * Constructor.
	 * @throws RemoteException 
	 */
	public MTLServer() throws RemoteException {
		super(Location.Montreal, 2020);
		for(String s : managerList)
			pwList.add(s);
		
		try {
			createDRecord("MTL0007", "Jack","Molson","Maison","1234","dentist","Montreal");
			createDRecord("MTL0007", "Mark","Zac","Facebook","1111","surgeon","Laval");
			
			createNRecord("MTL0007", "Mary","Christma","Junior","Active","01/22/2014");
			createNRecord("MTL0007", "Larry","Page","Senior","Active","05/02/2011");
			createNRecord("MTL0007", "Thierry","Henry","Junior","Terminated","08/17/2009");
			createNRecord("MTL0007", "Bill","Gates","Senior","Active","10/03/2010");
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
	 * Instantiate the montreal server.
	 */
	public static void main(String[] args){
		try{
			MTLServer server = new MTLServer();
			AnsCounts ans = new AnsCounts(server, ClinicServer.PORT_NO_MONTREAL);
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
