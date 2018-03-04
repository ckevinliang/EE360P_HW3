import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

public class BookServer {
	NameTable table;
	
	public BookServer(HashMap<String,Integer> inventory) {
	table = new NameTable(inventory);
	}
	
  public static void main (String[] args) {
    int tcpPort;
    int udpPort;
    boolean udp = true;
    HashMap <String,Integer> inventory = new HashMap <String, Integer>();
    
    if (args.length != 1) {
      System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
      System.exit(-1);
    }
    
    String fileName = args[0];
    tcpPort = 7000;
    udpPort = 8000;
    int len = 1024;
    
    // parse the inventory file
 	getInventory(inventory, fileName);
	BookServer ns = new BookServer(inventory);
	System.out.println("BookServer started:");
	while(true){
		try {
			DatagramSocket datasocket = new DatagramSocket(udpPort);
			ServerSocket listener = new ServerSocket(Symbols.ServerPort);
			Socket s;
			while ( (s = listener.accept()) != null) {
				Thread t = new ServerThread(ns.table, s, true);
				t.start();
			}
		} catch (IOException e) {
			System.err.println("Server aborted:" + e);
		}
	}
	
	
	// what if there are quotations in the book name
	
	  }
  
  	public static void getInventory(HashMap<String,Integer> inventory, String fileName ){
  	    try {
  	        // FileReader reads text files in the default encoding.
  	        FileReader fileReader = new FileReader(fileName);

  	        // Always wrap FileReader in BufferedReader.
  	        BufferedReader bufferedReader = new BufferedReader(fileReader);

  	        String line;
  			while((line = bufferedReader.readLine()) != null) {
  	            String [] list = line.split("\"");
  	            System.out.println(list[2]);
  	    		list[2] = list[2].replaceAll(" ", "");
  	    		inventory.put(list[1], Integer.valueOf(list[2]));
  	        }
  			
  	        // Always close files.
  	        bufferedReader.close();         
  	    }
  	    catch(FileNotFoundException ex) {
  	        System.out.println(
  	            "Unable to open file '" + 
  	            fileName + "'");                
  	    } catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
		return;
  	}
}