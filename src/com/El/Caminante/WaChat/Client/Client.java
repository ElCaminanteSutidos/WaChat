package com.El.Caminante.WaChat.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	Socket server;
	BufferedReader input;
	PrintWriter output;
	boolean running = true;
	
	public Client() {
		Scanner inputCMD = new Scanner(System.in);
		try {
			server = new Socket("localhost", 200);
			input = new BufferedReader(new InputStreamReader(server.getInputStream()));
			output = new PrintWriter(server.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to connect to server");
			running = false;
		}
		
		while(running) {
			String line = inputCMD.nextLine();
			if(line != null) {
				output.println(line);
				output.flush();
				try {
					line = input.readLine();
					System.out.println(line);
				} catch (IOException e) {
				}
			}
		}
		
	}

}
