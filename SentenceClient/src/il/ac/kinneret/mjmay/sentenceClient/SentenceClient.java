
package il.ac.kinneret.mjmay.sentenceClient;// Importing the required packages
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	private static JButton btn_Browse, btn_Upload, bth_uploadForAll , btn_Downlowd, btn_List, btn_disconnect , btn_lockFile ,btn_lockForAll,  btn_UnlockFile, btn_unlockForAll, btn_GetVersion,  btn_VersionOfAllServers; // Declaring Button variables.
	private  static JTextPane txt_Filename ;
	private static JComboBox<String> ComboBoxListOfFiles; //  combobox variable for the List of file that upload to the server .
	private static JComboBox<String> ComboBoxNamesOfServer; // combobox variable -> to choose the server.
	private static JTextArea jTextArea; //  textarea variable for write Remarks on the form.
	private static JFileChooser jFileChooser; // filechooser variable --> for showOpenDialog function and getSelectedFile func .
	private static File fileProjectClient = new File("");
	private static String pathOfProject = fileProjectClient.getAbsolutePath();
	final static String PATH_CONFIGURATION_FILE = "\\FileConfigurationCsv.csv";
	final static String PATH_LOGGER_CLIENT = "\\MyLogFileClient.log";

	public static Logger logger;
	public static FileHandler fh;
	public static Socket socket;


	//More variable :
	private static int i;
	private static int PORT;
	private static String nameOfServerStr ;
	private static String IP;
	private static String NAMEOFSERVER ="";

	public static String IPforlist;
	public static  int portforlist ;
	final File[] fileToSend;

	//date format for http
	static String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.systemDefault())).toString();

	public MultipleClient() throws IOException {
		super(new BorderLayout());

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
		bth_uploadForAll = new JButton("Upload for all");
		bth_uploadForAll.addActionListener(this);
		btn_Upload.addActionListener(this);
		btn_List = new JButton("List");
		btn_List.addActionListener(this);
		btn_Downlowd = new JButton("Download");
		btn_Downlowd.addActionListener(this);
		btn_disconnect = new JButton("Disconnect");
		btn_disconnect.addActionListener(this);
		btn_lockFile= new JButton("Lock ");
		btn_lockFile.addActionListener(this);
		btn_lockForAll = new JButton("Lock for all");
		btn_lockForAll.addActionListener(this);
		btn_UnlockFile= new JButton("UnLock");
		btn_UnlockFile.addActionListener(this);
		btn_unlockForAll = new JButton("Unlock for all");
		btn_unlockForAll.addActionListener(this);
		btn_GetVersion = new JButton("Version");
		btn_GetVersion.addActionListener(this);
		txt_Filename = new JTextPane();
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
		buttonPanel.add(bth_uploadForAll);
		buttonPanel.add(btn_List);
		buttonPanel.add(ComboBoxListOfFiles);
		buttonPanel.add(btn_Downlowd);
		buttonPanel.add(btn_lockFile);
		buttonPanel.add(btn_lockForAll);
		buttonPanel.add(btn_UnlockFile);
		buttonPanel.add(btn_unlockForAll);
		buttonPanel.add(btn_GetVersion);
		buttonPanel.add(txt_Filename);
		buttonPanel.add(btn_VersionOfAllServers);
		buttonPanel.add(btn_disconnect);


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
				jTextArea.append("\nYou choose the file :  " + file.getName() + " , size : "+ file.length() + "\n" );
				logger.info("\nYou choose the file :  " + file.getName() + " , size : "+ file.length() + "\n" );
			}
		}

		//Upload clicked event
		else if (e.getSource() == btn_Upload) {
			try {
				Upload();
			} catch (Exception error) {
				error.printStackTrace();
			}
		}

		else  if(e.getSource() == bth_uploadForAll){
			try{
				uploadForAll();
			}catch (Exception error){
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

		else if(e.getSource() == btn_lockForAll){
			try{
				LockForAll();
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

		else  if(e.getSource() == btn_unlockForAll){
			try{
				unlockForAll();
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
	public void Upload() throws IOException {
		try {

			listOfServers();
			Socket socket = new Socket(IPforlist, portforlist); // socket connection to the server

			//the file we send the details for the server
			File file = jFileChooser.getSelectedFile();  // getting the selected file to upload to server - BROWSER
			String fileName = file.getName();
			int fileSize = (int) file.length();
			PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
			//for the send msg
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			Scanner scanner = new Scanner(socket.getInputStream());

			//send a msg to the server upload filename filesize
			outToServer.writeBytes("UPLOAD\n");
			//send values to the server
			pr.println(fileName);
			pr.println(fileSize);
			pr.println("client");// send to the server that he is the client
			System.out.println("Address of client "+ Inet4Address.getLocalHost().getHostAddress());
			String ipofme = Inet4Address.getLocalHost().getHostAddress();
			pr.println(ipofme); //send the ip of the client

			String uploadFile;
			byte[] filebyte= Files.readAllBytes(Paths.get(file.toString()));
			uploadFile = Base64.getEncoder().encodeToString(filebyte);
			pr.println(uploadFile);

			String msg = scanner.nextLine();
			switch (msg){
				case "OK":
					jTextArea.append("\nOK : Upload succsessfuly\n");
					logger.info("\nOK :Upload succsessfuly\n");
					break;
				case "ERROR":{
					jTextArea.append("\nERROR : Upload not succsessfuly\n");
					logger.info("\nERROR :Upload not succsessfuly\n");
				}
			}
		}catch (SocketException e) {
			e.printStackTrace();
			//logger anf text area on gui window - on error case
			jTextArea.append("\nUpload : not connect to server :" + NAMEOFSERVER);
			logger.info("\nUpload : not connect to server :" +NAMEOFSERVER);
		}
	}

	//---------------------------------------------------------------------


	/*
	This function upload the file to all the servers
	 */
	private static void uploadForAll() throws IOException {
		String CSVpath = pathOfProject + PATH_CONFIGURATION_FILE;
		BufferedReader bufferedReader = null;

		String line = "";
		try {
			bufferedReader = new BufferedReader(new FileReader(CSVpath));
			bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				try {
					String[] row = line.split(","); //---> serverOne | Port : 1234 | Ip 127.0.0.1
					NAMEOFSERVER=row[0];
					PORT = Integer.parseInt(row[1]);
					IP = row[2];

					Socket socket = new Socket(IP, PORT); // socket connection to the server
					jTextArea.append("\nConnect to server :  " + row[0] + "\n");
					PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
					Scanner scanner = new Scanner(socket.getInputStream());

					DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
					File file = jFileChooser.getSelectedFile();  // getting the selected file to upload to server
					String fileName = file.getName();
					outToServer.writeBytes("UPLOAD\n");
					int FileSize = (int) file.length();
					//send values to the server
					pr.println(fileName);
					pr.println(FileSize);
					pr.println("client");// send to the server that he is the client
					String ipofme = Inet4Address.getLocalHost().getHostAddress();
					pr.println(ipofme); //send the ip of the client

					String uploadFile;
					byte[] filebyte = Files.readAllBytes(Paths.get(file.toString()));
					uploadFile = Base64.getEncoder().encodeToString(filebyte);
					pr.println(uploadFile);

					String msg = scanner.nextLine();
					switch (msg) {
						case "OK":
							jTextArea.append("\nOK : Upload succsessfuly\n");
							logger.info("\nOK :Upload succsessfuly\n");
							break;
						case "ERROR": {
							jTextArea.append("\nERROR : Upload not succsessfuly\n");
							logger.info("\nERROR :Upload not succsessfuly\n");
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
					//logger anf text area on gui window - on error case
					jTextArea.append("\nUpload : not connect to server :" + NAMEOFSERVER);
					logger.info("\nUpload : not connect to server :" + NAMEOFSERVER);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//-----------------------------------------------------------------------------------------------------
	/*
    This function passes the fileconfiguration.csv rows and matches the selected server name with the corresponding ip and port in the file.
    Finally updates the variables - IP , PORT in the socket .
     */
	private static void listOfServers () throws IOException{

		String CSVpath= pathOfProject + PATH_CONFIGURATION_FILE;
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
					NAMEOFSERVER =NamePortIpArr[0];
					portforlist = Integer.parseInt(NamePortIpArr[1])  ;
					IPforlist = NamePortIpArr[2];
				}
			}

		}catch (Exception e){

		}

	}



	//---------------------------------------------------------------------
	/*
     This function overrides the files in the uploadFile folder - the folder where the files uploaded to the server are saved.
      (See code in FileServer) and saves them in the comboBox of the list
      */
	public void listOfFiles() throws IOException {

		try {
			listOfServers();
			Socket socket = new Socket(IPforlist, portforlist);
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			outToServer.writeBytes("LIST\n");
			BufferedReader ListOfFilesFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = null;
			ComboBoxListOfFiles.removeAllItems();//we need to remove all the items on the comboBox- to approve the file in the folder yet .
			jTextArea.append("\nListOfServer : \n ");
			logger.info("\nListOfServer : \n ");
			while ((line = ListOfFilesFromServer.readLine()) != null) {
				jTextArea.append(line + "  ");
				logger.info(line + "  ");
				ComboBoxListOfFiles.addItem(line);
			}
		}catch (SocketException e){
			e.printStackTrace();
			//logger and text area on gui window
			jTextArea.append("\nLIST : not connect to server :" +nameOfServerStr);
			logger.info("\nLIST : not connect to server :" +nameOfServerStr);
		}
	}


	//----------------------------------------------------------------------------------------------------------
	//this function get version of file in a specific server chosen (on the combo box ) and a specific name of file (on text box)
	//it send to the server chosen the comman : GEXTVERSION "FILENAME" .
	public void getVersion() throws IOException {
		try {
			listOfServers();
			Socket socket = new Socket(IPforlist, portforlist);
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			Scanner scanner = new Scanner(socket.getInputStream());
			String FileNameOnVersion = txt_Filename.getText();
			outToServer.writeBytes("GETVERSION\n");

			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.println(FileNameOnVersion); // send a msg to the server with the file name

			String WhoLockedTheFile = "no one";
			//msgFromServer SCANNER - THE SERVER MSG GOING TO BE "OK" OR "ERROR"
			String msgFromServer = scanner.nextLine(); //ok or error msg
			String digestAnddate = scanner.nextLine(); //the degit and time from the server
			try {
				WhoLockedTheFile = scanner.nextLine();
			}catch (Exception e){
				e.printStackTrace();
			}

			switch (msgFromServer) {
				case "OK":
					jTextArea.append("\nSuccessful version a file in "+nameOfServerStr +": "  + FileNameOnVersion + "\n the version is : " + digestAnddate + " locked by : "+ WhoLockedTheFile );
					logger.info("\nSuccessful version a file in " +nameOfServerStr + ": "+  FileNameOnVersion + "\n the version is : " + digestAnddate + " locked by : "+ WhoLockedTheFile);
					break;
				case "ERROR":
					jTextArea.append("\nFailed get version a file : " + FileNameOnVersion + ", it doesnt exist in server ! ");
					logger.info("\nFailed get version a file : " + FileNameOnVersion + ", it doesnt exist in server ! ");
					break;
				default:
					jTextArea.append("version function Not good \n");
					logger.info("version function Not good \n");
					break;
			}

		}catch (SocketException e){
			e.printStackTrace();
			jTextArea.append("\nVERSION exception !");
			logger.info("\nVERSION exception !");
		}
	}


	//---------------------------------------------------------------------
	public void UNLock() throws IOException {
		try {
			listOfServers(); // the client choose on which server to unlock the file , this function update the IPforlist and portforlist
			socket = new Socket(IPforlist, portforlist);
			//get the  name of file from gui
			String selectedBook = txt_Filename.getText();

			//tell the server that we want to lock  a file from him- that move to the LOCK case in server(HandlerClientThread)
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			outToServer.writeBytes("UNLOCK\n");



			//print writer to erite to the server .
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.println(selectedBook);
			pw.println("client");
			String ipofme = Inet4Address.getLocalHost().getHostAddress();
			pw.println(ipofme); //send the ip of the client
			//msgFromServer SCANNER - THE SERVER MSG GOING TO BE "OK" OR "ERROR"


			//Scanner to get information from the server
			Scanner scanner = new Scanner(socket.getInputStream());
			String msgFromServer = scanner.nextLine();
			switch (msgFromServer) {
				case "OK":
					jTextArea.append("\nSuccessful UNLock a file : " + selectedBook + "\n");
					logger.info("\nSuccessful UNLock a file : " + selectedBook + "\n");
					break;
				case "ERROR":
					jTextArea.append("\nFailed UNLock a file : " + selectedBook + "\n");
					logger.info("\nFailed UNLock a file : " + selectedBook + "\n");
					break;
				default:
					jTextArea.append("\nUNLock function Not good \n");
					logger.info("\nUNLock function Not good \n");
					break;
			}
		}catch (SocketException e) {
			e.printStackTrace();
			jTextArea.append("\nUNLOCK: not connect to server :"+NAMEOFSERVER);
			logger.info("\nUNLOCK: not connect to server :"+NAMEOFSERVER);
		}

	}

	//---------------------------------------------------------------------

	public void unlockForAll() throws  IOException {
		String CSVpath = pathOfProject + PATH_CONFIGURATION_FILE;
		BufferedReader bufferedReader = null;

		String line = "";
		try {
			bufferedReader = new BufferedReader(new FileReader(CSVpath));
			bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				try {
					String[] row = line.split(","); //---> serverOne | Port : 1234 | Ip 127.0.0.1
					NAMEOFSERVER = row[0];
					PORT = Integer.parseInt(row[1]);
					IP = row[2];
					Socket socket = new Socket(IP, PORT); // socket connection to the server
					jTextArea.append("\nConnect to server :  " + row[0] + "\n");

					PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
					DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());

					String selectedBook = txt_Filename.getText(); //the name of the file to unlock
					outToServer.writeBytes("UNLOCK\n");
					pw.println(selectedBook);
					pw.println("client");
					String ipofme = Inet4Address.getLocalHost().getHostAddress();
					pw.println(ipofme); //send the ip of the client
					//msgFromServer SCANNER - THE SERVER MSG GOING TO BE "OK" OR "ERROR"


					//Scanner to get information from the server
					Scanner scanner = new Scanner(socket.getInputStream());
					String msgFromServer = scanner.nextLine();
					switch (msgFromServer) {
						case "OK":
							jTextArea.append("\nSuccessful UNLock a file : " + selectedBook + "\n");
							logger.info("\nSuccessful UNLock a file : " + selectedBook + "\n");
							break;
						case "ERROR":
							jTextArea.append("\nFailed UNLock a file : " + selectedBook + "\n");
							logger.info("\nFailed UNLock a file : " + selectedBook + "\n");
							break;
						default:
							jTextArea.append("\nUNLock function Not good \n");
							logger.info("\nUNLock function Not good \n");
							break;
					}
				} catch (SocketException e) {
					e.printStackTrace();
					jTextArea.append("\nUNLOCK :not connect to server :" + NAMEOFSERVER + "\n");
					logger.info("\nUNLOCK: not connect to server :" + NAMEOFSERVER + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	//---------------------------------------------------------------------
//this function lock file on the server , the client need to write the name of the file in the text box, after he need to click on the lock button .
	public void Lock() throws IOException {
		try {
			listOfServers(); //this function chose the server that chosen by client on combo box and update ip and port
			socket= new Socket(IPforlist , portforlist);
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			//send msg to the server
			outToServer.writeBytes("LOCK\n");
			//get the  name of file from gui
			String selectedBook = txt_Filename.getText(); //THE FILE THE USER SELECT TO LOCK
			pw.println(selectedBook); //THE NAME OF THE FILE TO THE SERVER
			pw.println("client");
			String ipofme = Inet4Address.getLocalHost().getHostAddress();
			pw.println(ipofme); //send the ip of the client

			//Scanner to get information from the server
			Scanner scanner = new Scanner(socket.getInputStream());


			String msgFromServer = scanner.nextLine();
			switch (msgFromServer) {
				case "OK":
					jTextArea.append("\nSuccessful Lock a file : " + selectedBook + "\n");
					logger.info("\nSuccessful Lock a file : " + selectedBook + "\n");
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
			jTextArea.append("\nLOCK :not connect to server :"+ NAMEOFSERVER +"\n");
			logger.info("\nLOCK :not connect to server :"+ NAMEOFSERVER+ "\n");
		}
	}

//______________________________________________________________________________________________________________________________
//This function lock file inn all the server who connect now ! , if the server not connect its ignor .
	public void LockForAll() throws IOException{
			String CSVpath = pathOfProject + PATH_CONFIGURATION_FILE;
			BufferedReader bufferedReader = null;

			String line = "";
			try {
				bufferedReader = new BufferedReader(new FileReader(CSVpath));
				bufferedReader.readLine();
				while ((line = bufferedReader.readLine()) != null) {
					try {
						String[] row = line.split(","); //---> serverOne | Port : 1234 | Ip 127.0.0.1
						NAMEOFSERVER = row[0];
						PORT = Integer.parseInt(row[1]);
						IP = row[2];

						Socket socket = new Socket(IP, PORT); // socket connection to the server
						jTextArea.append("\nConnect to server :  " + row[0] + "\n");

						Scanner scanner = new Scanner(socket.getInputStream());
						DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
						PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);


						//send msg to the server
						outToServer.writeBytes("LOCK\n");
						//get the  name of file from gui
						String selectedBook = txt_Filename.getText(); //THE FILE THE USER SELECT TO LOCK
						pw.println(selectedBook); //THE NAME OF THE FILE TO THE SERVER
						pw.println("client");
						String ipofme = Inet4Address.getLocalHost().getHostAddress();
						pw.println(ipofme); //send the ip of the client


						String msgFromServer = scanner.nextLine();
						switch (msgFromServer) {
							case "OK":
								jTextArea.append("Successful LOCK a file : " + selectedBook + "\n");
								logger.info("Successful LOCK a file : " + selectedBook + "\n");
								break;
							case "ERROR":
								jTextArea.append("Failed LOCK a file : " + selectedBook + "\n");
								logger.info("Failed LOCK a file : " + selectedBook + "\n");
								break;
							default:
								jTextArea.append("LOCK function Not good \n");
								logger.info("LOCK function Not good \n");
								break;

						}

					} catch (SocketException e) {
						e.printStackTrace();
						jTextArea.append("\nLOCK :not connect to server :" + NAMEOFSERVER + "\n");
						logger.info("\nLOCK :not connect to server :" + NAMEOFSERVER + "\n");
					}
				}

			} catch (IOException e) {

			}
		}


	//__________________________________________________________________________________________________________________________________

	// Function to download files to the server
	public void downloadFile() throws IOException {
		try {
			//connection
			listOfServers();
			Socket socket = new Socket(IPforlist, portforlist);

			//get the  name of file from gui
			String selectedBook = (String) ComboBoxListOfFiles.getSelectedItem();

			JFileChooser FileChooser = new JFileChooser();
			FileChooser.setDialogTitle("choose location");
			FileChooser.setSelectedFile(new File(selectedBook));

			if(FileChooser.showSaveDialog(btn_Downlowd) == JFileChooser.APPROVE_OPTION){

				//tell the server that we want to download from him- that move to the DOWNLOAD case in server(HandlerClientThread)
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				outToServer.writeBytes("DOWNLOAD\n");

				Scanner scanner = new Scanner(socket.getInputStream());


				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				pw.println(selectedBook);
				//file from the server  download here .
				//save the file to folder
				String uploadFile = scanner.nextLine();

				FileOutputStream fos = new FileOutputStream(FileChooser.getSelectedFile().toString()); //to save the file in a path spcific that the client select
				BufferedOutputStream bos = new BufferedOutputStream(fos); //buffer to saving this file
				InputStream is = socket.getInputStream();

				//Upload the file - saving this file to a path temporary folder on the server//////////////////////
				byte[]filebytes = Base64.getDecoder().decode(uploadFile);
				fos.write(filebytes);

			}


			logger.info("\nThe file download succesfully! ");
			jTextArea.append("\nThe file download succesfully! ");
			//logger.info("The download not succesfully! ");
			//jTextArea.append("The file download not succesfully! ");
		}catch (SocketException e) {
			e.printStackTrace();
			jTextArea.append("\nDOWNLOAD :not connect to server :"+nameOfServerStr);
			logger.info("\nDOWNLOAD: not connect to server :"+nameOfServerStr);
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


	//this function get version of file from all server :

	public void grtVersionFromAll() throws IOException {
		String CSVpath = pathOfProject + PATH_CONFIGURATION_FILE;
		BufferedReader bufferedReader = null;

		String line = "";
		try {
			bufferedReader = new BufferedReader(new FileReader(CSVpath));
			bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				try {
					String[] row = line.split(","); //---> serverOne | Port : 1234 | Ip 127.0.0.1
					NAMEOFSERVER = row[0];
					PORT = Integer.parseInt(row[1]);
					IP = row[2];
					Socket socket = new Socket(IP, PORT); // socket connection to the server
					jTextArea.append("\nConnect to server :  " + row[0] + " ");

					DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
					Scanner scanner = new Scanner(socket.getInputStream());
					String filename = (String) ComboBoxListOfFiles.getSelectedItem();
					String FileNameOnVersion = txt_Filename.getText();
					outToServer.writeBytes("GETVERSION\n");

					PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
					pw.println(FileNameOnVersion); // send a msg to the server with the file name

					//msgFromServer SCANNER - THE SERVER MSG GOING TO BE "OK" OR "ERROR"
					String msgFromServer = scanner.nextLine();
					String digestAnddate = scanner.nextLine();
					String whoLockedTheFile = "no one ";
					try{
						whoLockedTheFile= scanner.nextLine();
					}catch (Exception e){
						e.printStackTrace();
					}

					switch (msgFromServer) {
						case "OK":
							jTextArea.append("\nServer :" +NAMEOFSERVER +" "+ IP + ":  " + PORT + "  version of file : " + FileNameOnVersion + " : " + digestAnddate + " locked by : " + whoLockedTheFile+ "\n");
							logger.info("\nServer : "+NAMEOFSERVER+ " " +IP + ":  " + PORT + " version of file: " + FileNameOnVersion + " : " + digestAnddate +  " locked by : " + whoLockedTheFile +" \n");
							break;
						case "ERROR":
							jTextArea.append("\nServer : "+ IP + ":  " + PORT + "\nFailed get version a file : " + FileNameOnVersion + "it doesnt exist in server ! " + "\n");
							logger.info("\nServer : " + IP + ":  " + PORT + "\nFailed get version a file : " + FileNameOnVersion + "it doesnt exist in server ! " + "\n");
							break;
						default:
							jTextArea.append("\nversion function Not good \n");
							logger.info("\nversion function Not good \n");
							break;
					}
				} catch (SocketException e1) {
					e1.printStackTrace();
					jTextArea.append("\nVERSION :not connect to server :" + IP+"\n");
					logger.info("\nVERSION : not connect to server :"+ IP+"\n");
				}
			}
		} catch (IOException E) {
		}
	}



	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				try {
					//logger initialize
					logger = Logger.getLogger("myLogClient");
					fh = new FileHandler(pathOfProject +PATH_LOGGER_CLIENT);
					logger.addHandler(fh);
					SimpleFormatter formatter = new SimpleFormatter();
					fh.setFormatter(formatter);
					logger.info("My Log client :\n\n");
				}catch (SecurityException e){
					e.printStackTrace();
				}
				catch (IOException e){
					e.printStackTrace();
				}

				//show the window now
				try {
					createAndShowGUI();
				} catch (IOException e) {
					e.printStackTrace();
				}





			}
		});
	}



}