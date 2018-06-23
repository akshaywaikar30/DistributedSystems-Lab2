//Name: AkshayMaheshWaikar
//ID: 1001373973
//Mahrsee, Rishabh. “Multi-Threaded Chat Application.” GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-1/.
//Mahrsee, Rishabh. "Multi-Threaded Chat Application." GeeksforGeeks, 17 June 2017, www.geeksforgeeks.org/multi-threaded-chat-application-set-2/.
//https://stackoverflow.com/questions/15247752/gui-client-server-in-java
//http://www.jmarshall.com/easy/http/ HTTP Made Really Easy. 
//Pseudo-code from Chapter 8 of Textbook
//https://docs.oracle.com/javase/8/docs/technotes/guides/lang/Countdown.java
//http://www.java2s.com/Tutorials/Java/java.nio.file/Files/Java_Files_readAllBytes_Path_path_.htm

import java.awt.event.ActionEvent; 			//All the header files are here
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Multiclient3 extends JFrame implements ActionListener
{
	final static int ServerPort = 3214;								//Port to connect to server
	static Socket s;												//Socket for communicating messages
	static String msg;												//To recieve message
	static Timer timer = new Timer();								//object for timer
	static JTextArea txtFromClient;									//to type messages on client
	static JTextArea txtdisp;										//to display messages on client
	JButton sendText;												//to send message
	static int count; 												//to detect count of messages
	JButton abortbtn;												//abort button
	JButton commitbtn;												//commit button

	
	public Multiclient3(){										//it runs the constructor and enables all gui properties														
		this.setTitle("Client3");																								
		this.setSize(1366, 768);										//Creating a Frame for client														
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);																			
		getContentPane().setLayout(null);																						

		txtFromClient = new JTextArea();							// creates a TextArea for client to type messages
		txtFromClient.setBounds(1200, 200, 650, 100);				//This determines size of the TextArea
		add(txtFromClient);											//adds the TextArea to the container
		
		txtdisp = new JTextArea();								// creates TextArea to display messages broadcasted by server
		txtdisp.setBounds(350, 15, 700, 950);					//This determines size of the TextArea
		txtdisp.setEditable(false);							//Makes the TextArea non editable
		add(txtdisp);											//adds the TextArea to the container

		sendText = new JButton("Send");						// creates button for client to send the text which is txtFromClient
		sendText.setBounds(1300, 350, 130, 25);				//This determines size of the TextArea
		sendText.addActionListener(this);					//passes button object to send data after clicking it
		add(sendText);										//adds button to the container
		
		commitbtn=new JButton("Commit");					// creates button for client to send the commit    	
		commitbtn.setBounds(1300, 400, 130, 25);			//This determines size of the Commit
		commitbtn.addActionListener(this);					//passes button object after clicking it
		add(commitbtn);										//adds button to the container
		
		abortbtn=new JButton("Abort");						//creates button for client to send the Abort  
		abortbtn.setBounds(1300, 450, 130, 25);				//This determines size of the Abort
		abortbtn.addActionListener(this);					//passes button object after clicking it
		add(abortbtn);										//adds button to the container

		this.setVisible(true);	// makes GUI visible
	}

	@Override
	public void actionPerformed(ActionEvent ae) {					//method gets called on click of send button
		if (ae.getSource().equals(sendText)) {						//It checks which button to be called
			try {
				sendMsg();											//calls the sendMsg() function
			} catch (Exception e) {									//Handles exception if any occurred
				e.printStackTrace();
			}
		}
		else if (ae.getSource().equals(commitbtn)) {					//method gets called on click of commit button
															
			try {										
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());		//sends message across the network
				String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)).toString();		//to get current date and time
				 String post="POST HTTP/1.1";									//http request
				 String type="application/x-www-form-urlencoded";					
				 String userAgent="Chat Room";
				 String chttp="\n"+post+"\n"+"Date:"+date+"\n"+ 
						"Content-Type:"+type+"\n"+ 
						"Content-Length:"+6+"\n"+"User-Agent:"+userAgent+"\n";
				dos.writeUTF(chttp+"\nCommit\n");									//sends commit in http format
				Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt"), "Vote Commit\n".getBytes(), StandardOpenOption.APPEND);	//writes in file
				//write local commit to file
				
			} catch (Exception e) {									//Handles exception if any occurred
				e.printStackTrace();
			}
		}
		else if (ae.getSource().equals(abortbtn)) {						//It checks which button to be called
			try {										
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());	//sends message across the network
				String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)).toString();		//to get current date and time
				 String post="POST HTTP/1.1";							
				 String type="application/x-www-form-urlencoded";				
				 String userAgent="Chat Room";								//http request
				 String ahttp="\n"+post+"\n"+"Date:"+date+"\n"+ 
						"Content-Type:"+type+"\n"+ 
						"Content-Length:"+5+"\n"+"User-Agent:"+userAgent+"\n";
				dos.writeUTF(ahttp+"\nAbort\n");					//sends abort in http format
				Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt"), "Vote Abort\n".getBytes(), StandardOpenOption.APPEND);
			} catch (Exception e) {									//Handles exception if any occurred
				e.printStackTrace();
			}
		}
	}
	public void sendMsg()  throws UnknownHostException, IOException {
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());		//sends data across the network and InputStream can accept it on other end
		String word = txtFromClient.getText().trim();							//gets the data from the text area and assigns to word variable
		 String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)).toString();	//to get current date and time	
		 String post="POST HTTP/1.1";
		 String type="application/x-www-form-urlencoded";
		 int length=word.length();										//http request
		 String userAgent="Chat Room";
		 String http="\n"+post+"\n"+"Date:"+date+"\n"+ 
				"Content-Type:"+type+"\n"+ 
				"Content-Length:"+length+"\n"+"User-Agent:"+userAgent+"\n";
		dos.writeUTF(http+word);														// read word from the client and write to server
		txtFromClient.setText(" ");														//sets the text field empty
	}

	public static void main(String args[]) throws UnknownHostException, IOException 		//executes the main function
	{	
		new Multiclient3();//Object is called on 
		Path display = Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2", "Client3.txt");			//reads file upon client starting
		try {
			byte[] dispArray = Files.readAllBytes(display);

			String displayString = new String(dispArray, "ISO-8859-1");
			txtdisp.append(displayString);
		} catch (IOException e) {
			System.out.println(e);
		}
		File yourFile = new File("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt");		//path to create file
		yourFile.createNewFile();																	//creates new file
		InetAddress ip = InetAddress.getByName("localhost");										//gets the ip address
		s = new Socket(ip, ServerPort);																//allocates the ip address
		
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());		//sends data across the network and InputStream can accept it on other end					
		while(true) {
			DataInputStream dis = new DataInputStream(s.getInputStream());		//accepts the data from the OutputStream
			msg = dis.readUTF();													//reads the messages from inputstream
			if(msg.contains("Sending vote request")) {
				String store=msg.substring(145);
				
				Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt"), store.getBytes(), StandardOpenOption.APPEND);		//writes in to local file
			}
		
			
			txtdisp.append(msg);				//appends the message to the TextArea 
			
			if(msg.contains("Coordinator Terminated")) {				//checks if the msg contains terminates of coordinator
				timer = new Timer();										//creates new timer object
			}
			if(msg.contains(" Sending vote request") || msg.contains("Coordinator Terminated") || msg.contains("Decision_Request")) {			//checks if message has certain messages
				int timerVal = 20;										//assigns seconds the timer should run
				timer.scheduleAtFixedRate(new TimerTask() {			//schedules  task for repeated fixed rate execution
					int i = timerVal;									
					public void run() {									// method is executed to start timer 
						System.out.println(i--);						//decrements timer by 1 second
						if(msg.contains("Global Commit") || msg.contains("Global Abort")) {		//checks if message contains given messages
							
							timer.cancel();			//cancels time
							timer.purge();			//cancels time

							return;
						}
						if(msg.contains("Coordinator Terminated")||i<0) {		//executes when coordinator is terminated or timer is finished
							try {
								if(i<0) {
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt"), "abort\n".getBytes(), StandardOpenOption.APPEND);	//writes to file if timer is over
								}
							String decision="Decision_Request\n";
							
							String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)).toString();		
							 String post="POST HTTP/1.1";
							 String type="application/x-www-form-urlencoded";		//http format
							 String userAgent="Chat Room";
							 String drhttp="\n"+post+"\n"+"Date:"+date+"\n"+ 
									"Content-Type:"+type+"\n"+ 
									"Content-Length:"+16+"\n"+"User-Agent:"+userAgent+"\n";
								dos.writeUTF(drhttp+"Decision_Request\n");
								Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt"),"Decision_Request\n".getBytes(), StandardOpenOption.APPEND);	//writes decision to local file
								txtdisp.append(decision);			//appends the message to the TextArea 
								
								DataInputStream dis = new DataInputStream(s.getInputStream());			//accepts the data from the OutputStream
								String dec= dis.readUTF();												
								
								Thread.sleep(2000);
								
								 String gchttp="\n"+post+"\n"+"Date:"+date+"\n"+ 				
										"Content-Type:"+type+"\n"+ 
										"Content-Length:"+13+"\n"+"User-Agent:"+userAgent+"\n";				//http format
								 String gahttp="\n"+post+"\n"+"Date:"+date+"\n"+ 	
											"Content-Type:"+type+"\n"+ 
											"Content-Length:"+12+"\n"+"User-Agent:"+userAgent+"\n";
								if(dec.contains("Global Commit")||dec.contains("Commit")) {	//checks if the message contains commit to global commit
									String gcwrite=gchttp+"Global Commit";
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt"), "Global Commit\n".getBytes(), StandardOpenOption.APPEND); // writes to local file
									dos.writeUTF(gcwrite);			
									
								}
								else if(dec.contains("Abort")||dec.contains("Global Abort")) {		//checks if the message contains Abort to global Abort
									dos.writeUTF(gahttp+"Global Abort");			
									String gawrite=gahttp+"Global Abort\n";
									Files.write(Paths.get("C:\\Users\\aksha\\eclipse-workspace\\DLab2\\Client3.txt"), "Global Abort\n".getBytes(), StandardOpenOption.APPEND);	//writes to local file
								}
							}

							catch (IOException | InterruptedException e) {		//catches exception if any
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							timer.cancel();						//cancels timer
							timer.purge();
							return;
						}
					}
					},0,1000);				//runs the timer by seconds
			}
		}
	}
}


