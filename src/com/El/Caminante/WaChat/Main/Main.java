package com.El.Caminante.WaChat.Main;

public class Main {

	public static void main(String[] args) {
		boolean isServer = false;
		if (args.length > 0) {
			isServer = args[0].toLowerCase().equals("-server");
		}
		new Main(isServer);
	}

	public Main(boolean isServer) {
		if (isServer) {
			System.out.println("WaChat Server!");
		} else {
			System.out.println("WaChat Client!");
		}

	}

}
