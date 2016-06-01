package Client;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Scanner;

import Server.ClinicServerInterface;

/**
 * Manager client.
 * @author Hao
 *
 */
@SuppressWarnings("deprecation")
public class ManagerClient{
	public static String[] dRec = {
			"Please enter the First name of the doctor:",
			"Please enter the Last name of the doctor:",
			"Please enter the Address of the doctor:",
			"Please enter the Phone number of the doctor:",
			"Please enter the Specialization of the doctor:",
			"Please enter the location of the doctor(Montreal, Laval or DDO):"
	};
	
	public static String[] nRec = {
			"Please enter the First name of the nurse:",
			"Please enter the Last name of the nurse:",
			"Please enter the Designation of the nurse:",
			"Please enter the Status of the nurse:",
			"Please enter the Status date of the nurse: (mm/dd/yyyy)"
	};
	
	/**
	 * callback method.
	 */
	public String notifyMe(String message){
		System.out.println("Callback message: " + message);
		return message;
	}
	
	/**
	 * Check the validity of the manager ID input by user.
	 * @param mID
	 * @return
	 */
	public static boolean checkManagerID(String mID){
		if(!mID.substring(0,3).equals("MTL") && !mID.substring(0,3).equals("LVL") && !mID.substring(0,3).equals("DDO"))
			return false;
		else if(mID.length() != "MTL0001".length())
			return false;
		for(int i = 3; i < mID.length()-1; i++){
			if(!"0123456789".contains(mID.substring(i,i+1)))
				return false;
		}
		return true;
	}
	
	/**
	 * Show main menu of the user interface.
	 */
	public static void showMenu()
	{
		System.out.println("\n****Welcome to DSMS Manager Client****\n");
		System.out.println("Please select an option (1-5)");
		System.out.println("1. Create a new doctor record.");
		System.out.println("2. Create a new nurse record.");
		System.out.println("3. Get the number of doctor or nurse.");
		System.out.println("4. Edit a record.");
		System.out.println("5. Exit.");
	}
	
	/*
	 * Show the choice of record type.
	 */
	public static void showType(){
		
		System.out.println("Please select a field to edit: (1 or 2)");
		System.out.println("1. Doctor");
		System.out.println("2. Nurse");
	}
	
	/*
	 * Show the choice of doctor record fields.
	 */
	public static void showDFields()
	{
		System.out.println("Please select a field to edit: (1-3)");
		System.out.println("1. Address");
		System.out.println("2. Phone number");
		System.out.println("3. Location");
	}
	
	/*
	 * Show the choice of doctor record location.
	 */
	public static void showLocation(){
		System.out.println("Please select a new location: (1-3)");
		System.out.println("1. Montreal");
		System.out.println("2. Laval");
		System.out.println("3. DDO");
	}
	
	/*
	 * Show the choice of nurse record field.
	 */
	public static void showNFields()
	{
		System.out.println("Please select a field to edit: (1-3)");
		System.out.println("1. Designation");
		System.out.println("2. Status");
		System.out.println("3. Status Date");
	}
	
	/*
	 * Show the choice of nurse designation.
	 */
	public static void showDesignation(){
		System.out.println("Please select a new designation: (1 or 2)");
		System.out.println("1. Junior");
		System.out.println("2. Senior");
	}
	
	/*
	 * Show the choice of nurse status.
	 */
	public static void showStatus(){
		System.out.println("Please select a new status: (1 or 2)");
		System.out.println("1. Active");
		System.out.println("2. Terminated");
	}
	
	/*
	 * Main method of user interface.
	 */
	public static void main(String[] args) {
		
		int userChoice=0;
		String userInput="";
		String userID = "";
		String rmiAddr = "";
		Scanner keyboard = new Scanner(System.in);
		try{
			System.setSecurityManager(new RMISecurityManager());
			ClinicServerInterface server = null;
			while(true){
				System.out.println("\n****Welcome to DSMS Manager Client****\n");
				System.out.println("Please enter your manager ID to log in:");
				userID = keyboard.next();
				if(checkManagerID(userID)){
					if(userID.substring(0, 3).equals("MTL")){rmiAddr = "rmi://localhost:2020/DSMSatMontreal";}
					if(userID.substring(0, 3).equals("LVL")){rmiAddr = "rmi://localhost:2019/DSMSatLaval";}
					if(userID.substring(0, 3).equals("DDO")){rmiAddr = "rmi://localhost:2018/DSMSatDDO";}
					server = (ClinicServerInterface) Naming.lookup(rmiAddr);
					System.out.println("Please enter your password: ");
					String passWd = keyboard.next();
					if(server.ManagerCheck(userID, passWd)){
						System.out.println("Your identity is valid. Welcome back!");
						(new ClientLog(userID, "logged in.")).userLog();
						break;
					}
					else{
						System.out.println("Wrong manager ID or password, please try again.");
					}
				}
				else{
					System.out.println("Invalid manager ID, please try again.");
				}
			}
		
			showMenu();
		
			while(true)
			{
				Boolean valid = false;
				
				// Enforces a valid integer input.
				while(!valid)
				{
					try{
						userChoice=keyboard.nextInt();
						valid=true;
					}
					catch(Exception e)
					{
						System.out.println("Invalid Input, please enter an Integer");
						valid=false;
						keyboard.nextLine();
					}
				}
				
				// Manage user selection.
				switch(userChoice)
				{
				case 1: 
					String[] doc = new String[6];
					for(int i = 0; i < dRec.length-1; i++){
						System.out.println(dRec[i]);
						userInput=keyboard.next();
						doc[i] = userInput;
					}
					showLocation();
					int inputLoc = keyboard.nextInt();
					switch(inputLoc){
					case 1: 
						doc[doc.length-1] = "Montreal";
						break;
					case 2:
						doc[doc.length-1] = "Laval";
						break;
					case 3:
						doc[doc.length-1] = "DDO";
						break;
					default:
						System.out.println("Invalid input, please try again.");
					}	
					
					System.out.println(server.createDRecord(userID, doc[0], doc[1], doc[2], doc[3], doc[4], doc[5]));
					(new ClientLog(userID, "created a doctor record.")).userLog();
					showMenu();
					break;
				case 2:
					String[] nur = new String[5];
					boolean fail = false;
					for(int i = 0; i < nRec.length-3; i++){
						System.out.println(nRec[i]);
						userInput=keyboard.next();
						nur[i] = userInput;
					}
					showDesignation();
					int inputDes = keyboard.nextInt();
					switch(inputDes){
					case 1:
						nur[2] = "Junior";
						break;
					case 2:
						nur[2] = "Senior";
						break;
					default:
						System.out.println("Invalid input, please try again.");
						fail = true;
					}
					if(fail){break;}
					showStatus();
					int inputStat = keyboard.nextInt();
					switch(inputStat){
					case 1:
						nur[3] = "Active";
						break;
					case 2:
						nur[3] = "Terminated";
						break;
					default:
						System.out.println("Invalid input, please try again.");
						fail = true;
					}
					if(fail){showMenu(); break;}
					System.out.println(nRec[nRec.length-1]);
					userInput = keyboard.next();
					nur[nur.length-1] = userInput;
					System.out.println(server.createNRecord(userID, nur[0], nur[1], nur[2], nur[3], nur[4]));
					(new ClientLog(userID, "created a nurse record.")).userLog();
					showMenu();
					break;
				case 3:
					showType();
					int typeChoice = keyboard.nextInt();
					switch(typeChoice){
					case 1:
						System.out.println(server.getRecordCounts(userID, "Doctor"));
						break;
					case 2:
						System.out.println(server.getRecordCounts(userID, "Nurse"));
						break;
					default:
						System.out.println("Invalid input, please try again.");
					}
					(new ClientLog(userID, "checked record counts.")).userLog();
					showMenu();
					break;
				case 4:
					System.out.println("Please enter the record ID: ");
					userInput = keyboard.next();
					if(userInput.charAt(0) == 'D'){
						showDFields();
						int fieldChoice = keyboard.nextInt();
						switch (fieldChoice){
						case 1:
							System.out.println("Please enter the new Address:");
							String newAddr = keyboard.next();
							System.out.println(server.editRecord(userID, userInput, "address", newAddr));
							break;
						case 2:
							System.out.println("Please enter the new Phone number:");
							String newPhone = keyboard.next();
							System.out.println(server.editRecord(userID, userInput, "phone", newPhone));
							break;
						case 3:
							showLocation();
							int newLoc = keyboard.nextInt();
							switch(newLoc){
							case 1: 
								System.out.println(server.editRecord(userID, userInput, "location", "Montreal"));
								break;
							case 2:
								System.out.println(server.editRecord(userID, userInput, "location", "Laval"));
								break;
							case 3:
								System.out.println(server.editRecord(userID, userInput, "location", "DDO"));
								break;
							default:
								System.out.println("Invalid input, please try again.");
							}	
							break;
						default:
							System.out.println("Invalid input, please try again.");
						}
					}
					else if(userInput.charAt(0) == 'N'){
						showNFields();
						int fieldChoice = keyboard.nextInt();
						switch (fieldChoice){
						case 1:
							showDesignation();
							int newDes = keyboard.nextInt();
							switch(newDes){
							case 1:
								System.out.println(server.editRecord(userID, userInput, "designation", "Junior"));
								break;
							case 2:
								System.out.println(server.editRecord(userID, userInput, "designation", "Senior"));
								break;
							default:
								System.out.println("Invalid input, please try again.");
							}
							break;
						case 2:
							showStatus();
							int newStat = keyboard.nextInt();
							switch(newStat){
							case 1:
								System.out.println(server.editRecord(userID, userInput, "status", "Active"));
								break;
							case 2:
								System.out.println(server.editRecord(userID, userInput, "status", "Terminated"));
								break;
							default:
								System.out.println("Invalid input, please try again.");
							}
							break;
						case 3:
							System.out.println("Please enter the new Status Date: (mm/dd/yyyy)");
							String newDate = keyboard.next();
							System.out.println(server.editRecord(userID, userInput, "date", newDate));
							break;
						default:
							System.out.println("Invalid input, please try again.");
						}
					}
					else{
						System.out.println("Invalid input, please try again.");
					}
					(new ClientLog(userID, "edited a record.")).userLog();
					showMenu();
					break;
				case 5:
					System.out.println("Thanks for using DSMS Manager Client. Have a nice day!");
					keyboard.close();
					(new ClientLog(userID, "logged off.")).userLog();
					System.exit(0);
				default:
					System.out.println("Invalid Input, please try again.");
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
