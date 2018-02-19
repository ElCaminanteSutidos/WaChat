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

public class Server {

	ServerSocket server;
	boolean running = false;
	ArrayList<ServerClient> clients;
	SystemTray tray;
	TrayIcon icon;
	ServerWindow serverWindow;

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

	public Server(int port) {
		serverWindow = new ServerWindow(this);
		clients = new ArrayList<ServerClient>();
		System.out.println("WaChat Server port=" + port + "!");
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(5000);
			running = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Create a pop-up menu components
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					stop();
				}
				
			});
			MenuItem showWindowItem = new MenuItem("Show Window");
			showWindowItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					serverWindow.setVisible(true);
				}
				
			});
			
			MenuItem hideWindowItem = new MenuItem("Hide Window");
			hideWindowItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					serverWindow.setVisible(false);
				}
				
			});

			// Add components to pop-up menu
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
	
	public void updateUserList() {
		String userList = "/users";
		for (int i = 0; i < clients.size(); i++) {
			userList += "/"+clients.get(i).username;
		}
		SendToAll(userList);
	}

	public void SendMSGToAll(String msg) {
		SendMSGToAllExcept(msg, null);
	}
	
	public void SendToAll(String msg) {
		SendToAllExcept(msg, null);
	}
	
	public void SendToAllExcept(String msg, ServerClient exception) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i) == exception)
				continue;
			clients.get(i).send(msg);
		}
	}

	public void SendMSGToAllExcept(String msg, ServerClient exception) {
		SendToAllExcept("/msg/" + msg, exception);
	}
	
	public void stop() {
		running = false;
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).terminate(false);
		}
		clients.clear();
		tray.remove(icon);
		serverWindow.dispose();
	}
	
	public void handleCMD(String cmd) {
		switch(cmd) {
		case "/stop":
			stop();
		default:
			SendMSGToAll("<SERVER> "+cmd);	
		}
	}

}
