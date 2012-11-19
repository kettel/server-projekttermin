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
	private CreateContactCommand createContactCommand = null;
	private GetAllContactsCommand getAllContacts = null;
	private RemoveContactCommand removeContact = null;
	Server server;

	public ServerTerminal(Server server) {
		in = new Scanner(System.in);
		this.server = server;
		createContactCommand = new CreateContactCommand(server);
		getAllContacts = new GetAllContactsCommand();
		removeContact = new RemoveContactCommand();
	}

	/**
	 * Lyssnar på olika kommandon för att sedan utföra en metod beroende på vad
	 * som skrivs in
	 */
	public void run() {
		while (listeningToCommands) {
			String command = in.nextLine();
			if (command.equals(createContactCommand.commandLine())) {
				createContactCommand.commandTask();
			}else if(command.equals(getAllContacts.commandLine())){
				getAllContacts.commandTask();
			} else if(command.equals(removeContact.commandLine())){
				removeContact.commandTask();
			}
			else if(command.equals("gcm")){
				new GCM().sendMessage();
			}else {
			
				System.out.println("Kommandot existerar inte.");
			}
		}
	}
}
