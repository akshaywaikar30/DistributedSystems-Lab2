//Name: AkshayMaheshWaikar
//ID: 1001373973
//Mahrsee, Rishabh. “Multi-Threaded Chat Application.” GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-1/.
//Mahrsee, Rishabh. "Multi-Threaded Chat Application." GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-2/.
//https://stackoverflow.com/questions/15247752/gui-client-server-in-java
//http://www.jmarshall.com/easy/http/ HTTP Made Really Easy. 
//Pseudo-code from Chapter 8 of Textbook
//https://docs.oracle.com/javase/8/docs/technotes/guides/lang/Countdown.java
//http://www.java2s.com/Tutorials/Java/java.nio.file/Files/Java_Files_readAllBytes_Path_path_.htm

import java.awt.Frame;		//All the header files are here			
import java.awt.Window;
import java.awt.event.ActionEvent; 			
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Coordinator extends JFrame implements ActionListener

{
	final static int ServerPort = 3214;				//port number for communication
	static Socket s1;					
	static JTextArea txtFromClient;					//declaring variables for displaying messages
	static JTextArea txtdisp;
	JButton sendText;								//declares a send button
	JButton close;									//declares a close button
	static int count=0;
	
	static Timer timer = new Timer();			//creates a timer object
	public Coordinator(){										//it runs the constructor and enables all gui properties														
		this.setTitle("Co-Ordinator");
		this.setSize(1366, 768);										//Creating a Frame for client														
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);																						
		
		txtFromClient = new JTextArea();							// creates a TextArea for client to type messages
		txtFromClient.setBounds(1200, 400, 700, 100);				//This determines size of the TextArea
		add(txtFromClient);											//adds the TextArea to the container
		
		txtdisp = new JTextArea();								// creates TextArea to display messages broadcasted by server
		txtdisp.setBounds(350, 15, 700, 1050);					//This determines size of the TextArea
		txtdisp.setEditable(false);							//Makes the TextArea non editable
		add(txtdisp);											//adds the TextArea to the container
		
		sendText = new JButton("Send");						// creates button for client to send the text which is txtFromClient
		sendText.setBounds(1600, 600, 130, 25);				//This determines size of the TextArea
		sendText.addActionListener(this);					//passes button object to send data after clicking it
		add(sendText);										//adds button to the container
		
		close = new JButton("Close");						// creates button for client to close 
		close.setBounds(1600, 650, 130, 25);				//This determines size of the TextArea
		close.addActionListener(this);					//passes button object to send data after clicking it
		add(close);										//adds button to the container
		
		
		this.setVisible(true);								// makes GUI visible

	}

	@Override
	public void actionPerformed(ActionEvent ae) {					//method gets called on click of send buttons
		if (ae.getSource().equals(sendText)) {						//It checks which button to be called
			try {
				sendMsg();											//calls the sendMsg() function
			} catch (Exception e) {									//Handles exception if any occurred
				e.printStackTrace();
			}
		}
		else if (ae.getSource().equals(close)) {						//It checks which button to be called
			try {										//calls the sendMsg() function
				DataOutputStream dos = new DataOutputStream(s1.getOutputStream());
				dos.writeUTF("Coordinator Terminated");
				System.exit(0);								//terminates coordinator
				
			} catch (Exception e) {									//Handles exception if any occurred
				e.printStackTrace();
			}
		}
	} void sendMsg()  throws UnknownHostException, IOException {
		DataOutputStream dos = new DataOutputStream(s1.getOutputStream());		//sends data across the network and InputStream can accept it on other end
		 String word = txtFromClient.getText().trim();	
		 String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)).toString();		
			 String post="POST HTTP/1.1";
			 String type="application/x-www-form-urlencoded";
			 int length=word.length();												//http format
			 String userAgent="Chat Room";
			 String http="\n"+post+"\n"+"Date:"+date+"\n"+ 
					"Content-Type:"+type+"\n"+ 
					"Content-Length:"+length+"\n"+"User-Agent:"+userAgent+"\n";
		dos.writeUTF(http+word+" Sending vote request\n");				//displays the message from client on to the server with http post method
														 
		txtFromClient.setText("");										//sets the text area empty
	}
	public static void main(String args[]) throws UnknownHostException, IOException 
	{
		new Coordinator();												//Object is called on 
		InetAddress ip = InetAddress.getByName("localhost");					//stores the ip address
		s1 = new Socket(ip, ServerPort);											//creates socket for communication
		File yourFile = new File("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Coordinator.txt");		//path to create a local file
		yourFile.createNewFile();																		//creates file if it doesn't exist
		DataInputStream dis = new DataInputStream(s1.getInputStream());									//reads incoming messages
		Path display = Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2", "Coordinator.txt");
		try {
			byte[] dispArray = Files.readAllBytes(display);												//reads data from local file upon coordinator starting

			String displayString = new String(dispArray, "ISO-8859-1");
			txtdisp.append(displayString);																//displays data on the text area
		} catch (IOException e) {
			System.out.println(e);																			//catches exception if any
		}
		DataOutputStream dos = new DataOutputStream(s1.getOutputStream());//accepts the data from the OutputStream
			String msg = dis.readUTF();	//reads the message from InputStream
			txtdisp.append(msg);							//displays message
			//appends the message to the TextArea 
			int timerVal = 20;			//assigns seconds the timer should run
				
				timer = new Timer();			//creates timer object
				timer.scheduleAtFixedRate(new TimerTask() {			//schedules  task for repeated fixed rate execution
					int i = timerVal;
					public void run() // method is executed to start timer 
					{ 
						String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)).toString();		
					 String post="POST HTTP/1.1";
					 String type="application/x-www-form-urlencoded";		//http format
					
					 String userAgent="Chat Room";
					 String gchttp="\n"+post+"\n"+"Date:"+date+"\n"+ 
							"Content-Type:"+type+"\n"+ 
							"Content-Length:"+13+"\n"+"User-Agent:"+userAgent+"\n";	//http format
					
					 String gahttp="\n"+post+"\n"+"Date:"+date+"\n"+ 
								"Content-Type:"+type+"\n"+ 				
								"Content-Length:"+12+"\n"+"User-Agent:"+userAgent+"\n";
						System.out.println(i--);				//reduces timer by 1 second
						try 
						{		
						DataInputStream dis = new DataInputStream(s1.getInputStream());			//accepts the data from the OutputStream
						String msg = dis.readUTF();						//reads message from input message
						txtdisp.append(msg);
								if(msg.contains("Commit"))			//check is there is commit in msg
									count++;					//increments counter
								
								if(count==3) 	
								{	String gcwrite=gchttp+"\nGlobal Commit\n";
									dos.writeUTF(gcwrite);					//broacasts global commit if it recieves 3 commits
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Coordinator.txt"), "Global Commit\n".getBytes(), StandardOpenOption.APPEND);
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client1.txt"), "Global Commit\n".getBytes(), StandardOpenOption.APPEND);
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client2.txt"), "Global Commit\n".getBytes(), StandardOpenOption.APPEND);
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt"), "Global Commit\n".getBytes(), StandardOpenOption.APPEND);
									timer.cancel();
									timer.purge();
									txtdisp.append(gchttp+"\nGlobal Commit");
									return;
								}
								else if ( msg.contains("Abort") ||i < 0) // checks if msg contains global abort or there is a timeout 
								{	//broadcasts global abort and write in local file
									dos.writeUTF(gahttp+"\nGlobal Abort");
									String gawrite=gchttp+"\nGlobal Abort\n";
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Coordinator.txt"), "Global Abort\n".getBytes(), StandardOpenOption.APPEND);
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client1.txt"), "Global Abort\n".getBytes(), StandardOpenOption.APPEND);
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client2.txt"), "Global Abort\n".getBytes(), StandardOpenOption.APPEND);
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt"), "Global Abort\n".getBytes(), StandardOpenOption.APPEND);
									txtdisp.append(gahttp+"\nGlobal Abort");
									timer.cancel();
									timer.purge();
									return;
								}
							
						}
						catch (IOException e) 
						{											//catches exception if any
							e.printStackTrace();
						}
					}       
				}, 0, 1000);						//runs timer in seconds
			
		}

		}


