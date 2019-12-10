package najlaksiDomaciCLIENT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
	
	public static BufferedReader inputFromServer;
	public static PrintStream inputForServer;
	public static BufferedReader inputFromKeyboard;

	public static void main(String[] args) {
		
		try {
			Socket socketForCommunication = new Socket("localhost", 6666);
			
			inputFromServer = new BufferedReader(new InputStreamReader(socketForCommunication.getInputStream()));
			inputForServer = new PrintStream(socketForCommunication.getOutputStream());
			inputFromKeyboard = new BufferedReader(new InputStreamReader(System.in));
			
			new Thread(new Client()).start();
			String messageFromServer;
			
			while(true) {
				messageFromServer = inputFromServer.readLine();
				System.out.println(messageFromServer);
				
				if(messageFromServer.startsWith(">>> Exit")) {
					break;
				}
			}
			
			socketForCommunication.close();
		} catch (UnknownHostException e) {
			 System.out.println("UNKNOWN HOST!");

		} catch (IOException e) {
			System.out.println("SERVER IS DOWN!!!");
		}
	}

	@Override
	public void run() {
		
		String messageForServer;
		
		try {
			while(true) {
				messageForServer = inputFromKeyboard.readLine();
				inputForServer.println(messageForServer);
				
				if(messageForServer.startsWith("--exit")) {
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}