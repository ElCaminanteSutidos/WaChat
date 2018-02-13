package com.El.Caminante.WaChat.Main;

import com.El.Caminante.WaChat.Client.Client;
import com.El.Caminante.WaChat.Server.Server;

public class Main {

	public static void main(String[] args) {
		boolean isServer = false;
		int port = 200;
		for(int i = 0; i < args.length; i++) {
			switch(args[i].toLowerCase()) {
			case "-server":
				isServer = true;
				break;
			case "-port":
				if(args.length > i)
					port = Integer.parseInt(args[i+1]);
				break;
			}
		}
		if (isServer) {
			new Server(port);
		} else {
			new Client();
		}
	}

}
