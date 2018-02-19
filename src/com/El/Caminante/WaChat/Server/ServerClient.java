package com.El.Caminante.WaChat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ServerClient implements Runnable {

	Server server;
	Socket client;
	BufferedReader input;
	PrintWriter output;
	boolean running = true;
	String username = "Unknown";
	public static int TIMEOUT = 5000;
	public static int TIMEOUT_ATTEMPTS = 5;
	boolean responded = true;
	int failedAttempts = 0;

	public ServerClient(Socket client, Server server) {
		this.client = client;
		this.server = server;
		try {
			client.setSoTimeout(5000);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			System.err.println("Failed to Creat Input or Output Stream for client");
			e.printStackTrace();
		}
	}

	public void run() {
		while (running) {
			String line;
			try {
				if ((line = input.readLine()) != null) {
					if (line.startsWith("/msg/")) {
						server.SendMSGToAll("<" + username + "> " + line.split("/msg/")[1]);
					} else if (line.equals("/end/")) {
						terminate(true);
						server.SendMSGToAll(username + " has Disconnected.");
					} else if (line.contains("/Username/")) {
						username = line.split("/Username/")[1];
						server.SendMSGToAllExcept(username + " has Connected.", this);
						server.updateUserList();
					} else if(line.equals("/pong/")) {
						responded = true;
					}
				}
			}catch (IOException e) {
				if(responded == false) {
					failedAttempts++;
					if(failedAttempts >= TIMEOUT_ATTEMPTS) {
						terminate(true);
						server.SendMSGToAll(username + " has Timed Out.");
					}
				}else {
					failedAttempts = 0;
					responded = false;
					output.println("/ping/");
					output.flush();
				}
			}

		}
	}

	public void send(String msg) {
		output.println(msg);
		output.flush();
	}

	public void terminate(boolean removeFromList) {
		if (removeFromList)
			server.clients.remove(this);
		running = false;
		output.println("/end/");
		output.flush();
		output.close();
		try {
			input.close();
			client.close();
		} catch (IOException e) {
		}
		server.updateUserList();
	}

}
