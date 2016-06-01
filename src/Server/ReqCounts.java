package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Send request for counts using UDP client.
 * It is not runnable because the caller uses executerservice for multithread.
 * @author Hao
 *
 */
public class ReqCounts{
	public String countType = "";
	public String addr = "";
	public int serverPort;
	public String remoteCounts = "-1";
	public boolean newRec = false;
	
	/**
	 * Constructor.
	 * @param countType
	 * @param addr
	 * @param serverPort
	 */
	public ReqCounts(String countType, String addr, int serverPort){
		this.countType = countType;
		this.addr = addr;
		this.serverPort = serverPort;
	}
//	public void run(){
//		req();
//	}
	
	/**
	 * Get counts from remote servers using UDP.
	 * @return
	 */
	public void req(){
		DatagramSocket cSocket = null;	
		
		try{
			cSocket = new DatagramSocket();
			byte[] m = countType.getBytes();
			InetAddress aHost = InetAddress.getByName(addr);
			DatagramPacket request = new DatagramPacket(m, countType.length(), aHost, serverPort);
			cSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			cSocket.receive(reply);
			remoteCounts = new String(reply.getData());
		}
		catch(SocketException e){System.out.println("Socket: "+ e.getMessage());}
		catch(IOException e){System.out.println("IO: " + e.getMessage());}
		finally{if(cSocket != null) cSocket.close();}
		//return remoteCounts;
	}
}
