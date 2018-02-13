package com.El.Caminante.WaChat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClient implements Runnable {
	
	Server server;
	Socket client;
	BufferedReader input;
	PrintWriter output;
	
	
	public ServerClient(Socket client, Server server) {
		this.client = client;
		this.server = server;
		try {
			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			System.err.println("Failed to Creat Input or Output Stream for client");
			e.printStackTrace();
		}
	}

	public void run() {
		while(server.running) {
			String line;
			try {
				if((line = input.readLine()) != null) {
					System.out.println(line);
					output.println(line);
					output.flush();
					if(line.equals("stop")) {
						server.running = false;
					}
				}
			} catch (IOException e) {
			}
			
		}
	}

}
