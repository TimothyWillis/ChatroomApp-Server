package client;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextField;
import java.awt.SystemColor;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JTextArea;

public class ChatClientWindow implements Runnable
{
	private JFrame frmChatatron;
	private JTextField sendText;
	private JTextArea chatField;
	private JButton btnSend;
	private DefaultCaret caret;
	
	private static ChatClientWindow window;
	
	private static Socket socket			= null;
	private static DataInputStream input	= null;
	private static PrintStream output		= null;

	private static int sendPort;
	private static String ipAddress;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		createWindow();
		if(getPortIP() && setUpConnection())
		{
				if(getUserName())
					try
					{
						new Thread(new ChatClientWindow()).start();  
					}
					catch (Exception e){}	
				else
					output.println("UnknownUser");
		}	
		else
            System.exit(0);
	}
	/**
	 * Send UserName to the Server
	 */
	public static boolean getUserName()
	{
		//Create Custom Input Form
		JTextField userName = new JTextField("Bob Johnson");
		JPanel userNamePanel = new JPanel(new GridLayout(0, 1));
		userNamePanel.add(new JLabel("UserName:"));
		userNamePanel.add(userName);
		int result = JOptionPane.showConfirmDialog(null, userNamePanel, "Choose your name!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) 
		{
			sendMessage(userName.getText());			
				return true;
		}
		else
			return false;
	}
	/**
	 * Send a message to the server
	 */
	public static void sendMessage(String msg)
	{
		try
		{
			output.println(msg);
		}
		catch (Exception e) {}
	}
	/**
	 * Set up a connection to the Server
	 */
	@SuppressWarnings("deprecation")
	public static boolean setUpConnection()
	{
		try
		{
			socket = new Socket(ipAddress, sendPort);
			output = new PrintStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());
			String line = input.readLine();
			if (line.contains("We're Sorry,"))
			{
				JOptionPane.showMessageDialog(null, line, "Server Full :(", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			else
			{
				window.chatField.append(line);
				return true;
			}
		}
		catch (Exception e) 
		{
			JOptionPane.showMessageDialog(null,"Unable to connect to Server", "Error :(", JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}	
	/**
	 * Get user to choose port and IP
	 */
	public static boolean getPortIP()
	{
		//Create Custom Input Form
		JTextField ipField = new JTextField("127.0.0.1");
		JTextField portField = new JTextField("12345");
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("IP Address:"));
		panel.add(ipField);
		panel.add(new JLabel("Port Number:"));
		panel.add(portField);
		int result = JOptionPane.showConfirmDialog(null, panel, "Welcome!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) 
		{
			ipAddress = ipField.getText();
			sendPort = Integer.parseInt(portField.getText());
			return true;
		}
		else
			return false;
	}
	/**
	 * Create the main Window
	 */
	public static void createWindow()
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					window = new ChatClientWindow();
					window.frmChatatron.setVisible(true);
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the application.
	 */
	public ChatClientWindow() 
	{
		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		//Create the Frame
		frmChatatron = new JFrame();
		frmChatatron.setTitle("Chat-a-tron 5000");
		frmChatatron.getContentPane().setBackground(SystemColor.controlHighlight);
		frmChatatron.setBounds(100, 100, 450, 300);
		frmChatatron.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmChatatron.getContentPane().setLayout(null);
		frmChatatron.addWindowListener(new java.awt.event.WindowAdapter() 
		{
			/**
			 * Listen for the form to close and close the connection
			 */
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
		    {
		        if (JOptionPane.showConfirmDialog(frmChatatron, "Are you sure to close this window?", "Really Closing? :(", 
		            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
		        {
		        	try
		        	{
		        		output.println("/quit");
		        	}
		        	catch(Exception e){}
		            System.exit(0);
		        }
		    }
		});
		
		//Create the Text Area
		sendText = new JTextField();
		sendText.setBounds(10, 208, 315, 42);
		sendText.setColumns(10);
		
		//Create the Button
		btnSend = new JButton("Send ");
		btnSend.setFont(new Font("Monotype Corsiva", Font.ITALIC, 20));
		btnSend.setBounds(335, 208, 89, 42);
		btnSend.addActionListener(new ActionListener() 
		{
			/**
			 * Listen for send button to be pressed
			 */
			public void actionPerformed(ActionEvent arg0) 
			{
				//Send Message
				if (!sendText.getText().isEmpty())
				{
					sendMessage(sendText.getText());
					sendText.setText("");
				}
			}
		});
		
		//Create the Chat Area
		chatField = new JTextArea();
		chatField.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		chatField.setEditable(false);
		chatField.setBounds(10, 11, 414, 190);
		chatField.setLineWrap(true);
		
		//Create ScrollPane
		JScrollPane scroll = new JScrollPane (chatField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setBounds(10, 11, 414, 190);
    
        //Set scroll to always on bottom
        caret = (DefaultCaret)chatField.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
		//Build the window
		frmChatatron.getContentPane().add(scroll);
		frmChatatron.getContentPane().add(sendText);
		frmChatatron.getContentPane().add(btnSend);
		frmChatatron.getRootPane().setDefaultButton(btnSend);
		frmChatatron.setResizable(false);
	}
	/**
	 * Run function for thread
	 */
	@SuppressWarnings("deprecation")
	public void run()
	{
		String inLine;
		try
		{
			while ((inLine = input.readLine()) != null)
			{
				if (inLine == "/quit")				
					break;				
				else
					window.chatField.append(inLine + "\n");

		        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			}
			output.close();
		    input.close();
		    socket.close();
		}
		catch (IOException e){}
	}
}