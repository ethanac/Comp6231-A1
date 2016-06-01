package Server;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

/**
 * @author Hao
 * @date 05/23/2016
 * RMI-DSMS, COMP 6231 - DSMS Interface
 */

public interface ClinicServerInterface extends Remote{
	/**
	 * Create a doctor record.
	 * @param userID
	 * @param FirstName
	 * @param LastName
	 * @param address
	 * @param phoneNb
	 * @param specialization
	 * @param locat
	 * @return
	 * @throws RemoteException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public String createDRecord(String userID, String FirstName, String LastName, String address, String phoneNb, String specialization, String locat) throws RemoteException, IOException, InterruptedException, ExecutionException;
	/**
	 * Create a nurse record.
	 * @param userID
	 * @param FirstName
	 * @param LastName
	 * @param designation
	 * @param status
	 * @param statusDate
	 * @return
	 * @throws RemoteException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public String createNRecord(String userID, String FirstName, String LastName, String designation, String status, String statusDate) throws RemoteException, IOException, InterruptedException, ExecutionException;
	/**
	 * Get records counts.
	 * @param userID
	 * @param recordType
	 * @return
	 * @throws RemoteException
	 * @throws IOException
	 */
	public String getRecordCounts(String userID, String recordType) throws RemoteException, IOException;
	/**
	 * Edit a record.
	 * @param userID
	 * @param recordID
	 * @param fieldName
	 * @param newValue
	 * @return
	 * @throws RemoteException
	 * @throws IOException
	 */
	public String editRecord(String userID, String recordID, String fieldName, String newValue) throws RemoteException, IOException;
	/**
	 * Check if the user of client is valid.
	 * @param id
	 * @param password
	 * @return
	 * @throws RemoteException
	 */
	public boolean ManagerCheck(String id, String password) throws RemoteException;
//	public void registerForCallback(CallbackClientInterface callbackClientObject) throws RemoteException;
//	public void unregisterForCallback(CallbackClientInterface callbackClientObject) throws RemoteException;
}
