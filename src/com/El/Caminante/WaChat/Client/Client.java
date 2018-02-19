package com.El.Caminante.WaChat.Client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import com.El.Caminante.WaChat.Client.GUI.Chat;
import com.El.Caminante.WaChat.Client.GUI.Login;

/**
 * The main class for the client of the WaChat program
 * 
 * @author Samuel "ElCaminante" Walker
 *
 */
public class Client implements Runnable {

	/**
	 * the socket connected to the server
	 */
	private Socket server;
	/**
	 * the input stream for the server
	 */
	private BufferedReader input;
	/**
	 * the servers output stream
	 */
	private PrintWriter output;
	/**
	 * weather or not the client should continue running
	 */
	private boolean running = false;
	/**
	 * the window for the login dialog shown at the program startup
	 */
	private Login loginDialog;
	/**
	 * the chat box window
	 */
	private Chat chat;

	/**
	 * main function, just calls the constructor of the class
	 * 
	 * @param args
	 *            the arguments passed to the program
	 */
	public static void main(String[] args) {
		int port = 200;
		try {
			if (args.length > 0)
				port = Integer.parseInt(args[0]);
		} catch (Exception e) {
		}
		new Client(port);
	}

	/**
	 * constructor to initiate the client program
	 * 
	 * @param port
	 *            The default port the client will use to connect to the server
	 */
	public Client(int port) {
		loginDialog = new Login(this);
		loginDialog.txtPort.setText("" + port);
		loginDialog.setVisible(true);

	}

	/**
	 * Attempt to login to the server on the given ip and port
	 * 
	 * @param ip
	 *            the ip to connect to
	 * @param port
	 *            the port to connect to
	 * @return true if it connects successfully false otherwise
	 */
	public boolean Login(String ip, int port) {

		try {
			server = new Socket(ip, port);
			input = new BufferedReader(new InputStreamReader(server.getInputStream()));
			output = new PrintWriter(server.getOutputStream());
			server.setSoTimeout(1000);
		} catch (IOException e) {
			return false;
		}

		return true;

	}

	/**
	 * Opens the chat window, and starts the listeners for the window. and starts
	 * the listening thread
	 */
	public void start() {
		running = true;
		loginDialog.dispose();
		chat = new Chat(this);
		chat.setVisible(true);
		chat.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				output.println("/end/");
				output.flush();
				running = false;
				chat.dispose();
				output.close();
				try {
					input.close();
					server.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

		});
		chat.txtChattext.requestFocus();
		output.println("/Username/" + loginDialog.txtUsername.getText());
		output.flush();
		new Thread(this, "Client Recieve Thread").start();
	}

	/**
	 * The listening thread for the client, receives messages from the server, and
	 * processes them.
	 */
	public void run() {
		while (running) {
			String line = null;
			try {
				line = input.readLine();
			} catch (IOException e) {
				continue;
			}
			if (line.startsWith("/msg/")) {
				chat.println(line.substring(5));
				if (chat.isFocusOwner() == false) {
					chat.requestFocus();
					chat.txtChattext.requestFocus();
				}
			} else if (line.equals("/end/")) {
				JOptionPane.showMessageDialog(null, "The Server Has Been Closed.", "Server Terminated",
						JOptionPane.ERROR_MESSAGE);
				running = false;
				chat.dispose();
				output.close();
				try {
					input.close();
					server.close();
				} catch (Exception e) {
				}
			} else if (line.startsWith("/users/")) {
				chat.updateOnlineList(line.substring(7));
			} else if (line.equals("/ping/")) {
				output.println("/pong/");
				output.flush();
			}
		}
	}

	/**
	 * Sends a chat message to the server <br/>
	 * <br/>
	 * will only send as a message, will not send special commands
	 * 
	 * @param msg
	 *            the message to be sent
	 */
	public void SendChatMSG(String msg) {
		if (msg == null || msg.equals(""))
			return;
		output.println("/msg/" + msg);
		output.flush();
	}

}
