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
			while(true){
				//receive the message depending on what the current mode is
				if(udpMode){
					//receive Datagram
				}
				else{
					
				}
				Scanner sc = new Scanner(theClient.getInputStream());
				PrintWriter pout = new PrintWriter(theClient.getOutputStream());
				String command = sc.nextLine();
				System.out.println("received:" + command);
				Scanner st = new Scanner(command);  
				String tag = st.next();
				String tokens[] = tag.split("\\s+");
				if (tokens[0].equals("setmode")) {
					if(tokens[1].equals("T"))
						udpMode = false;
					else
						udpMode = true;
					
				} else if (tokens[0].equals("borrow")) {
					//split token properly WRONGG
					int retVal = table.borrow(tokens[1], tokens[2]);
					if(retVal == 0)
						pout.println("Request Failed - Book not available");
					else if (retVal == -1)
						pout.println("Request Failed - We do not have this book");
					else
						pout.println("You request has been approved, " +retVal + " " + tokens[1] + " " + tokens[2]);				}
				else if (tokens[0].equals("blockingFind")) {
					
				} else if (tokens[0].equals("return")) {
					int val = Integer.parseInt(tokens[1]);
					table.returnBook(val);
				}
				pout.flush();
				theClient.close();
			} catch (IOException e) {
				System.err.println(e);
			}

			}
				
			
	}
	
	public static void handleClientRequest(){
		
	}
}
