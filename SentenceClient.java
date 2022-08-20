package il.ac.kinneret.mjmay.sentenceClient;// Importing the required packages
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

import java.net.SocketException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.*;


class MultipleClient extends JPanel implements ActionListener {

	//Buttons according to the defined tasks
	private static JButton btn_Browse, btn_Upload, btn_Downlowd, btn_List, btn_disconnect , btn_lockFile , btn_UnlockFile, btn_GetVersion , btn_lockForAllServer , btn_unlockForAll, bth_uploadForAll , btn_VersionOfAllServers; // Declaring Button variables.
	 private  static JTextPane version_text ;
	 private static JComboBox<String> ComboBoxListOfFiles; //  combobox variable for the List of file that upload to the server .
	 private static JComboBox<String> ComboBoxNamesOfServer; // combobox variable -> to choose the server.
	 private static JTextArea jTextArea; //  textarea variable for write Remarks on the form.
	 private static JFileChooser jFileChooser; // filechooser variable --> for showOpenDialog function and getSelectedFile func .

	public static Logger logger;
	public static FileHandler fh;


	//More variable :
	private static int i;
	 private static int PORT;
	private static String nameOfServerStr ;
	 private static String IP;
	final File[] fileToSend;

	//date format for http
	static String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.systemDefault())).toString();

	public MultipleClient() throws IOException {
		super(new BorderLayout());


		IP= "0.0.0.0";
		PORT = 1234 ;
		String nameOfServer ="";

		// TODO:Check if the uplaod good with that array of file .
		fileToSend = new File[1];

		//Create the Text area object
		jTextArea = new JTextArea("Welcome to the client : \n " , 20, 20);
		logger.info("Welcome to the client : \n");

		jTextArea.setEditable(false);
		jTextArea.setFont((new Font("Ariel", Font.BOLD, 12)));
		jTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
		JScrollPane logScrollPane = new JScrollPane(jTextArea);

		//Create a file chooser
		jFileChooser = new JFileChooser();//--> filechooser variable --> for showOpenDialog function and getSelectedFile func .

		// Bottons and adding listener to the buttons--Later in the code we will see the events-- on method actionPerformed
		btn_Browse = new JButton("Browse");
		btn_Browse.addActionListener(this);
		btn_Upload = new JButton("Upload");
		btn_Upload.addActionListener(this);
		btn_List = new JButton("List");
		btn_List.addActionListener(this);
		btn_Downlowd = new JButton("Download");
		btn_Downlowd.addActionListener(this);
		btn_disconnect = new JButton("Disconnect");
		btn_disconnect.addActionListener(this);
		btn_lockFile= new JButton("Lock ");
		btn_lockFile.addActionListener(this);
		btn_UnlockFile= new JButton("UnLock");
		btn_UnlockFile.addActionListener(this);
		btn_GetVersion = new JButton("Version");
		btn_GetVersion.addActionListener(this);
		version_text = new JTextPane();
		btn_lockForAllServer = new JButton("Lock All");
		btn_lockForAllServer.addActionListener(this);
		btn_unlockForAll = new JButton("Unlock all");
		btn_unlockForAll.addActionListener(this);
		bth_uploadForAll= new JButton("Upload to all");
		bth_uploadForAll.addActionListener(this);
		btn_VersionOfAllServers = new JButton("version all");
		btn_VersionOfAllServers.addActionListener(this);




		//We need two Combo Box ,1 for the list of file on specific server ( that we could downlowd it from the server)
		//and one for the server name that we have on file configuration .

		//whe check it on the class on 5 computers :
		ComboBoxNamesOfServer = new JComboBox<String>();
		ComboBoxNamesOfServer.addItem("serverOne");
		ComboBoxNamesOfServer.addItem("serverTwo");
		ComboBoxNamesOfServer.addItem("serverThree");
		ComboBoxNamesOfServer.addItem("localhost");

		ComboBoxListOfFiles = new JComboBox<String>(); //Combo box to the list of file, full when enter on list ( if we have file on the folder ) .
		// .
		//Panel for the Layout :
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.GRAY);
		buttonPanel.setBounds(0,0,250,250);



		//add button to the panel .
		buttonPanel.add(ComboBoxNamesOfServer);
		buttonPanel.add(btn_Browse);
		buttonPanel.add(btn_Upload);
		buttonPanel.add(btn_List);
		buttonPanel.add(ComboBoxListOfFiles);
		buttonPanel.add(btn_Downlowd);
		buttonPanel.add(btn_lockFile);
		buttonPanel.add(btn_UnlockFile);
		buttonPanel.add(btn_disconnect);
		buttonPanel.add(btn_GetVersion);
		buttonPanel.add(version_text);
		buttonPanel.add((btn_lockForAllServer));
		buttonPanel.add((btn_unlockForAll));
		buttonPanel.add(bth_uploadForAll);
		buttonPanel.add(btn_VersionOfAllServers);


		//Add the buttons and the log(textarea) to this panel.
		add(buttonPanel, BorderLayout.SOUTH);
		add(logScrollPane, BorderLayout.CENTER); //panel for the text area
	}


	//2.1
	/*
	Function that move on file configuration (csv ) with :name of server ; Ip ; Port
	 */





	// Defining methods for the actions of the button declared above

	public void actionPerformed(ActionEvent e) {

		//Browse event :
		if (e.getSource() == btn_Browse) {
			int fileChooseFromDialog  =jFileChooser.showOpenDialog(MultipleClient.this);

			if (fileChooseFromDialog == JFileChooser.APPROVE_OPTION) {
				File file = jFileChooser.getSelectedFile();
				jTextArea.append("You choose the file :  " + file.getName() + ".\n" );
				logger.info("You choose the file :  " + file.getName() + ".\n");
			}
		}

		//Upload clicked event
		else if (e.getSource() == btn_Upload) {
			try {
				SendFileToTheServer();
			} catch (Exception error) {
				error.printStackTrace();
			}
		}

		//List Button clicked
		else if (e.getSource() == btn_List) {
			try {
				listOfFiles();
			} catch (Exception error) {
				error.printStackTrace();
			}
		}

		//Downloud clicked event
		else if (e.getSource() == btn_Downlowd) {
			try {
				downloadFile();
			} catch (Exception error) {
				error.printStackTrace();
			}
		}

		//lock clicked event
		else if(e.getSource() == btn_lockFile){
			try{
				Lock();
			}catch (Exception error){
				error.printStackTrace();
			}
		}


		//unlock clicked event
		else if(e.getSource() == btn_UnlockFile){
			try{
				UNLock();
			}catch (Exception error){
				error.printStackTrace();
			}
		}

		//Disconnect clicked event
		else if(e.getSource() == btn_GetVersion){
			try{
				getVersion();
			}catch (Exception error){
				error.printStackTrace();
			}
		}

		//get version for all clicked event :
		else if(e.getSource() == btn_VersionOfAllServers){
			try{
				grtVersionFromAll();
			}catch (Exception error){
				error.printStackTrace();
			}
		}

		//upload to all server clicked event :
		else if(e.getSource() == bth_uploadForAll){
			try{
				UploadForAllServer();
			}catch (Exception error){
				error.printStackTrace();
			}
		}

		//Unlock all clicked event
		else if(e.getSource() == btn_unlockForAll){
			try{
				UNlockForAllServer();
			}catch (Exception error){
				error.printStackTrace();
			}
		}

		//lock all clicked event
		else if(e.getSource() == btn_lockForAllServer){
			try{
				lockForAllServer();
			}catch (Exception error){
				error.printStackTrace();
			}
		}

		//Disconnect clicked event
		else if(e.getSource() == btn_disconnect){
			try{
				Disconnect();
			}catch (Exception error){
				error.printStackTrace();
			}
		}
	}



	/*
UPLOAD
    This function starts when you press the Upload button,
    This is where the connection to the remote server by socket takes place.
    You must select a server name before uploading , in case the connection did not occur properly.
    that function call to the server function  - save file ' to save the file that send here .
 */
	public void SendFileToTheServer() throws IOException {

		try {
			listOfServers();//Update the IP according to the selected server
			Socket socket = new Socket(IP, PORT); // socket connection to the server

			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());

			File file = jFileChooser.getSelectedFile();  // getting the selected file to upload to server
			String fileName = file.getName();
			outToServer.writeBytes("UPLOAD\n"); //send a msg to the server
			int fileSize = (int) file.length();
			OutputStream os = socket.getOutputStream();
			PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			Scanner in = new Scanner(socket.getInputStream());

			pr.println(fileName);
			pr.println(fileSize);
			byte[] buffer = new byte[fileSize]; // Defining the size of the buffer
			bis.read(buffer, 0, buffer.length);
			os.write(buffer, 0, buffer.length);
			// System.out.println(in.nextLine());
			os.flush();

			jTextArea.append("The connection to :\n IP : " + IP + ", PORT : " + PORT + " was successful! \n ---------------\n ");
			logger.info("The connection to :\n IP : " + IP + ", PORT : " + PORT + " was successful! \n ---------------\n ");


			DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); // Initializing data I/O streams
			FileInputStream fis = new FileInputStream(file);

			Scanner scanner = new Scanner(socket.getInputStream());
			if (scanner.nextLine().equals("OK")) {
				jTextArea.append("File " + file.getName() + " uploaded successfully\n ---------------\n"); // Message to client that the  file is uploaded successfully
				logger.info("File " + file.getName() + " uploaded successfully\n ---------------\n ");
			} else {
				jTextArea.append("File " + file.getName() + " NOT uploaded  \n ---------------\n");
				logger.info("File " + file.getName() + " NOT uploaded  \n ---------------\n");
			}

			dos.writeUTF(file.getName());
			while (fis.read(buffer) > 0) {
				dos.write(buffer);
			}

			dos.flush();
		}catch (SocketException e) {
			e.printStackTrace();
			jTextArea.append("\nUpload : not connect to server :" + IP);
			logger.info("\nUpload : not connect to server :" +IP);
		}
	}


	/*
   This function passes the fileconfiguration.csv rows and matches the selected server name with the corresponding ip and port in the file.
   Finally updates the variables - IP , PORT in the socket .
    */
	private static void listOfServers () throws IOException{

		String CSVpath= "C:\\multiServerClient\\SentenceServerMulti\\FileConfigurationCsv.csv";
		BufferedReader bufferedReader =null;

		String line = "";
		try{
			bufferedReader =new BufferedReader(new FileReader(CSVpath));
			while ((line=bufferedReader.readLine()) != null ) {
				String[] row = line.split(","); //---> serverOne | Port : 1234 | Ip 127.0.0.1
				String[] NamePortIpArr = new String[3];

				//this for saved the name of server in the comboBox namesOfServer .
				for (int i = 0; i < 3; i++) {
					NamePortIpArr[i] = row[i];
				}
				nameOfServerStr= (String) ComboBoxNamesOfServer.getSelectedItem();
				if(NamePortIpArr[0].equals(nameOfServerStr)){
					PORT= Integer.parseInt(NamePortIpArr[1])  ;
					IP = NamePortIpArr[2];
				}
			}
			//jTextArea.append("You choose the Server : "+nameOfServerStr +" | IP : "+ IP +" |PORT :" + PORT +" \n ---------------\n"); // Message to client that the  file is uploaded successfully
			//logger.info("You choose the Server : "+nameOfServerStr +" | IP : "+ IP +" |PORT :" + PORT +" \n ---------------\n");

		}catch (Exception e){

		}

	}




	/*
    This function overrides the files in the uploadFile folder - the folder where the files uploaded to the server are saved.
     (See code in FileServer) and saves them in the comboBox of the list
     */
	public void listOfFiles() throws IOException {

		try {
			listOfServers();
			Socket socket = new Socket(IP, PORT);
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			outToServer.writeBytes("LIST\n");
			BufferedReader ListOfFilesFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = null;
			ComboBoxListOfFiles.removeAllItems();//we need to remove all the items on the comboBox- to approve the file in the folder yet .
			jTextArea.append("ListOfServer : \n ");
			logger.info("ListOfServer : \n ");
			while ((line = ListOfFilesFromServer.readLine()) != null) {
				jTextArea.append(line + " ");
				logger.info(line + " ");
				ComboBoxListOfFiles.addItem(line);
			}
		}catch (SocketException e){
			e.printStackTrace();
			jTextArea.append("\nLIST : not connect to server :" +IP);
			logger.info("\nLIST : not connect to server :" +IP);
		}
	}


	public void getVersion() throws IOException {
		try {
			listOfServers();
			Socket socket = new Socket(IP, PORT);
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			Scanner scanner = new Scanner(socket.getInputStream());
			String filename = (String) ComboBoxListOfFiles.getSelectedItem();
			String FileNameOnVersion = version_text.getText();
			outToServer.writeBytes("GETVERSION\n");

			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.println(FileNameOnVersion); // send a msg to the server with the file name

			//msgFromServer SCANNER - THE SERVER MSG GOING TO BE "OK" OR "ERROR"
			String msgFromServer = scanner.nextLine();  //ok or error msg
			String digestAnddate = scanner.nextLine(); //the degit and time from the server

			switch (msgFromServer) {
				case "OK":
					jTextArea.append("Successful version a file : " + FileNameOnVersion + "\n the version is : " + digestAnddate + "\n---------------\n");
					logger.info("Successful version a file : " + FileNameOnVersion + "\n the version is : " + digestAnddate + "\n---------------\n");
					break;
				case "ERROR":
					jTextArea.append("Failed get version a file : " + FileNameOnVersion + ", it doesnt exist in server ! " + "\n---------------\n");
					logger.info("Failed get version a file : " + FileNameOnVersion + ", it doesnt exist in server ! " + "\n---------------\n");
					break;
				default:
					jTextArea.append("version function Not good \n");
					logger.info("version function Not good \n");
					break;

			}

		}catch (SocketException e){
			e.printStackTrace();
			jTextArea.append("\nVERSION :not connect to server !"+IP);
			logger.info("\nVERSION : not connect to server "+IP);
		}
	}

	public void UNLock() throws IOException {
		try {
			//connection
			listOfServers();
			Socket socket = new Socket(IP, PORT);

			//get the  name of file from gui
			String selectedBook = (String) ComboBoxListOfFiles.getSelectedItem();

			//tell the server that we want to lock  a file from him- that move to the LOCK case in server(HandlerClientThread)
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			outToServer.writeBytes("UNLOCK\n");

			//Scanner to get information from the server
			Scanner scanner = new Scanner(socket.getInputStream());

			//print writer to erite to the server .
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.println(selectedBook);

			//msgFromServer SCANNER - THE SERVER MSG GOING TO BE "OK" OR "ERROR"
			String msgFromServer = scanner.nextLine();
			switch (msgFromServer) {
				case "OK":
					jTextArea.append("Successful UNLock a file : " + selectedBook + "\n");
					logger.info("Successful UNLock a file : " + selectedBook + "\n");
					break;
				case "ERROR":
					jTextArea.append("Failed UNLock a file : " + selectedBook + "\n");
					logger.info("Failed UNLock a file : " + selectedBook + "\n");
					break;
				default:
					jTextArea.append("UNLock function Not good \n");
					logger.info("UNLock function Not good \n");
					break;

			}
		}catch (SocketException e) {
			e.printStackTrace();
			jTextArea.append("\nUNLOCK: not connect to server :"+IP);
			logger.info("\nUNLOCK: not connect to server :"+IP);
		}

	}

	public void Lock() throws IOException {
		try {
			//connection
			listOfServers(); //CHOOSE SERVER FROM THE LIST
			Socket socket = new Socket(IP, PORT); //CONNECTION

			//tell the server that we want to lock  a file from him- that move to the LOCK case in server(HandlerClientThread)
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			outToServer.writeBytes("LOCK\n");

			//get the  name of file from gui
			String selectedBook = (String) ComboBoxListOfFiles.getSelectedItem(); //THE FILE THE USER SELECT TO LOCK
			//print writer to erite to the server .
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.println(selectedBook); //THE NAME OF THE FILE TO THE SERVER


			//Scanner to get information from the server
			Scanner scanner = new Scanner(socket.getInputStream());


			String msgFromServer = scanner.nextLine();
			switch (msgFromServer) {
				case "OK":
					jTextArea.append("Successful Lock a file : " + selectedBook + "\n");
					logger.info("Successful Lock a file : " + selectedBook + "\n");
					break;
				case "ERROR":
					jTextArea.append("Failed Lock a file : " + selectedBook + "\n");
					logger.info("Failed Lock a file : " + selectedBook + "\n");
					break;
				default:
					jTextArea.append("Lock function Not good \n");
					logger.info("Lock function Not good \n");
					break;

			}
		}catch (SocketException e) {
			e.printStackTrace();
			jTextArea.append("\nLOCK :not connect to server :"+IP);
			logger.info("\nLOCK :not connect to server :"+IP);
		}
	}


	// Function to download files to the server
	public void downloadFile() throws IOException {
	try {
		//connection
		listOfServers();
		Socket socket = new Socket(IP, PORT);

		//get the  name of file from gui
		String selectedBook = (String) ComboBoxListOfFiles.getSelectedItem();

		//tell the server that we want to download from him- that move to the DOWNLOAD case in server(HandlerClientThread)
		DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
		outToServer.writeBytes("DOWNLOAD\n");


		Scanner scanner = new Scanner(socket.getInputStream());


		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
		pw.println(selectedBook);
		//file from the server  download here .
		//save the file to folder
		String path = scanner.nextLine();
		int FileSize = scanner.nextInt();

		BufferedOutputStream bos;

		//path to save the file should be here- need to be in gui (the saving ) .
		try (FileOutputStream fos = new FileOutputStream("C:\\multiServerClient\\SentenceClient\\Downloadfiles\\" + selectedBook)) {
			bos = new BufferedOutputStream(fos);
			byte[] buffer = new byte[FileSize];
			fos.write(buffer);
		}

		System.out.println("Incoming File: " + selectedBook);
		System.out.println("Size: " + FileSize + "Byte");
		// if(FileSize ==is.read(buffer , 0 , buffer.length) )System.out.println("File is verified");
		//  else System.out.println("File is corrupted. File Recieved " + is.read(buffer , 0 , buffer.length) + " Byte");
		bos.close();
		logger.info("\nThe file download succesfully! ");
		jTextArea.append("\nThe file download succesfully! ");
		//logger.info("The download not succesfully! ");
		//jTextArea.append("The file download not succesfully! ");
	}catch (SocketException e) {
		e.printStackTrace();
		jTextArea.append("\nDOWNLOAD :not connect to server :"+IP);
		logger.info("\nDOWNLOAD: not connect to server :"+IP);
		}
	}



	/**
	 * Create the GUI
	 */
	private static void createAndShowGUI() throws IOException {


		JFrame frame = new JFrame("Client");
		frame.setSize(500,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//the main Panel.
		JComponent newContentPane = new MultipleClient();
		frame.setContentPane(newContentPane);


		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}




	/*
	This function starts when you press the disconnect button, it exits the program.
	 */
	private void Disconnect() throws Exception {
		System.exit(0);
	}





//This function upload file for all the server in the list

public void UploadForAllServer() throws IOException{

	String CSVpath= "C:\\multiServerClient\\SentenceServerMulti\\FileConfigurationCsv.csv";
	BufferedReader bufferedReader =null;

	String line = "";
	try{
		bufferedReader =new BufferedReader(new FileReader(CSVpath));
		bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				try {
				String[] row = line.split(","); //---> serverOne | Port : 1234 | Ip 127.0.0.1
				PORT = Integer.parseInt(row[1]);
				IP = row[2];

				Socket socket = new Socket(IP, PORT); // socket connection to the server
				jTextArea.append("\nConnect to server :  " + IP + "\n");

				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				File file = jFileChooser.getSelectedFile();  // getting the selected file to upload to server
				String fileName = file.getName();
				outToServer.writeBytes("UPLOAD\n");
				int FileSize = (int) file.length();
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				pw.println(fileName); // send a msg to the server with the file name
				pw.println(FileSize);//send the ile size to the server
				BufferedInputStream bis = new BufferedInputStream((new FileInputStream(file)));
				OutputStream os = socket.getOutputStream();
				Scanner scanner = new Scanner(socket.getInputStream());

				byte[] buffer = new byte[FileSize];
				bis.read(buffer, 0, buffer.length);
				os.write(buffer, 0, buffer.length);
				os.flush();


				//upload message :
				//msgFromServer SCANNER - THE SERVER MSG GOING TO BE "OK" OR "ERROR"
				String msgFromServer = scanner.nextLine();
				switch (msgFromServer) {
					case "OK":
						jTextArea.append("\nSuccessful UPLOAD a file : " + file);
						logger.info("\nSuccessful UPLOAD a file : " + file);
						break;
					case "ERROR":
						jTextArea.append("\nFailed get UPLOAD a file : " + file + "\n");
						logger.info("\nFailed get UPLOAD a file : " + file + "\n");
						break;
					default:
						jTextArea.append("\nUPLOAD function Not good \n");
						logger.info("\nUPLOAD function Not good \n");
						break;
				}
			}catch (SocketException e1){
					e1.printStackTrace();
					jTextArea.append("\nUPLOAD :not connect to server :"+IP+"\n");
					logger.info("\nUPLOAD: not connect to server :"+IP+"\n");
				}
		}

	}catch (Exception e) {}
}




	//THIS FUNCTION UNLOCKED THE FILE FOR ALL SERVER
	//THIS FUNCTION locked file to all server - if the file exist there
	public void UNlockForAllServer() throws IOException{

		String CSVpath= "C:\\multiServerClient\\SentenceServerMulti\\FileConfigurationCsv.csv";
		BufferedReader bufferedReader =null;

		String line = "";
		try{
			bufferedReader =new BufferedReader(new FileReader(CSVpath));
			bufferedReader.readLine();

				while ((line = bufferedReader.readLine()) != null) {
					try {
					String[] row = line.split(","); //---> serverOne | Port : 1234 | Ip 127.0.0.1
					PORT = Integer.parseInt(row[1]);
					IP = row[2];

					Socket socket = new Socket(IP, PORT); // socket connection to the server
					jTextArea.append("\nConnect to server " + IP + "\n");
					jTextArea.append("\nConnect to server " + IP + "\n");

					DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
					File file = jFileChooser.getSelectedFile();  // getting the selected file to upload to server
					String fileName = file.getName();
					outToServer.writeBytes("UNLOCK\n");
					PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
					pw.println(fileName); // send a msg to the server with the file name
					Scanner scanner = new Scanner(socket.getInputStream());


					//locks message :
					//msgFromServer SCANNER - THE SERVER MSG GOING TO BE "OK" OR "ERROR"
					String msgFromServer = scanner.nextLine();
					switch (msgFromServer) {
						case "OK":
							jTextArea.append("\nSuccessful UNLOCK a file : " + file);
							logger.info("\nSuccessful UNLOCK a file : " + file);
							break;
						case "ERROR":
							jTextArea.append("\nFailed get UNLOCK a file : " + file + "\n");
							logger.info("\nFailed get UNLOCK a file : " + file + "\n");
							break;
						default:
							jTextArea.append("\nUNLOCK function Not good \n");
							logger.info("\nUNLOCK function Not good \n");
							break;
					}

				}catch (SocketException e1){
					e1.printStackTrace();
					jTextArea.append("\nUNLOCK :not connect to server :"+IP+"\n");
					logger.info("\nUNLOCK: not connect to server :"+IP+"\n");
				}
				}


		}catch (Exception e) {}
	}





	//this function get version of file from all server :

	public void grtVersionFromAll() throws IOException {
		String CSVpath = "C:\\multiServerClient\\SentenceServerMulti\\FileConfigurationCsv.csv";
		BufferedReader bufferedReader = null;

		String line = "";
		try {
			bufferedReader = new BufferedReader(new FileReader(CSVpath));
			bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				try {
					String[] row = line.split(","); //---> serverOne | Port : 1234 | Ip 127.0.0.1
					PORT = Integer.parseInt(row[1]);
					IP = row[2];
					Socket socket = new Socket(IP, PORT); // socket connection to the server

					DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
					Scanner scanner = new Scanner(socket.getInputStream());
					String filename = (String) ComboBoxListOfFiles.getSelectedItem();
					String FileNameOnVersion = version_text.getText();
					outToServer.writeBytes("GETVERSION\n");

					PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
					pw.println(FileNameOnVersion); // send a msg to the server with the file name

					//msgFromServer SCANNER - THE SERVER MSG GOING TO BE "OK" OR "ERROR"
					String msgFromServer = scanner.nextLine();
					String digestAnddate = scanner.nextLine();

					switch (msgFromServer) {
						case "OK":
							jTextArea.append("\nAddress : " + IP + ":  " + PORT + " \nSuccessful version a file : " + FileNameOnVersion + "\n the version is : " + digestAnddate + "\n");
							logger.info("\nAddress : " + IP + ":  " + PORT + " \nSuccessful version a file : " + FileNameOnVersion + "\n the version is : " + digestAnddate + "\n");
							break;
						case "ERROR":
							jTextArea.append("\nAddress : " + IP + ":  " + PORT + "Failed get version a file : " + FileNameOnVersion + "it doesnt exist in server ! " + "\n");
							logger.info("\nAddress : " + IP + ":  " + PORT + "Failed get version a file : " + FileNameOnVersion + "it doesnt exist in server ! " + "\n");
							break;
						default:
							jTextArea.append("\nversion function Not good \n");
							logger.info("\nversion function Not good \n");
							break;
					}


				} catch (SocketException e1) {
					e1.printStackTrace();
					jTextArea.append("\nVERSION :not connect to server :" + IP+"\n");
					logger.info("\nVERSION : not connect to server :" + IP+"\n");
				}
			}
		} catch (IOException E) {
		}
	}





	//THIS FUNCTION locked file to all server - if the file exist there
	public void lockForAllServer() throws IOException {

		String CSVpath = "C:\\multiServerClient\\SentenceServerMulti\\FileConfigurationCsv.csv";
		BufferedReader bufferedReader = null;

		String line = "";
		try {
			bufferedReader = new BufferedReader(new FileReader(CSVpath));
			bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				try {

					String[] row = line.split(","); //---> serverOne | Port : 1234 | Ip 127.0.0.1
					PORT = Integer.parseInt(row[1]);
					IP = row[2];

					Socket socket = new Socket(IP, PORT); // socket connection to the server
					jTextArea.append("\nConnect to server " + IP + "\n");

					//tell the server that we want to lock  a file from him- that move to the LOCK case in server(HandlerClientThread)
					DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
					outToServer.writeBytes("LOCK\n");

					//get the  name of file from gui
					String selectedBook = (String) ComboBoxListOfFiles.getSelectedItem(); //THE FILE THE USER SELECT TO LOCK
					//print writer to erite to the server .
					PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
					pw.println(selectedBook); //THE NAME OF THE FILE TO THE SERVER


					//Scanner to get information from the server
					Scanner scanner = new Scanner(socket.getInputStream());


					String msgFromServer = scanner.nextLine();
					switch (msgFromServer) {
						case "OK":
							jTextArea.append("Successful Lock a file : " + selectedBook + "\n");
							logger.info("Successful Lock a file : " + selectedBook + "\n");
							break;
						case "ERROR":
							jTextArea.append("Failed Lock a file : " + selectedBook + "\n");
							logger.info("Failed Lock a file : " + selectedBook + "\n");
							break;
						default:
							jTextArea.append("Lock function Not good \n");
							logger.info("Lock function Not good \n");
							break;
					}

				} catch (SocketException e1) {
					e1.printStackTrace();
					jTextArea.append("\n LOCK :not connect to server :" + IP +"\n");
					logger.info("\n LOCK : not connect to server :" + IP+"\n");
				}

			}
		} catch (Exception e) {
		}
	}


	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				try {
					logger = Logger.getLogger("myLogClient");
					fh = new FileHandler("C:\\multiServerClient\\SentenceClient\\MyLogFileClient.log");
					logger.addHandler(fh);
					SimpleFormatter formatter = new SimpleFormatter();
					fh.setFormatter(formatter);
					logger.info("My Log client :\n");
				}catch (SecurityException e){
					e.printStackTrace();
				}
				catch (IOException e){
					e.printStackTrace();
				}


				try {
					createAndShowGUI();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}



}