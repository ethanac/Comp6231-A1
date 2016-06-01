package Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallbackClientInterface extends Remote{
	
	/**
	 * Send a message to the Callback client.
	 * @param message
	 * @return
	 * @throws RemoteException
	 */
	public String notifyMe(String message) throws RemoteException;
}
