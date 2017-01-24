package ntp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class TimeClient {
	private static String hostUrl = "127.0.0.1";
	private static int PORT = 27780;
	private Double minD;
	private NTPRequest minNTPrequest;
	private Socket socket;

	public TimeClient() throws ClassNotFoundException, InterruptedException {

		try {
                        
                        ArrayList<NTPRequest> NTPrequests=new ArrayList<NTPRequest>();
			for (int i = 0; i < 10; i++) {
                            threadSleep(300);
				socket = new Socket(InetAddress.getByName(hostUrl), PORT);
                                
                                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
                                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
                                //calculating T1
                                NTPrequests.add(i, new NTPRequest());
                                NTPrequests.get(i).setT1(System.currentTimeMillis());
                                
                                //send to server
                                toServer.writeObject(NTPrequests.get(i));
                                System.out.println("Send to server");
                                
                                //recieve from server
                                threadSleep(10 + (int)(Math.random() * 100));//Delay the packet comming from server(communication delay)
                                NTPrequests.set(i,(NTPRequest)fromServer.readObject());
                                System.out.println("Recieve from server");
                                
                                //calculate T4
                                NTPrequests.get(i).setT4(System.currentTimeMillis());
                                NTPrequests.get(i).calculateOandD();
                                threadSleep(300);
				
				socket.close();
				
			}
                        System.out.println("All measurements(ms) : ");
			for(NTPRequest request : NTPrequests)
                        {System.out.println( request.toString());}
                        
                        //sorting according to d
			Collections.sort(NTPrequests, new Comparator<NTPRequest>() {
                            @Override
                            public int compare(NTPRequest  x, NTPRequest  y)
                            {

                                return  (x.d).compareTo(y.d);
                            }
                        });
                        
                        
                        System.out.println(" the selected time difference(ms) : ");
                        System.out.println( NTPrequests.get(0).toString());
                        
                        System.out.println("the accuracy : ");
                        System.out.println((NTPrequests.get(0).o-((NTPrequests.get(0).d)/2))+"=<o=<"+ (NTPrequests.get(0).o+(NTPrequests.get(0).d/2)));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void sendNTPRequest(NTPRequest request) {
		//

	}

	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
		new TimeClient();
             
                
	}

}
