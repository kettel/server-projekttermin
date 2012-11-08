package server;

import java.util.Scanner;

/**
 * En terminal för servern där olika kommandon kan matas in för att utföra olika
 * metoder
 * 
 * @author kristoffer
 * 
 */
public class ServerTerminal extends Thread {

	private Scanner in = null;
	private boolean listeningToCommands = true;
	private CreateContactCommand contactCommand = null;
	Server server;

	public ServerTerminal(Server server) {
		in = new Scanner(System.in);
		this.server = server;
		contactCommand = new CreateContactCommand(server);
	}

	/**
	 * Lyssnar på olika kommandon för att sedan utföra en metod beroende på vad
	 * som skrivs in
	 */
	public void run() {
		while (listeningToCommands) {
			String command = in.nextLine();
			if (command.equals(contactCommand.commandLine())) {
				contactCommand.commandTask();
			} else {
				System.out.println("Kommandot existerar inte.");
			}
		}
	}
}
