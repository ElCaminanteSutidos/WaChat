package com.El.Caminante.WaChat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ServerClient implements Runnable {

	/**
	 * The reference to the server class
	 */
	private Server server;
	/**
	 * The socket of the client that this class represents
	 */
	private Socket client;
	/**
	 * the input stream for the client
	 */
	private BufferedReader input;
	/**
	 * the output stream for the client
	 */
	private PrintWriter output;
	/**
	 * whether the client should be running or not
	 */
	private boolean running = true;
	/**
	 * the username of the client
	 */
	String username = "Unknown";
	/**
	 * the timeout of each ping attempt to the client in milliseconds
	 */
	public static int TIMEOUT = 5000;
	/**
	 * the number of ping attempts to attempt before timing out a client
	 */
	public static int TIMEOUT_ATTEMPTS = 5;
	/**
	 * whether or not the client has responded sense the last ping attempt
	 */
	private boolean responded = true;
	/**
	 * the number of failed ping attempts sense the last successful one
	 */
	private int failedAttempts = 0;

	/**
	 * Creates a new ServerClient for the specified client, and on the specified
	 * server
	 * 
	 * @param client
	 *            the client that this server-client represents
	 * @param server
	 *            the server that this client is connected to
	 */
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

	/**
	 * Starts the listening for client messages and responds to them.
	 */
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
					} else if (line.equals("/pong/")) {
						responded = true;
					}
				}
			} catch (IOException e) {
				if (responded == false) {
					failedAttempts++;
					if (failedAttempts >= TIMEOUT_ATTEMPTS) {
						terminate(true);
						server.SendMSGToAll(username + " has Timed Out.");
					}
				} else {
					failedAttempts = 0;
					responded = false;
					output.println("/ping/");
					output.flush();
				}
			}

		}
	}

	/**
	 * Sends a message to this client
	 * 
	 * @param msg
	 *            the message to be sent
	 */
	public void send(String msg) {
		output.println(msg);
		output.flush();
	}

	/**
	 * Terminates the connection with this client
	 * 
	 * @param removeFromList
	 *            whether or not this client should remove itself from the list of
	 *            clients
	 */
	public void terminate(boolean removeFromList) {
		if (removeFromList)
			server.removeClient(this);
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
