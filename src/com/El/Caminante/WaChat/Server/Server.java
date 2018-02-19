package com.El.Caminante.WaChat.Server;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.El.Caminante.WaChat.Server.GUI.ServerWindow;

/**
 * The main class for the server of the WaChat program
 * 
 * @author Samuel "ElCaminante" Walker
 *
 */
public class Server {

	/**
	 * The server socket that represents the socket that the clients will connect to
	 */
	private ServerSocket server;
	/**
	 * Whether or not the server should continue running
	 */
	private boolean running = false;
	/**
	 * An array of all clients connected to the server
	 */
	private ArrayList<ServerClient> clients;
	/**
	 * The System Tray
	 */
	private SystemTray tray;
	/**
	 * The icon in the system tray
	 */
	private TrayIcon icon;
	/**
	 * The window with the server info
	 */
	private ServerWindow serverWindow;

	/**
	 * The main function, just call the constructor of the class
	 * 
	 * @param args
	 *            the arguments passed to the program
	 */
	public static void main(String[] args) {
		int port = 200;
		for (int i = 0; i < args.length; i++) {
			switch (args[i].toLowerCase()) {
			case "-port":
				if (args.length > i)
					port = Integer.parseInt(args[i + 1]);
				break;
			}
		}
		new Server(port);
	}

	/**
	 * Constructs the server, and starts listening for connections on the specified
	 * port
	 * 
	 * @param port
	 *            The port the server should listen for connections on
	 */
	public Server(int port) {
		serverWindow = new ServerWindow(this);
		clients = new ArrayList<ServerClient>();
		System.out.println("WaChat Server port=" + port + "!");
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(5000);
			running = true;
		} catch (Exception e) {
			running = false;
			System.err.println("FAILED TO INSTANTIATE SERVER SOCKET");
			e.printStackTrace();
		}

		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			PopupMenu popup = new PopupMenu();
			icon = null;
			try {
				icon = new TrayIcon(ImageIO.read(new File("image/bulb.gif")), "WaChat Server");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					stop();
				}

			});
			MenuItem showWindowItem = new MenuItem("Show Window");
			showWindowItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					serverWindow.setVisible(true);
				}

			});

			MenuItem hideWindowItem = new MenuItem("Hide Window");
			hideWindowItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					serverWindow.setVisible(false);
				}

			});

			popup.add(exitItem);
			popup.addSeparator();
			popup.add(showWindowItem);
			popup.add(hideWindowItem);

			icon.setPopupMenu(popup);

			try {
				tray.add(icon);
			} catch (AWTException e) {
				System.out.println("TrayIcon could not be added.");
			}
		}

		Listen();

	}

	/**
	 * Listen for incoming connections, as long as running == true
	 */
	public void Listen() {
		while (running) {
			try {
				Socket client = server.accept();
				ServerClient sclient = new ServerClient(client, this);
				clients.add(sclient);
				new Thread(sclient, "ClientThread").start();
			} catch (IOException e) {
			}

		}
	}

	/**
	 * Update the Online user lists of all the clients
	 */
	public void updateUserList() {
		String userList = "/users";
		for (int i = 0; i < clients.size(); i++) {
			userList += "/" + clients.get(i).username;
		}
		SendToAll(userList);
	}

	/**
	 * Sends a message to all clients, just calls sendMSGToAllExcept with a null
	 * exception <br/>
	 * <br/>
	 * ONLY SENDS MESSAGES, WILL NOT SEND COMMANDS
	 * 
	 * @param msg
	 *            the message to be sent
	 */
	public void SendMSGToAll(String msg) {
		SendMSGToAllExcept(msg, null);
	}

	/**
	 * Sends a command to all clients, just calls sendToAllExcept with a null
	 * exception
	 * 
	 * @param msg
	 *            the command to be sent
	 */
	public void SendToAll(String msg) {
		SendToAllExcept(msg, null);
	}

	/**
	 * Sends a command to all clients except a certain one
	 * 
	 * @param msg
	 *            the command to be sent
	 * @param exception
	 *            the client to be skipped
	 */
	public void SendToAllExcept(String msg, ServerClient exception) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i) == exception)
				continue;
			clients.get(i).send(msg);
		}
	}

	/**
	 * Sends a message to all clients except a certain one.<br/>
	 * <br/>
	 * ONLY SENDS MESSAGES, WILL NOT SEND COMMANDS
	 * 
	 * @param msg
	 *            the message to be sent
	 * @param exception
	 *            the client to be skipped
	 */
	public void SendMSGToAllExcept(String msg, ServerClient exception) {
		SendToAllExcept("/msg/" + msg, exception);
	}

	/**
	 * Will stop the server from running.
	 */
	public void stop() {
		running = false;
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).terminate(false);
		}
		clients.clear();
		tray.remove(icon);
		serverWindow.dispose();
	}

	/**
	 * Handles the commands that come from the server window
	 * 
	 * @param cmd
	 *            the command to be handled
	 */
	public void handleCMD(String cmd) {
		switch (cmd) {
		case "/stop":
			stop();
		default:
			SendMSGToAll("<SERVER> " + cmd);
		}
	}

	/**
	 * Removes a client from the list of clients
	 * 
	 * @param c
	 *            the client to be removed
	 */
	public void removeClient(ServerClient c) {
		clients.remove(c);
	}

}
