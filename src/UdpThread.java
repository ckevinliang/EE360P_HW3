import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;

public class UdpThread extends Thread {
	DatagramPacket packet;
	NameTable table;
	boolean udpMode;
	
	public UdpThread(DatagramPacket packet, NameTable table){
		this.packet = packet;
		this.table = table;
	}
	
	public void run(){
		String line = new String(packet.getData(), 0, packet.getLength());
		String[] tokens = line.split("\\s+");
		String returnString = "hello";
		if (tokens[0].equals("setmode")) {
			if(tokens[1].equals("T"))
				table.setMode(false);
			else
				table.setMode(true);
			
		} else if (tokens[0].equals("borrow")) {
			String[] dupSplit = tokens[1].split("\\s+", 1); 
			int retVal = table.borrow(dupSplit[0], dupSplit[1]);
			if(retVal == 0)
				returnString = "Request Failed - Book not available";
			else if (retVal == -1)
				returnString = "Request Failed - We do not have this book";
			else
				returnString = "You request has been approved, " +retVal + " " + tokens[1] + " " + tokens[2];			
		} else if (tokens[0].equals("return")) {                  
			int val = Integer.parseInt(tokens[1]);
			int retVal = table.returnBook(val);
			if(retVal == -1)
				returnString = val + " not found, no such borrow record\n";
			else
				returnString = val + " is returned\n";
			
		} else if(tokens[0].equals("inventory")){
			table.printInventory();
		} else if (tokens[0].equals("list")){
			int retVal = table.printList(tokens[1]);
			if(retVal == -1){
				returnString = "‘No record found for " + tokens[1] + "\n";
			}
		} else if (tokens[0].equals("exit")){
			//stop processing commands from this client
			//print inventory to file inventory.txt
		} else {
			returnString = "Invalid Command";
		}
		table.printInventory();
		System.out.println(returnString);
		DatagramPacket returnPacket = new DatagramPacket(returnString.getBytes(), returnString.getBytes().length, 
		        packet.getAddress(), packet.getPort());
		    table.udpSend(returnPacket);
		//theClient.close();
	}

}
