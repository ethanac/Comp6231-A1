package Test;

import org.junit.Test;

import Server.ClinicServer;
import Server.Location;

import org.junit.Before;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Test creating, editing records using multithread. And test synchronization.
 * @author Hao
 *
 */
public class ConcurrencyTest {
	ArrayList<Thread> tList1 = new ArrayList<Thread>();
	private final int THREAD_NO = 5;

	String[] idList = {"NR00010", "NR00011", "NR00012", "NR00013", "NR00014", "NR00015", "NR00016", "NR00017", "NR00018", "NR00019", 
			"NR00020", "NR00021", "NR00022", "NR00023", "NR00024", "NR00025", "NR00026", "NR00027", "NR00028", "NR00029", "NR00030", 
			"NR00031", "NR00032", "NR00033", "NR00034", "NR00035"
	};
	
	ClinicServer server;
	ArrayList<ServerTest> stTopList = new ArrayList<ServerTest>();
	
	/**
	 * Create the sample ClinicServer for testing.
	 */
	@Before
	public void setUp() {
		try {
			server = new ClinicServer(Location.Montreal, 2020);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test creating many doctor records.
	 */
	@Test
	public void testCreateDRecord(){
		ArrayList<ServerTest> stList = new ArrayList<ServerTest>();
		for(int i = 0; i < THREAD_NO; i++){
			stList.add(new ServerTest(1,server));
		}
		for(int i = 0; i < THREAD_NO; i++){
			tList1.add(new Thread(stList.get(i)));
		}
		for(Thread t : tList1)
			t.start();
		for(Thread t: tList1){
			try {
				t.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		assertEquals(server.getDCounts(), 130);
	}
	
	/**
	 * Test creating many nurse records simultaneously.
	 */
	@Test
	public void testCreateNRecord(){
		ArrayList<ServerTest> stList = new ArrayList<ServerTest>();
		for(int i = 0; i < THREAD_NO; i++){
			stList.add(new ServerTest(2,server));
		}
		for(int i = 0; i < THREAD_NO; i++){
			tList1.add(new Thread(stList.get(i)));
		}
		for(Thread t : tList1)
			t.start();
		for(Thread t: tList1){
			try {
				t.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		assertEquals(server.getNCounts(), 130);
	}
	
	/**
	 * Test editing records
	 */
	@Test
	public void testEditRecord(){
		ArrayList<ServerTest> stList = new ArrayList<ServerTest>();
		for(int i = 0; i < 26; i++){
			stList.add(new ServerTest(2,server));
		}
		for(int i = 0; i < 26; i++){
			tList1.add(new Thread(stList.get(i)));
		}
		for(Thread t : tList1)
			t.start();
		for(Thread t: tList1){
			try {
				t.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		for(int i = 0; i < 26; i++){
			stList.get(i).choice = 3;
			stList.get(i).recID = idList[i];
		}
		ArrayList<Thread> tList2 = new ArrayList<Thread>();
		for(int i = 0; i < 26; i++){
			tList2.add(new Thread(stList.get(i)));
		}
		for(Thread t : tList2)
			t.start();
		for(Thread t: tList2){
			try {
				t.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		assertEquals(server.getNCounts(), 676);
	}
	
	/**
	 * Test sync. Check the semaphore status after multiple editing operations.
	 */
	@Test
	public void testSync(){
		try {
			new ServerTest(2,server).testCreateNRecord();
		} catch (Exception e) {
			e.printStackTrace();
		};
		
		try {
			(new ServerTest(4, server)).testSync();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertFalse(server.semStateBefore);
		assertTrue(server.semStateAfter);
	}
}
