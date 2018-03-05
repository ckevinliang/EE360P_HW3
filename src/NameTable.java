import java.util.*;
import java.util.Map.Entry;

import javax.rmi.CORBA.Util;

import java.net.*;
public class NameTable {
	class NameEntry {
		public String name;
		public InetSocketAddress addr;
		public HashMap<Integer, String> student_books;
		public NameEntry(String pName){
			name = pName;
			student_books = new HashMap<Integer, String>();
		}
	}
	public DatagramSocket datasocket;
	HashMap<String, Integer> inventory;
	ArrayList<NameEntry> table = new ArrayList<NameEntry>(); 
	Integer recordID=0;
	boolean udpMode= true;
	
	public NameTable(HashMap<String,Integer> inventory, DatagramSocket datasocket){
		this.inventory = inventory;
		this.datasocket = datasocket;
	}
	public synchronized Integer borrow(String student, String bookName) {
		System.out.println("Searching " + student);
		for (String book: inventory.keySet()){
			if (book.equals(bookName)){
				//update inventory
				System.out.println(book + " in loop");
				int count = inventory.get(bookName);
				if(count == 0)
					return 0;
				count--;
				inventory.put(bookName, count);	
				recordID++;
				this.insert(student, bookName, recordID);
				return recordID;
			}
		}
		
				return -1;
	}
	// returns 0 if old value replaced, otherwise 1
	public synchronized void insert(String studentName, String bookName, int recordID) {
		System.out.println("Inserting " + studentName);
		for (NameEntry entry: table){
			if (studentName.equals(entry.name)) { 
				entry.student_books.put(recordID, bookName);
				return;
			}
		}
		//student does not exist
		NameEntry temp = new NameEntry(studentName);
		temp.student_books.put(recordID, bookName);
		table.add(temp);
		//notifyAll();
		return;
	}
	public synchronized Integer blockingFind(String procName, String bookName) {
		System.out.println("blockingFind " + procName);
		Integer addr = borrow(procName, bookName);
		while (addr == null) {
			//Util.wait(); not sure if we need this
			addr = borrow(procName, bookName);
		}
		return addr;
	}
	
	public synchronized int returnBook(Integer id){
		System.out.println("Searching " + id);
		for (NameEntry student: table){
			if (student.student_books.containsKey(id)){
				//update student book
				String bookName = student.student_books.get(id);
				student.student_books.remove(id);
				//update inventory
				int count = inventory.get(bookName);
				count++;
				inventory.put(bookName, count);	
				return id;
			}
		}
		return -1;
		
	}
	public synchronized void clear() {
		table.clear();
	}
	
	public synchronized void setMode(boolean udp){
		this.udpMode = udp;
	}
	
	public synchronized void printInventory(){
		for (Map.Entry<String, Integer> entry: inventory.entrySet()){
			System.out.println(entry.getKey() + " " + entry.getValue() );
		}
	}
	
	public synchronized int printList(String studentName){
		int found = -1;
		for(NameEntry entry: table){
			if(entry.name.equals(studentName)){
				for(Map.Entry<Integer, String> e: entry.student_books.entrySet()){
					System.out.println(e.getKey() + " " + e.getValue());
				}
				found = 1;
			}
		}
		return found;
	}
	
	public synchronized void udpSend(DatagramPacket packet){
		try{
			datasocket.send(packet);
		}
		catch(Exception e){
		      e.printStackTrace();
		    }
	}
}