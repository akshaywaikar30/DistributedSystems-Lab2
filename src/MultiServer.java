//Name: AkshayMaheshWaikar
//ID: 1001373973
//Mahrsee, Rishabh. “Multi-Threaded Chat Application.” GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-1/.
//Mahrsee, Rishabh. "Multi-Threaded Chat Application." GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-2/.
//https://stackoverflow.com/questions/15247752/gui-client-server-in-java
//http://www.jmarshall.com/easy/http/ HTTP Made Really Easy. 
//Pseudo-code from Chapter 8 of Textbook
//https://docs.oracle.com/javase/8/docs/technotes/guides/lang/Countdown.java
//http://www.java2s.com/Tutorials/Java/java.nio.file/Files/Java_Files_readAllBytes_Path_path_.htm


import java.io.BufferedWriter;
import java.io.DataInputStream; // All the header files 
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Server class
public class MultiServer extends JFrame {
	static ArrayList<ClientHandler> clientlist = new ArrayList<ClientHandler>();			//creates list for handling active clients

	public static int clien = 0; // counter for clients
	
	static JTextArea lblWord;

static BufferedWriter writer=null;
	public MultiServer() { // Initialised at the time of main method call
		this.setTitle("Server");
		this.setSize(766, 768); // -- Set all the GUI properties
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// getContentPane().setLayout(null);

		lblWord = new JTextArea(); // creating text field to show client response on server
		lblWord.setAutoscrolls(true);
		lblWord.setBounds(350, 15, 1050, 1025);
		lblWord.setEditable(false);
		add(lblWord);

		JScrollPane scroll = new JScrollPane(lblWord, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,		//adds scrollbar to the gui
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scroll);

		this.setVisible(true); // making GUI visible
	}

	public static void main(String[] args) throws IOException {
		new MultiServer(); // Calling Constructor
		
		Path display = Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2", "server.txt");
		try {
			byte[] dispArray = Files.readAllBytes(display);

			String displayString = new String(dispArray, "ISO-8859-1");
			lblWord.append(displayString);
		} catch (IOException e) {
			System.out.println(e);
		}
		// server is listening on port 3214
		ServerSocket ss = new ServerSocket(3214); // Declaring the port number for communication
		Socket s; // Creating socket for transferring messages
		// running infinite loop for getting messages, runs until socket number is legit
		while (true) {
			s = ss.accept(); // Accept the incoming request
			// obtain input and output streams
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			// displays message on the server's text area
			lblWord.append("Creating a new client: User " + clien + "\n");

			// Create a new handler object for handling this request.
			ClientHandler mtch = new ClientHandler(s, "\n" + "client" + clien, dis, dos, clien);

			// Create a new Thread with this object.
			Thread t = new Thread(mtch);
			// add this client to active clients list
			clientlist.add(mtch);
			// start the thread.
			t.start();

			// increment clien for new client.

			clien++;

		}
	}
}

// ClientHandler class
class ClientHandler implements Runnable {
	Scanner scn = new Scanner(System.in);

	private String name; // declaring variables to store client name
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	int i;
	String filecreat;
	boolean isloggedin;

	// constructor
	public ClientHandler(Socket s, String name,DataInputStream dis, DataOutputStream dos, int i) {
		this.dis = dis;
		this.dos = dos;
		this.name = name;					//taking all the values needed by clienthandler constructor
		this.s = s;
		this.i = i;
		
		this.isloggedin = true;
	}

	@Override
	public void run() {

		String received;
		// long timestamp_fl = 0;
		try {
			while (true) {
				// receives the string from the client
				received = dis.readUTF();						//stores message after reading from input stream
				MultiServer.lblWord.append("From: Client" + i + received + "\n"); 	//appends word to text area
				if (received.equals("logout")) { // shows the logout functionality
					MultiServer.lblWord.append("Client" + i + "has logged out"); // where if client types logout it
																					// discontinues that client thread
					for (ClientHandler it : MultiServer.clientlist) { // and broadcasts the message to other active
																		// clients
						it.dos.writeUTF(this.i + " has logged out");
					}
					this.isloggedin = false;
					this.s.close();		//closes the socket with client which has logged out
					break;
				}

				// break the string into message and recipient part
				StringTokenizer st = new StringTokenizer(received, "#");
				String MsgToSend = st.nextToken();
				for (ClientHandler it : MultiServer.clientlist) {
					it.dos.writeUTF(this.name + // sends message to all active clients with the correct timer
							" : " + MsgToSend + "\t");
				}
				String res = "Client:" + i + " " + MsgToSend + "\n";
				// Creating file to store communication among all the active clients
				File yourFile = new File("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\server.txt");		//system path to create a file
				yourFile.createNewFile();																		//creates new file if it doesnt exist
				Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\server.txt"), res.getBytes(),	//write the content to file
						StandardOpenOption.APPEND);

			}

		} catch (IOException e) { // catches all the exceptions for the try block

			e.printStackTrace();
		}

		try {
			// closing resources
			this.dis.close();
			this.dos.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}