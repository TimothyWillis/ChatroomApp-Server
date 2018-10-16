package chatServer;

import java.io.*;
import java.net.*;

public class ChatServer
{
	//Class Variable Declarations
	private static ServerSocket svrSocket	= null;	
	private static Socket socket			= null;
	
	//Class Final Variable Declarations
	private static final int maxClients = 16;
	private static final ServerThread[] chatThreads = new ServerThread[maxClients];
	
	public static void main(String args[])
	{
		//Declare the port number to use
		int portNum = 12345,
			count 	= 0;
		boolean running = true;
		
		//Welcome User
		System.out.println("Welcome!\n\nServer Started");
		
		//Open the serverSocket on the given port
		try {svrSocket = new ServerSocket(portNum);} 
		catch (IOException e){System.out.println(e);}
		
		//Inform User that the server has started
		System.out.println("");
		
		while (running)
		{
			try 
			{
				//Wait for Client(s) to connect
				socket = svrSocket.accept();
				
				//Find an open Client number
				for(count = 0; count < maxClients; count++)
				{
					if(chatThreads[count] == null)
					{
						//Create new ChatThread and start it
						chatThreads[count] = new ServerThread(socket, chatThreads);
						chatThreads[count].start();
						break;
					}
				}
				serverFullMessage(count);
			}
			catch (IOException e) {System.out.println(e);}
		}
	}
	
	//Method to check if the server was full and send a response message to client
	private static void serverFullMessage(int count) throws IOException
	{
		if (count == maxClients)
		{
			PrintStream toClient = new PrintStream(socket.getOutputStream());
			toClient.println("We're Sorry, the server is currently full. :(");
			toClient.close();
			socket.close();
		}
	}
}