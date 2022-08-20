package il.ac.kinneret.mjmay.sentenceServerMulti;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class HandleClientThread extends Thread {

    public static ConcurrentHashMap FileVersionHash= new ConcurrentHashMap();
    public static ConcurrentHashMap FileUsers = new ConcurrentHashMap();
    public static ConcurrentHashMap<String , Boolean> FileLockHash= new ConcurrentHashMap<String , Boolean>();
    private Socket clientSock;
    private String fileName;
    // Create a HashMap object called capitalCities that will store String keys(NAME OF FILE)  and String values:

    /**
     * Initializes the server.
     * @param sock The client socket to handle.
     */
    public HandleClientThread(Socket sock)
    {
        super("HandleClientThread-" + sock.getRemoteSocketAddress().toString());
        this.clientSock = sock;

    }


//for version- make bytes to hex
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }




    //this func update the hashMap of version
    public static void versionUpdate(String fileName) throws NoSuchAlgorithmException {
        //time
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date()); //thas the data time

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(fileName.getBytes(StandardCharsets.UTF_8)); // that the digest

        //save the version in hashMap every time the file upload .
        //check if the file exist and after that change the value of the version

       Boolean containTheFileAlready =  FileVersionHash.contains(fileName);
       if(!containTheFileAlready){
          FileVersionHash.put(fileName , hash+" " +nowAsISO ); // this line realy update tha hash map
        }
       else {
          FileVersionHash.replace(fileName,hash+" " +nowAsISO); // replace the version was there
        }


    }


    /**
     * Runs the server
     */
    public void run()
    {

        try{
            // attach a buffered reader and print writer
            Scanner scanner = new Scanner(clientSock.getInputStream());
            BufferedInputStream bis = new BufferedInputStream(clientSock.getInputStream());
            PrintWriter pw = new PrintWriter(clientSock.getOutputStream(), true);
            String Command = scanner.nextLine();

            //check what the commanf that the client send :

            switch (Command) {

                case "UPLOAD": {

                    String path = SentenceServerMulti.pathUpload; // the path from the file confiruration in the server
                    String CSV_FILE_NAME="C:\\multiServerClient\\SentenceServerMulti\\fileslist.csv";
                    String FileName = scanner.nextLine();
                    int FileSize = scanner.nextInt();
                    //version and time of this file :
                    //digest

                    versionUpdate(FileName);//this func update the hashMap of version
                    FileLockHash.put(FileName, false);//update the file to be not locked yet .

                    Boolean LockState = false ;
                    String userIp= clientSock.getRemoteSocketAddress().toString();

                    String[] DataTocsvfile = {FileName , LockState.toString() , userIp };

                    //saving the data of the file in csv file . in lock and unlock we will use it

                    File csvOutputFile = new File(CSV_FILE_NAME);
                    //ENTER TO THE CSV FILE - FILESCSV
                    try{
                        FileWriter fw = new FileWriter(CSV_FILE_NAME ,true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter pww = new PrintWriter(bw) ;

                        pww.println(FileName +"," + LockState.toString() + "," +userIp );
                        pww.flush();
                        pww.close();

                    }catch (Exception e){

                    }


                    BufferedOutputStream bos;
                    try (FileOutputStream fos = new FileOutputStream(path +"\\" + FileName)) { // the path from the file confiruration in the server
                        bos = new BufferedOutputStream(fos);
                        byte[] buffer = new byte[FileSize];
                        fos.write(buffer);
                    }
                    InputStream is = clientSock.getInputStream();


                    System.out.println("Incoming File: " + FileName);
                    System.out.println("Size: " + FileSize + "Byte");
                    // if(FileSize ==is.read(buffer , 0 , buffer.length) )System.out.println("File is verified");
                    //  else System.out.println("File is corrupted. File Recieved " + is.read(buffer , 0 , buffer.length) + " Byte");
                    pw.println("OK");
                    bos.close();
                    break;

                    /*
                    String path = SentenceServerMulti.pathUpload; // the path from the file confiruration in the server
                    String CSV_FILE_NAME="C:\\multiServerClient\\SentenceServerMulti\\fileslist.csv"; // not used maby after

                    //msg from the client
                    String FileName = scanner.nextLine();// we get the  file name from the client
                    int FileSize = scanner.nextInt(); // we get the filr size from the client
                    Boolean LockState = false ;
                    String userIp= clientSock.getInetAddress().toString();
                    //here we check if we hava this file on the folder. if yes we check if that file is lock, if yes it can be upload unly by the user locked it .
                    //if not locked it return msg to the client with error
                   if (FileVersionHash.containsKey(FileName)){
                       if(FileLockHash.get(FileName).equals(true)){
                           if(userIp.equals(FileUsers.get(FileName))){
                               //version and time of this file + digets
                               versionUpdate(FileName); //this func update the hashMap of version
                               FileLockHash.replace(FileName, true); //update the file to be not locked yet (at the hash map FileLockHash )

                               BufferedOutputStream bos;
                               try (FileOutputStream fos = new FileOutputStream(path +"\\" + FileName)) { // the path from the file confiruration in the server
                                   bos = new BufferedOutputStream(fos);
                                   byte[] buffer = new byte[FileSize];
                                   fos.write(buffer);
                               }
                               InputStream is = clientSock.getInputStream();


                               System.out.println("Incoming File: " + FileName);
                               System.out.println("Size: " + FileSize + "Byte");
                               System.out.println("HashMap elements update:");
                               System.out.println( FileLockHash);

                               SentenceServerMulti.logger.info(" Incoming File: " + FileName + " " +" Size: " + FileSize + " Byte"  );
                               pw.println("OK");
                               bos.close();
                           }
                       }  //the file exist but it on unlock state , the upload did not work
                       else {
                           pw.println("ERROR");
                           pw.println("the file exist and on unlock state, it can't be upload again ");
                       }
                   }
                   // the file doesnt exist in the list so it can upload
                   else {
                       versionUpdate(FileName); //this func update the hashMap of version
                       FileLockHash.put(FileName, false); //update the file to be not locked yet (at the hash map FileLockHash )

                       BufferedOutputStream bos;
                       try (FileOutputStream fos = new FileOutputStream(path +"\\" + FileName)) { // the path from the file confiruration in the server
                           bos = new BufferedOutputStream(fos);
                           byte[] buffer = new byte[FileSize];
                           fos.write(buffer);
                       }
                       InputStream is = clientSock.getInputStream();


                       System.out.println("Incoming File: " + FileName);
                       System.out.println("Size: " + FileSize + "Byte");
                       System.out.println("HashMap elements update:");
                       System.out.println( FileLockHash);

                       SentenceServerMulti.logger.info(" Incoming File: " + FileName + " " +" Size: " + FileSize + " Byte"  );
                       pw.println("OK");
                       bos.close();
                   }
                    //pw.println("ERROR");// TO CHECK
                   break;*/
                }



                case "LIST": {

                    String textToTheClient; // return to the client the files name like this :  FILELIST : NAME1:NAME2:NAME3...
                    PrintWriter outToClient = new PrintWriter(clientSock.getOutputStream(), true);
                    String path = "C:\\multiServerClient\\SentenceServerMulti\\UploadFiles";
                    File f = new File(path);
                    File[] listOfFile = f.listFiles();

                    String listForTheClient ="";

                    for (int i = 0; i < listOfFile.length; i++) {
                        if (listOfFile[i].isFile()) {
                            textToTheClient = listOfFile[i].getName();

                            outToClient.println(textToTheClient);
                            System.out.println(textToTheClient + "\n");
                        }

                        /*
                        * FILELIST filename:filename The line begins with FILELIST (capitalization isn’t important). It’s followed by a series of valid file names delimited by : There may be one or more file names in the
                        response. The line ends with a new line character*/
                    }
                    break;
                }


                case "DOWNLOAD": {

                    String textToTheClient; // return to the client the files name like this :  FILELIST : NAME1:NAME2:NAME3...
                    PrintWriter outToClient = new PrintWriter(clientSock.getOutputStream(), true);

                    String FileName = scanner.nextLine();
                    String path = "C:\\multiServerClient\\SentenceServerMulti\\UploadFiles\\" + FileName;
                    System.out.println("the file name is :" + FileName + "in path : " + path);
                    File fileToDownload = new File(path); //the filr we want to sent to the derver
                    BufferedInputStream BufferedInputStream = new BufferedInputStream(new FileInputStream(fileToDownload));
                    OutputStream os=clientSock.getOutputStream();
                    int fileSize = (int)fileToDownload.length();
                    Scanner in = new Scanner(clientSock.getInputStream());

                    outToClient.println(path);
                    outToClient.println(fileSize);
                    byte[] buffer = new byte[fileSize]; // Defining the size of the buffer
                    BufferedInputStream.read(buffer, 0 , buffer.length);
                    os.write(fileSize);
                    os.write(buffer, 0 , buffer.length);
                    System.out.println(in.nextLine());
                    outToClient.println("OK");
                    os.flush();


                    break;
                }



                //this lock file one server .
                case "LOCK" :{

                    System.out.println("HashMap elements update:");
                    System.out.println( FileLockHash);


                    //get the file name from the client
                    try {
                        String FileName = scanner.nextLine();
                        PrintWriter outToClient = new PrintWriter(clientSock.getOutputStream(), true);
                        String userIp = clientSock.getInetAddress().toString();
                        //check if we have the file in upload hash map

                        //update the hash map - list of files
                        String path = SentenceServerMulti.pathUpload;
                        BufferedReader bufferedReader = null;
//
                        Boolean LockState  = FileLockHash.get(FileName);
                        boolean fileExist = FileLockHash.containsKey(FileName);
                        System.out.println(fileExist + LockState.toString());
                        if (fileExist == false || LockState ) {//the file doesnt exist or the file already locked !
                            outToClient.println("ERROR");
                        }
                        else {//the file exist

                            FileLockHash.replace(FileName, true); // change lock of file to true
                            FileUsers.put(FileName ,userIp ); // saved the user ip
                            outToClient.println("OK");

                        }

                    }catch (IOException e){
                        System.out.println("problem in Lock case");
                    }
                    break;
                }

                case "UNLOCK" : {
                    String FileName = scanner.nextLine();
                    PrintWriter outToClient = new PrintWriter(clientSock.getOutputStream(), true);
                    String userIp = clientSock.getInetAddress().toString();
                    BufferedReader bufferedReader = null;


                    Boolean LockState = FileLockHash.get(FileName); //state of file - unlock or not
                    boolean fileExist = FileLockHash.containsKey(FileName);
                    System.out.println(fileExist +" " + LockState.toString() +" " + userIp + " " + FileUsers.get(FileName));
                    if (fileExist) { // if the file exist
                        if (LockState) { // check if the file is alraedy  locked .
                            if (userIp.equals(FileUsers.get(FileName))) { // check who locked that file- if the same user he can unlock it
                                FileLockHash.replace(FileName, false); // change lock of file to true
                                outToClient.println("OK");
                            }
                        }
                        outToClient.println("ERROR");
                    }
                    break;
                }




                case "GETVERSION" : {

                    String FileNameOnVersion = scanner.nextLine();
                    String Version = "";
                    PrintWriter outToClient = new PrintWriter(clientSock.getOutputStream(), true);

                    //check if the file is saved in the path
                    String path = SentenceServerMulti.pathUpload;
                    File folder = new File(path);
                    File[] listOfFile = folder.listFiles();

                    boolean bool = false;

                    for (int i = 0; i < listOfFile.length; i++) {
                        if (listOfFile[i].isFile()) {
                            if(listOfFile[i].getName().equals(FileNameOnVersion)){ // if the file exist
                                bool= true;
                                break;
                            }
                            else{
                                //THE FILE DOESNT EXIST
                            }
                        }
                    }

                    if(bool){
                        outToClient.println("OK");
                        String data = FileVersionHash.get(FileNameOnVersion).toString();
                        outToClient.println(data); //return the version of this file to the client
                    }
                    else{
                        outToClient.println("ERROR"); // the file probebly dosnt exist
                        outToClient.println("File Not exist!"); //return the version of this file to the client
                    }




                   // VERSION digest datetime The line begins with VERSION (capitalization isn’t important) followed by one
                   // space. It’s followed by a version hash digest in hexadecimal format, a space, and a timestamp in ISO
                   // 8601 long date time format (e.g. 2021-10-29T08:27:22Z). The line ends with a new line character.

                    break;
                }

                case "OK" : {
                   // OK The line contains just the letters OK, but capitalization and extra trailing spaces are not important. The
                  //  line ends with a new line character

                }

                case "ERROR" : {
                 //   ERROR The line contains just the letters ERROR, but capitalization and extra
                    // trailing spaces are not important. The line ends with a new line character.


                }


                default: {
                    System.out.println("nooooo ! ");
                    break;
                }
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Error in communication.  Closing.\n");
            SentenceServerMulti.logger.info("Error in communication.  Closing.\n");
            e.printStackTrace();
        }
        finally {
            System.out.println("Finished and closed on " + clientSock.getRemoteSocketAddress().toString());
            try { clientSock.close(); } catch (Exception ex) {}
        }
        return;
    }

}
