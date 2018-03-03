import javax.xml.crypto.Data;
import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.net.*;

public class BookClient {
    public static void main (String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;
        int clientId;
        String mode;
        int len = 1024; // receiving byte array size
        byte[] rBuffer = new byte[len];
        DatagramPacket receivePacket, sendPacket;
        boolean sendMessage;
        boolean receiveMessage;
        Scanner clientScanner;

        if (args.length != 2) {
            System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
            System.out.println("\t(1) <command-file>: file with commands to the server");
            System.out.println("\t(2) client id: an integer between 1..9");
            System.exit(-1);
        }

        String commandFile = args[0];
        clientId = Integer.parseInt(args[1]);
        hostAddress = "localhost";
        tcpPort = 7000;// hardcoded -- must match the server's tcp port
        udpPort = 8000;// hardcoded -- must match the server's udp port
        mode = "U";


        try {

            Socket socket = new Socket(hostAddress, tcpPort);
            Scanner sc = new Scanner(new FileReader(commandFile));

            while(sc.hasNextLine()) {
                sendMessage = true;
                receiveMessage = false;
                String cmd = sc.nextLine();
                String[] tokens = cmd.split("\\s+");
                DatagramSocket datasocket = new DatagramSocket();
                InetAddress ia = InetAddress.getByName(hostAddress);
                byte[] buffer;
                String message;

                if (tokens[0].equals("setmode")) {
                    // set mode of communication
                    mode = tokens[1];
                    String[] text = {tokens[0], tokens[1]};
                    message = String.join(" ", text);

                } else if (tokens[0].equals("borrow")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    String[] text = {tokens[0], tokens[1], tokens[2]};
                    message = String.join(" ", text);
                    receiveMessage = true;


                } else if (tokens[0].equals("return")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    String[] text = {tokens[0], tokens[1]};
                    message = String.join(" ", text);
                    receiveMessage = true;


                } else if (tokens[0].equals("inventory")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    String[] text = {tokens[0]};
                    message = String.join(" ", text);
                    receiveMessage = true;

                } else if (tokens[0].equals("list")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    String[] text = {tokens[0], tokens[1]};
                    message = String.join(" ", text);
                    receiveMessage = true;

                } else if (tokens[0].equals("exit")) {
                    // TODO: send appropriate command to the server
                    String[] text = {tokens[0]};
                    message = String.join(" ", text);

                } else {
                    System.out.println("ERROR: No such command");
                    message = "";
                    sendMessage = false;
                }


                // MIGHT HAVE TO CHANGE IT SINCE FOR SOME COMMANDS MULTIPLE LINES MUST BE RECEIVED FROM THE SERVER SUCH AS FOR LIST AND INVENTORY



                // SEND MESSAGE
                if(sendMessage){
                    if(mode == "U"){
                        buffer = new byte[message.length()];
                        buffer = message.getBytes();
                        sendPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
                        datasocket.send(sendPacket);

                    } else {
                        PrintWriter pout = new PrintWriter(socket.getOutputStream());
                        pout.println(message);
                        pout.flush();
                    }
                }

                String retMessage;

                // RECEIVE MESSAGES
                if(receiveMessage){
                    if(mode == "U"){
                        receivePacket = new DatagramPacket(rBuffer, rBuffer.length);
                        datasocket.receive(receivePacket);
                        retMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("Received from Server: " + retMessage);

                    } else {
                        clientScanner = new Scanner(socket.getInputStream());
                        retMessage = sc.nextLine();
                        System.out.println("Received from Server: " + retMessage);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SocketException se){
            se.printStackTrace();
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }
}