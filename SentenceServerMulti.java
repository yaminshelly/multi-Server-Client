package il.ac.kinneret.mjmay.sentenceServerMulti;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A class for a multithreaded sentence server. It receives a sentence, changes it to upper case, counts the letters
 * and returns the modified sentence and its length to the client.
 */
public class SentenceServerMulti {

	private static ServerSocket serverSocket;
	public TextArea area;
	public static String pathUpload;
	public static Logger logger;
	public static FileHandler fh;


	public SentenceServerMulti() {

		JFrame jframServer = new JFrame("Server Frame"); // Declaring the frame object and designing the GUI
		jframServer.setLayout(new BoxLayout(jframServer.getContentPane(), BoxLayout.Y_AXIS));
		jframServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//text
		area=new TextArea();
		area.setBounds(0,0,400,400);
		area.setBackground(Color.LIGHT_GRAY);
		area.setFont(new Font("Ariel", Font.BOLD, 15));
		area.setText("Welcome to Server!!! \n\n");


		//add to fram
		jframServer.add(area);
		jframServer.setSize(400,450);
		jframServer.setLayout(null);
		jframServer.setVisible(true);
	}


	public SentenceServerMulti(int port){

	}

	/**
	 * Gets the user to select an IP address to listen on from the list of local ones.
	 *
	 * @return The selected IP address
	 */
	public static InetAddress selectIPAddress()
	{
		// get the local IPs
		Vector<InetAddress> addresses = getLocalIPs();
		// see how many they are

		System.out.println("Choose an IP address to listen on:");
		for (int i = 0; i < addresses.size(); i++)
		{
			// show it in the list
			System.out.println(i + ": " + addresses.elementAt(i).toString());
		}

		BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
		int choice = -1;

		while ( choice < 0 || choice >= addresses.size())
		{
			System.out.print(": ");
			try {
				String line = brIn.readLine();
				choice = Integer.parseInt(line.trim());
			}
			catch (Exception ex) {
				System.out.print("Error parsing choice\n: ");
			}
		}

		return addresses.elementAt(choice);

	}

	public static Vector<InetAddress> getLocalIPs()
	{
		// make a list of addresses to choose from
		// add in the usual ones
		Vector<InetAddress> adds = new Vector<InetAddress>();
		try {
			adds.add(InetAddress.getByAddress(new byte[] {0, 0, 0, 0}));
			adds.add(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}));
		} catch (UnknownHostException ex) {
			// something is really weird - this should never fail
			System.out.println("Can't find IP address 0.0.0.0: " + ex.getMessage());
			ex.printStackTrace();
			return adds;
		}

		try {
			// get the local IP addresses from the network interface listing
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while ( interfaces.hasMoreElements() )
			{
				NetworkInterface ni = interfaces.nextElement();
				// see if it has an IPv4 address
				Enumeration<InetAddress> addresses =  ni.getInetAddresses();
				while ( addresses.hasMoreElements())
				{
					// go over the addresses and add them
					InetAddress add = addresses.nextElement();
					// make sure it's an IPv4 address
					if (!add.isLoopbackAddress() && add.getClass() == Inet4Address.class)
					{
						adds.addElement(add);
					}
				}
			}
		}
		catch (SocketException ex)
		{
			// can't get local addresses, something's wrong
			System.out.println("Can't get network interface information: " + ex.getLocalizedMessage());
			logger.info("Can't get network interface information: " + ex.getLocalizedMessage());
		}
		return adds;
	}



	/**
	 * Runs the multithreaded sentence server.
	 * @param args The parameters for the server.  Should be two parameters - the IP address and port to listen on.
	 */
	public static void main(String[] args) {

		///log file :
		try {
			logger = Logger.getLogger("myLogServer");
			fh = new FileHandler("C:\\multiServerClient\\SentenceServerMulti\\MyLogFileServer.log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.info("My Log server ");
		}catch (SecurityException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}



		String[] dataArr=new String[2]; //--1)path of sending file , 2)for port address
		try {
			File myObj = new File("C:\\multiServerClient\\SentenceServerMulti\\configureServer.txt");
			Scanner myReader = new Scanner(myObj);
			dataArr[0] = myReader.nextLine(); //path
			dataArr[1] = myReader.nextLine(); //port
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			logger.info("An error occurred.");


			e.printStackTrace();
		}
		int portNum = Integer.parseInt(dataArr[1]);
		pathUpload = dataArr[0];

		InetAddress address = selectIPAddress();

		String line = "";
		Listener listener = null;
	    BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			// open a socket on the port
			ServerSocket serverSock = null;
			//String IP;
			try {
				serverSock = new ServerSocket(portNum,10,address);
				//IP = serverSocket.getInetAddress().toString();
				listener = new Listener(serverSock);
				listener.start(); // Run of listener start
			} catch (IOException e) {

				e.printStackTrace();
			}

			System.out.println(
					address + " Started to listen. "
			);

			logger.info(address + " Started to listen. ");

			try {
				do {
					line = brIn.readLine();
				} while (!line.equalsIgnoreCase("stop"));
				// user asked to stop
				listener.interrupt();
 				serverSock.close();
				logger.info("Stopped listening.\n");

			} catch (IOException ex)
			{
				// this shouldn't happen, just quit
				listener.interrupt();
				break;
			}
		}
		System.out.println("Goodbye.");
		logger.info("Goodbye.\n");
		return;
	}
}
