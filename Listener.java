package il.ac.kinneret.mjmay.sentenceServerMulti;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Listener extends Thread{

    private ServerSocket serverSocket;
    static int i = 1;

    public Listener (ServerSocket socket){
        serverSocket = socket;
    }


    @Override
    public void run() {
        // start to listen on the server socket
        SentenceServerMulti sentenceServerMulti = new SentenceServerMulti();
       String IpAddress = serverSocket.getInetAddress().toString();


        System.out.println("Listening on port :  " + serverSocket.getLocalPort() );
        SentenceServerMulti.logger.info("Listening on port :  " + serverSocket.getLocalPort() + "\n");

        while (!interrupted()) { //Check if thread sleep or wait
            try {

                // get a new connection
                Socket clientSocket = serverSocket.accept();
             //   sentenceServerMulti.area.append("client "+ i + " connected \n");
               // i++;
                // start a worker
                 HandleClientThread clientThread = new HandleClientThread(clientSocket);
                 clientThread.start();

            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        // we're done!
        System.out.println("Stopped listening.");
        SentenceServerMulti.logger.info("Stopped listening.\n");
        try {
            serverSocket.close();
        } catch (Exception ex)
        {
            //noting to do
        }
    }
}
