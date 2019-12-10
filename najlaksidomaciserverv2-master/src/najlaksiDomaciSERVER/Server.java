package najlaksiDomaciSERVER;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static void main(String[] args) {
		
		try {
			ServerSocket serverSocket = new ServerSocket(6666);
			Socket socketForCommunication = null;
			
			while(true) {
				System.out.println("Waiting for connection...");
				socketForCommunication = serverSocket.accept();
				
				ServersThreadsForClients newThreadForClient = new ServersThreadsForClients(socketForCommunication);
				newThreadForClient.start();
			}
		} catch (IOException e) {
			System.out.println("Greska prilikom pokretanja servera!");
		}
	}
}