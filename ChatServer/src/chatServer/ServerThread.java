package chatServer;

import java.io.*;
import java.net.*;

public class ServerThread extends Thread
{
	//Class Variable Declarations
	private DataInputStream input	= null;
	private Socket socket			= null;
	private PrintStream output		= null;
	private String userName			= "";
	private String inputString		= "";
	private boolean running			= true;
	
	//Class Final Variable Declarations
	private final ServerThread[] chatThreads;
	private final int maxClients;
	
	//Class Constructor
	public ServerThread(Socket skt, ServerThread[] cht)
	{
		this.socket = skt;
		this.chatThreads = cht;
		this.maxClients = cht.length;
	}
	
	@SuppressWarnings("deprecation")
	public void run()
	{
		try
		{
			//Input/Output Streams
			input = new DataInputStream(socket.getInputStream());
			output = new PrintStream(socket.getOutputStream());
			
			this.output.println("Connected!!\n");
			
			//Client will send userName first
			this.userName = input.readLine().trim();					
			
					
			//Send message that a new user has entered the chat-room
			sendMessage(">>>User " + userName + " has entered the room!");
			System.out.println(">>>User " + userName + " has entered the room!");
			output.println(">>>Welcome to the Server " + this.userName + "!\n>>>Type \"/help\" for a list of commands!");
			
			while (running)
			{
				//Read line from client
				inputString = input.readLine().trim();
				
				if (inputString.startsWith("/"))
				{
					checkCommand(inputString);					
				}
				else
				{
					sendMessageAll(inputString);
				}
			}
			sendMessage(">>>User " + userName + " has left the chat-room..");
			System.out.println(">>>User " + userName + " has left the chat-room...");
			
			for (int i = 0; i < maxClients; i++) 
			{
		        if (chatThreads[i] == this) 
		        {
		          chatThreads[i] = null;
		        }
		    }
			input.close();
			output.close();
			socket.close();
		}
		catch (IOException e){}				
	}
	//Method to send messages to all connected clients
	public void sendMessageAll(String msg)
	{
		for (int count = 0; count < maxClients; count++)
		{
			if (chatThreads[count] != null)
			{
				chatThreads[count].output.println(this.userName + ":> " + msg);
			}
		}
	}
	//Method to send messages to all connected clients other than this client
	public void sendMessage(String msg)
	{
		for (int count = 0; count < maxClients; count++)
		{
			if (chatThreads[count] != null && chatThreads[count] != this)
			{
				chatThreads[count].output.println(msg);
			}
		}
	}
	//Command to check if an entered command was valid
	public void checkCommand(String command)
	{
		if (command.toLowerCase().startsWith("/quit"))
		{
			output.println("/quit");
			running = false;
		}
		else if (command.toLowerCase().startsWith("/users"))
		{
			int userNum = 0;
			String names = "";
			for (int count = 0; count < maxClients; count++)
			{
				if (chatThreads[count] != null && chatThreads[count] != this)
				{
					names += chatThreads[count].userName + "\n";
					userNum++;
				}
			}
			output.println(">>>Number of other Users Online: " + userNum + "\n" + names);
		}
		else if (command.toLowerCase().startsWith("/whisper "))
		{
			String commandSplit = command.replace("/whisper ", "");
			try
			{
				boolean found = false;
				int count;
				for (count = 0; count < maxClients; count++)
				{
					if (chatThreads[count] != null && commandSplit.startsWith(chatThreads[count].userName))
					{
						if (chatThreads[count] != this)
						{
							chatThreads[count].output.println(">>>WHISPER from " + this.userName + ":> " + commandSplit.replace(chatThreads[count].userName,""));
							found = true;
						}
						else
						{
							chatThreads[count].output.println(">>>Note to self: " + commandSplit.replace(chatThreads[count].userName,""));
							found = true;
						}
					}
				}	
				if (!found)
					output.println(">>>No user with that name can be found...");
			}
			catch (Exception e) 
			{
				
			}
			
		}
		else if (command.toLowerCase().startsWith("/help"))
		{
			output.println(
					">>>List of Commands<<<\n"
				  + "/users to see the other users online now\n"
				  + "/whisper + username + message to send a private message\n"
				  + "/quit to leave the server");
		}
		else
		{
			output.println(">>>Unknown Command type \"/help\" for list of commands.");
		}
	}
	
}