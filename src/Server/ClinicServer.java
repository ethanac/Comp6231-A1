package Server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Clinic server class. MTLServer, LavalServer and DDOServer extend this class.
 * @author Hao
 */
public class ClinicServer implements ClinicServerInterface{
	public static final int PORT_NO_MONTREAL = 6789;
	public static final int PORT_NO_LAVAL = 6788;
	public static final int PORT_NO_DDO = 6787;
	private ClinicDatabase dbase = new ClinicDatabase();
	private HashMap<String, String> addresses = new HashMap<String, String>();
	protected ArrayList<String> pwList = new ArrayList<String>();
	private int DCounts = 0;
	private int NCounts = 0;
	private int dTotalCounts = 0;
	private int nTotalCounts = 0;
	private Semaphore[] sem = new Semaphore[26];
	public Semaphore dMutex = new Semaphore(1);
	public Semaphore nMutex = new Semaphore(1);
	public String serverLocation = "";
	public int rmiPort;
	public enum docFields{
		address, phone, location
	}
	public enum nurseFields{
		designation, status, date
	}
	public boolean semStateBefore = true;    //For testing.
	public boolean semStateAfter = true;    //For testing.
	
	/**
	 * Constructor
	 * @throws RemoteException 
	 */
	public ClinicServer(Location location, int rmiPort) throws RemoteException{
		super();
		for(int i = 0; i < sem.length; i++){
			sem[i] = new Semaphore(1);
		}
		addresses.put(Location.DDO.name(), "localhost");
		addresses.put(Location.Montreal.name(), "localhost");
		addresses.put(Location.Laval.name(), "localhost");
		
		serverLocation = location.name();
		this.rmiPort = rmiPort;
	}
	
	/*
	 * (non-Javadoc)
	 * @see Server.ClinicServerInterface#createDRecord(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String createDRecord(String userID, String FirstName, String LastName, String address, String phoneNb, String specialization, String locat) throws InterruptedException, ExecutionException, FileNotFoundException, IOException{
		String rec = FirstName + "," + LastName + ","+ address + "," + phoneNb + "," + specialization + "," + locat;
		if(!serverLocation.equals("Montreal")){
			int rep = requestAddTotal("D");
			dTotalCounts = rep;
		}
		else
			updateTotalDCounts();
		String record = "DR" + generateID(dTotalCounts) + "," + rec;;
		dbase.addRecord(LastName.toUpperCase().charAt(0), record);
		DCounts++;
		(new ServerLog(userID, "create", record.substring(0,7))).userLog();
		return ("The doctor record has been created successfully! The record is \n" + record);
	}
	
	/*
	 * (non-Javadoc)
	 * @see Server.ClinicServerInterface#createNRecord(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String createNRecord(String userID, String FirstName, String LastName, String designation, String status, String statusDate) throws InterruptedException, ExecutionException, FileNotFoundException, IOException{
		String rec = FirstName + "," + LastName + ","+ designation + "," + status + "," + statusDate;
		if(!serverLocation.equals("Montreal")){
			int rep = requestAddTotal("N");
			nTotalCounts = rep;
		}
		else
			updateTotalNCounts();
		String record = "NR" + generateID(nTotalCounts) + "," + rec;
		dbase.addRecord(LastName.toUpperCase().charAt(0), record);
		NCounts++;
		(new ServerLog(userID, "create", record.substring(0,7))).userLog();
		return ("The nurse record has been created successfully! The record is \n" + record);
	}
	
	/*
	 * (non-Javadoc)
	 * @see Server.ClinicServerInterface#editRecord(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String editRecord(String userID, String recordID, String fieldName, String newValue) throws IOException{
		String record = "";
		String newRecord = "";
		int position = -1;
		LinkedList<String> recList = dbase.getRecord(recordID);
		if(recList == null){return "The ID is invalid or the record does not exist. Please try again.";}
		for(int i = 0; i < recList.size(); i++){
			if(recList.get(i).substring(0,7).equals(recordID)){
				record = recList.get(i);
				position = i;
				break;
			}
		}
		if(recordID.charAt(0) == 'D'){
			if(DCounts == 0){return "No doctor record in system.";}
			newRecord = editDoctor(userID, position, record, fieldName, newValue);
		}
		else{
			if(NCounts == 0){return "No nurse record in system.";}
			newRecord = editNurse(userID, position, record, fieldName, newValue);
		}
		return "The record has been updated. \n The old record is: \n" + record +"\nThe new record is: \n" + newRecord;
	}
	
	/*
	 * (non-Javadoc)
	 * @see Server.ClinicServerInterface#getRecordCounts(java.lang.String, java.lang.String)
	 */
	@Override
	public String getRecordCounts(String userID, String recordType) throws IOException{
		String counts = "";
		(new ServerLog(userID, "getRecordCounts", "")).userLog();
		try{
			if(recordType.compareToIgnoreCase("Doctor") == 0){
				counts = serverLocation + " " + getDCounts() + ", ";
				counts += requestCounts("D");
				return counts;
			}
			else if(recordType.compareToIgnoreCase("Nurse") == 0){
				counts = serverLocation + " " + getNCounts() + ", ";
				counts += requestCounts("N");
				return counts;
			}
			else
				return "None";
		}
		catch(Exception e){
			e.printStackTrace();
			return "Exception occured.";
			}
	}
	
	/*
	 * (non-Javadoc)
	 * @see Server.ClinicServerInterface#ManagerCheck(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean ManagerCheck(String id, String pw){
		int key = Integer.parseInt(id.substring(3, id.length()));
		if(pw.equals(pwList.get(key-1)))
			return true;
		else
			return false;
	}
	
	/**
	 * Check the validity of record ID.
	 * @param recID
	 * @return
	 */
	public boolean IDCheck(String recID){
		if(!recID.substring(0,1).equals("D") && !recID.substring(0,1).equals("N"))
			return false;
		else if(!recID.substring(1,2).equals("R"))
			return false;
		else if(recID.length() != "DR00001".length())
			return false;
		for(int i = 2; i < recID.length()-1; i++){
			if(!"0123456789".contains(recID.substring(i,i+1)))
				return false;
		}
		return true;
	}
	
	/**
	 * Request the counts of remote servers.
	 * @param type
	 * @return reply
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public String requestCounts(String type) throws InterruptedException, ExecutionException{
		ExecutorService es = Executors.newFixedThreadPool(2);
		ArrayList<Future> futures = new ArrayList<Future>();
		String reply = "";
		int k = 0;
		String[] remoteLoc = {"", ""};
		int[] remoteAddr = {0,0};
		String[] locations = {"Montreal", "Laval", "DDO"};
		for(int i = 0; i< locations.length; i++){
			if (!locations[i].equals(serverLocation)){
				remoteLoc[k] = locations[i];
				if(i == 0){remoteAddr[k] = ClinicServer.PORT_NO_MONTREAL;}
				if(i == 1){remoteAddr[k] = ClinicServer.PORT_NO_LAVAL;}
				if(i == 2){remoteAddr[k] = ClinicServer.PORT_NO_DDO;}
				k++;
			}
		}
		Callable<String> r1 = () ->{
			ReqCounts r = new ReqCounts(type, "localhost", remoteAddr[0]);
			r.req();
			return r.remoteCounts;
		};
		futures.add(es.submit(r1));
		Callable<String> r2 = () ->{
			ReqCounts r = new ReqCounts(type, "localhost", remoteAddr[1]);
			r.req();
			return r.remoteCounts;
		};
		futures.add(es.submit(r2));
		
		reply += remoteLoc[0] + " " + futures.get(0).get() + ", ";
		reply += remoteLoc[1] + " " + futures.get(1).get();
		return reply;
	}
	
	/**
	 * When a record added, update the total counts.
	 * @param type
	 * @return
	 */
	public int requestAddTotal(String type){
		int reply = 0;
		int remoteAddr = ClinicServer.PORT_NO_MONTREAL;

		if(type.equals("D")){
			ReqCounts r = new ReqCounts("ADDD", "localhost", remoteAddr);
			r.req();
			reply = Integer.parseInt(r.remoteCounts.trim());
		}
		else if(type.equals("N")){
			ReqCounts r = new ReqCounts("ADDN", "localhost", remoteAddr);
			r.req();
			reply = Integer.parseInt(r.remoteCounts.trim());
		}
		//System.out.println(reply);
		return reply;
	}
	
	/**
	 * update the total counts of doctors.
	 */
	public void updateTotalDCounts(){
		dMutex.P();
		dTotalCounts++;
		dMutex.V();
	}
	
	/**
	 * update the total counts of nurses.
	 */
	public void updateTotalNCounts(){
		nMutex.P();
		nTotalCounts++;
		nMutex.V();
	}
	
	/**
	 * Generate ID for each record.
	 * @param n
	 * @return
	 */
	private String generateID(int num){
		String zeros = "";
		int n = num;
		int i = 0;
		while(n > 0){
			n = n / 10;
			i++;
		}
		while(i < 5){
			zeros += "0";
			i++;
		}
		return zeros+num;
	}
	
	/**
	 * Get the number of doctor.
	 * @return DCounts
	 */
	public int getDCounts(){
		return DCounts;
	}
	
	/**
	 * Get the number of nurse.
	 * @return NCounts
	 */
	public int getNCounts(){
		return NCounts;
	}
	
	/**
	 * Get the number of doctor.
	 * @return dTotalCounts
	 */
	public int getTotalDCounts(){
		return dTotalCounts;
	}
	
	/**
	 * Get the number of nurse.
	 * @return nTotalCounts
	 */
	public int getTotalNCounts(){
		return nTotalCounts;
	}
	/**
	 * Edit a doctor record.
	 * @param recordID
	 * @param record
	 * @param fieldName
	 * @param newValue
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public String editDoctor(String userID, int position, String record, String fieldName, String newValue) throws FileNotFoundException, IOException{
		boolean editable = false;
		int fieldNumber = -1;
		String newRecord = "";
		
		for(docFields s : docFields.values()){
			if(s.name().equals(fieldName.toLowerCase())){
				editable = true;
				fieldName = s.name();
				break;
			}
		}
		if(editable){
			String[] splitted = record.split(",");
			switch(fieldName){
			case "address": fieldNumber = 3;
				break;
			case "phone": fieldNumber = 4;
				break;
			case "location": fieldNumber = 6;
				break;
			}
			splitted[fieldNumber] = newValue;
			for(int i = 0; i < splitted.length-1; i++){
				newRecord += splitted[i] + ",";
			}
			newRecord += splitted[splitted.length-1];
			/*
			 * Wait for corresponding semaphore.
			 */
			int semNb = splitted[2].toUpperCase().charAt(0) - 'A';
			sem[semNb].P();
			dbase.editRecord(splitted[2].toUpperCase().charAt(0), position, newRecord);
			/*
			 * Release the semaphore after editing a record.
			 */
			sem[semNb].V();
			(new ServerLog(userID, "edit", record.substring(0,7), fieldName, newValue)).userLog();
			return newRecord;
		}
		else{
			return ("You are not allowed to edit this field!");
		}
	}
	
	/**
	 * Edit a nurse record.
	 * @param recordID
	 * @param record
	 * @param fieldName
	 * @param newValue
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public String editNurse(String userID, int position, String record, String fieldName, String newValue) throws FileNotFoundException, IOException{
		boolean editable = false;
		int fieldNumber = -1;
		String newRecord = "";

		for(nurseFields s : nurseFields.values()){
			if(s.name().equals(fieldName.toLowerCase())){
				editable = true;
				fieldName = s.name();
				break;
			}
		}
		if(editable){
			String[] splitted = record.split(",");
			switch(fieldName){
			case "designation": fieldNumber = 3;
				break;
			case "status": fieldNumber = 4;
				break;
			case "date": fieldNumber = 5;
				break;
			}
			splitted[fieldNumber] = newValue;
			for(int i = 0; i < splitted.length-1; i++){
				newRecord += splitted[i] + ",";
			}
			newRecord += splitted[splitted.length-1];
			/*
			 * Wait for corresponding semaphore.
			 */
			int semNb = splitted[2].toUpperCase().charAt(0) - 'A';
			semStateBefore = sem[semNb].isLocked();   //Check semaphore status before editing. For testing only.
			sem[semNb].P();
			semStateAfter = sem[semNb].isLocked();   //Check semaphore status beginning editing. For testing only.
			dbase.editRecord(splitted[2].toUpperCase().charAt(0), position, newRecord);
			/*
			 * Release the semaphore after editing a record.
			 */
			sem[semNb].V();
			(new ServerLog(userID, "edit", record.substring(0,7), fieldName, newValue)).userLog();
			return newRecord;
		}
		else{
			return ("You are not allowed to edit this field!");
		}
	}
	
	/**
	 * Create a doctor record for testing
	 * @param FirstName
	 * @param LastName
	 * @param address
	 * @param phoneNb
	 * @param specialization
	 * @param locat
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String createDRecord(String FirstName, String LastName, String address, String phoneNb, String specialization, String locat) throws InterruptedException, ExecutionException, FileNotFoundException, IOException{
		String rec = FirstName + "," + LastName + ","+ address + "," + phoneNb + "," + specialization + "," + locat;
		String record = "DR" + generateID(++dTotalCounts) + "," + rec;;
		dbase.addRecord(LastName.toUpperCase().charAt(0), record);
		DCounts++;
		return ("The doctor record has been created successfully! The record is \n" + record);
	}
	
	/**
	 * Create nurse record for testing
	 * @param FirstName
	 * @param LastName
	 * @param designation
	 * @param status
	 * @param statusDate
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String createNRecord(String FirstName, String LastName, String designation, String status, String statusDate) throws InterruptedException, ExecutionException, FileNotFoundException, IOException{
		String rec = FirstName + "," + LastName + ","+ designation + "," + status + "," + statusDate;
		String record = "NR" + generateID(++nTotalCounts) + "," + rec;
		dbase.addRecord(LastName.toUpperCase().charAt(0), record);
		NCounts++;
		return ("The nurse record has been created successfully! The record is \n" + record);
	}
	
	/**
	 * Edit record for testing.
	 * @param recordID
	 * @param fieldName
	 * @param newValue
	 * @return
	 * @throws IOException
	 */
	public String editRecord(String recordID, String fieldName, String newValue) throws IOException{
		String record = "";
		String newRecord = "";
		int position = -1;
		LinkedList<String> recList = dbase.getRecord(recordID);
		if(recList == null){return "The ID is invalid or the record does not exist. Please try again.";}
		for(int i = 0; i < recList.size(); i++){
			if(recList.get(i).substring(0,7).equals(recordID)){
				record = recList.get(i);
				position = i;
				break;
			}
		}
		if(recordID.charAt(0) == 'D'){
			if(DCounts == 0){return "No doctor record in system.";}
			newRecord = editDoctor("MTL0001", position, record, fieldName, newValue);
		}
		else{
			if(NCounts == 0){return "No nurse record in system.";}
			newRecord = editNurse("MTL0001", position, record, fieldName, newValue);
		}
		return "The record has been updated. \n The old record is: \n" + record +"\nThe new record is: \n" + newRecord;
	}
	
	/**
	 * Check a semaphore's status for testing.
	 * @param i
	 * @return
	 */
	public boolean checkSemaphore(int i){
		return sem[i].isLocked();
	}
	
	/**
	 * Export this server using RMI
	 * @throws Exception
	 */
	public void exportServer() throws Exception{
		Remote obj = UnicastRemoteObject.exportObject(this, rmiPort);
		Registry r = LocateRegistry.createRegistry(rmiPort);
		r.bind("DSMSat"+serverLocation, obj);
	}

}
