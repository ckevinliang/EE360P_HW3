import java.util.*;

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
	HashMap<String, Integer> inventory;
	ArrayList<NameEntry> table = new ArrayList<NameEntry>(); 
	Integer recordID;
	
	public NameTable(HashMap<String,Integer> inventory){
		this.inventory = inventory;
	}
	public synchronized Integer borrow(String student, String bookName) {
		System.out.println("Searching " + student);
		for (String book: inventory.keySet()){
			if (book.equals(bookName)){
				//update inventory
				int count = inventory.get(bookName);
				if(count == 0)
					return 0;
				count--;
				inventory.put(bookName, count);	
				insert(student, bookName, recordID++);
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
		notifyAll();
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
	
	public synchronized void returnBook(Integer id){
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
				return;
			}
		}
		
		
	}
	public synchronized void clear() {
		table.clear();
	}
}