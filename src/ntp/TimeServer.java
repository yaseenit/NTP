package ntp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeServer {
	private static int PORT = 27780;
	private ServerSocket serverSocket;

	public TimeServer() {
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server started on port: " + PORT);
			 while (true) {
                            
                            Socket clinetsock = serverSocket.accept();
                            Thread listeneer = new Thread(new TimeServer.NTPRequestHandler(clinetsock));
                            listeneer.start();
                            System.out.println("got a connection");

                         }

		} catch (IOException e) {
			e.printStackTrace();
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
        

	public static void main(String[] args) {
		new TimeServer();
	}

	private class NTPRequestHandler implements Runnable {
		private Socket client;
                 ObjectOutputStream toClient ;
                 ObjectInputStream fromClient;
                 NTPRequest NTPrequest;

		public NTPRequestHandler(Socket client) {
                        
                    try {
                        this.client = client;
                        NTPrequest=new NTPRequest();
                         toClient = new ObjectOutputStream(client.getOutputStream());
                         fromClient = new ObjectInputStream(client.getInputStream());
                    } catch (IOException ex) {
                        Logger.getLogger(TimeServer.class.getName()).log(Level.SEVERE, null, ex);
                    } 
		}

		@Override
		public void run() {
                    try {
                        
                        while ((NTPrequest= (NTPRequest)fromClient.readObject()) != null) {
                            System.out.println("read from Client");
                            threadSleep(10 + (int)(Math.random() * 100));//Delay the packet comming from Client
                            NTPrequest.setT2(System.currentTimeMillis()+1200);//adding artificial offset for server
                            NTPrequest.setT3(System.currentTimeMillis()+1200);//adding artificial offset for server
                            
                            toClient.writeObject(NTPrequest);
                            System.out.println("send to client");
                            
                            
                        }
                        
                    } catch (IOException ex) {
                        //Logger.getLogger(TimeServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (ClassNotFoundException ex) {
                        //Logger.getLogger(TimeServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
		private void sendNTPAnswer(NTPRequest request) {
			///
		}

	}

}
