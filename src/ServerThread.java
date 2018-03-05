import java.net.*; import java.io.*; import java.util.*;
public class ServerThread extends Thread {
	NameTable table;
	Socket theClient;
	boolean udpMode;
	public ServerThread(NameTable table, Socket s, boolean udpMode) {
		this.table = table;
		theClient = s;
		this.udpMode = udpMode;
		
	}
	public void run() {
		try {
				InputStreamReader in = new InputStreamReader(theClient.getInputStream());
				BufferedReader bufin = new BufferedReader(in);
				PrintWriter pout = new PrintWriter(theClient.getOutputStream());
				PrintWriter out = new PrintWriter(new FileWriter("output.txt", true)); 
				String command = bufin.readLine();
				System.out.println("received:" + command);
				String retString = "hello";
				table.printInventory();
				String tokens[] = command.split("\\s+", 2);
				System.out.println("First token is "+ tokens[0]);
				if (tokens[0].equals("setmode")) {
					if(tokens[1].equals("T"))
						table.setMode(false);
					else
						table.setMode(true);
					
				} else if (tokens[0].equals("borrow")) {
					String[] dupSplit = tokens[1].split("\\s+", 2);
					dupSplit[1] = dupSplit[1].replace("\"", "");
					int retVal = table.borrow(dupSplit[0], dupSplit[1]);
					System.out.println(dupSplit[1]);
					if(retVal == 0)
						retString = "Request Failed - Book not available";
					else if (retVal == -1)
						retString= "Request Failed - We do not have this book";
					else
						retString = "You request has been approved, " +retVal + " " + dupSplit[0] + " " + dupSplit[1];
				} else if (tokens[0].equals("return")) {                  
					int val = Integer.parseInt(tokens[1]);
					int retVal = table.returnBook(val);
					if(retVal == -1)
						retString = val + " not found, no such borrow record\n";
					else
						retString=val + " is returned\n";
					
				} else if(tokens[0].equals("inventory")){
					table.printInventory();
				} else if (tokens[0].equals("list")){
					int retVal = table.printList(tokens[1]);
					if(retVal == -1){
						retString="No record found for " + tokens[1] + "\n";
					}
				} else if (tokens[0].equals("exit")){
					//stop processing commands from this client
					//print inventory to file inventory.txt
				}
				System.out.println(retString);
				//table.printInventory();
				pout.write(retString);
				pout.flush();
				theClient.close();
				out.println(retString);
				out.close();
				
			} catch (IOException e) {
				System.err.println(e);
			}
		}
}
