package com.El.Caminante.WaChat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	ServerSocket server;
	boolean running = false;

	public Server(int port) {
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
		
		Listen();
		
	}

	public void Listen() {
		while (running) {
			try {
				Socket client = server.accept();
				ServerClient sclient = new ServerClient(client, this);
				new Thread(sclient, "ClientThread").start();
			} catch (IOException e) {
			}
			
		}
	}

}
