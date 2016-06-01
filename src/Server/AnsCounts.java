package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * UDP server, responsible for returning record counts or total counts.
 * @author Hao
 */
public class AnsCounts implements Runnable{
	public ClinicServer cs = null;
	private int port;
	
	public AnsCounts(ClinicServer cs, int p){
		this.cs = cs;
		port = p;
	}
	public void run(){
		ansCounts();
	}
	/**
	 * Reply to the counts request using UDP.
	 */
	private void ansCounts(){
		DatagramSocket aSocket = null;
		try{
		  aSocket = new DatagramSocket(port);
		  //byte[] buffer = new byte[1000];
		  while(true){
			  String s = "";
			  byte[] buffer = new byte[1000];
			  DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			  DatagramPacket reply = null;
			  aSocket.receive(request);
			  s = new String(request.getData()).trim();
			  if(s.equals("D")){   //Get the doctor counts.
				  String content = cs.getDCounts()+"";
				  reply = new DatagramPacket(content.getBytes(), content.length(), request.getAddress(), request.getPort());
			  }
			  else if(s.equals("N")){   //Get the nurse counts.
				  String content = cs.getNCounts()+"";
				  reply = new DatagramPacket(content.getBytes(), content.length(), request.getAddress(), request.getPort());
			  }
			  else if(s.equals("ADDD")){   //Increment and get the total counts of doctor before adding a doctor record.
				  cs.updateTotalDCounts();
				  String content = cs.getTotalDCounts()+"";
				  reply = new DatagramPacket(content.getBytes(), content.length(), request.getAddress(), request.getPort());
			  }
			  else if(s.equals("ADDN")){   //Increment and get the total counts of nurse before adding a nurse record.
				  cs.updateTotalNCounts();
				  String content = cs.getTotalNCounts()+"";
				  reply = new DatagramPacket(content.getBytes(), content.length(), request.getAddress(), request.getPort());
			  }
			  aSocket.send(reply);
		  }
		}
		catch(SocketException e){System.out.println("Socket: " + e.getMessage());}
		catch(IOException e){System.out.println("IO: " + e.getMessage());}
		finally{if(aSocket != null) aSocket.close();}
	}
}
