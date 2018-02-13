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

public class Client implements Runnable{
	
	Socket server;
	BufferedReader input;
	PrintWriter output;
	boolean running = false;
	Login loginDialog;
	Chat chat;
	
	public static void main(String[] args) {
		int port = 200;
		try {
			if (args.length > 0)
				port = Integer.parseInt(args[0]);
		} catch (Exception e) {
		}
		new Client(port);
	}
	
	public Client(int port) {
		
		loginDialog = new Login(this);
		loginDialog.txtPort.setText(""+port);
		loginDialog.setVisible(true);
		
	}
	
	public boolean Login(String ip, int port) {
		
		try {
			server = new Socket(ip, port);
			input = new BufferedReader(new InputStreamReader(server.getInputStream()));
			output = new PrintWriter(server.getOutputStream());
			server.setSoTimeout(1000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		return true;
		
	}
	
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
        			// TODO Auto-generated catch block
        			ex.printStackTrace();
        		}
            }

        });
		chat.txtChattext.requestFocus();
		output.println("/Username/"+loginDialog.txtUsername.getText());
		output.flush();
		new Thread(this, "Client Recieve Thread").start();
	}
	
	public void run() {
		while(running) {
			String line = null;
			try {
				line = input.readLine();
			} catch (IOException e) {
				continue;
			}
			if(line.startsWith("/msg/")) {
				chat.println(line.substring(5));
				if(chat.isFocusOwner() == false) {
					chat.requestFocus();
					chat.txtChattext.requestFocus();
				}
			}else if(line.equals("/end/")) {
				JOptionPane.showMessageDialog(null,
					    "The Server Has Been Closed.",
					    "Server Terminated",
					    JOptionPane.ERROR_MESSAGE);
				running = false;
				chat.dispose();
				output.close();
				try {
					input.close();
					server.close();
				}catch(Exception e) {
				}
			}
		}
	}
	
	public void SendChatMSG(String msg) {
		if(msg == null || msg.equals(""))
			return;
		output.println("/msg/"+msg);
		output.flush();
	}

}
