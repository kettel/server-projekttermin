package server;

import java.util.Scanner;

import model.Contact;
import database.Database;

/**
 * En terminal för servern där olika kommandon kan matas in för att utföra olika metoder
 * @author kristoffer
 *
 */
public class ServerTerminal extends Thread {

	private Scanner in = null;
	private Database db = null;
	private boolean listeningToCommands = true;

	public ServerTerminal() {
		in = new Scanner(System.in);
		db = new Database();
	}

	/**
	 * Lyssnar på olika kommandon för att sedan utföra en metod beroende på vad
	 * som skrivs in
	 */
	public void run() {
		while (listeningToCommands) {
			String command = in.nextLine();
			if (command.equals("skapaKontakt")) {
				createContact();
			} else {
				System.out.println("Kommandot existerar inte.");
			}
		}
	}

	/**
	 * Skapar en ny kontakt med den information som en kontakt behöver
	 */
	private void createContact() {
		try {
			Contact newContact = new Contact();
			System.out.print("Kontakt namn: ");
			newContact.setContactName(in.nextLine());
			System.out.print("Telefonnummer: ");
			newContact.setContactPhoneNumber(Long.valueOf(in.nextLine())
					.longValue());
			System.out.print("E-post: ");
			newContact.setContactEmail(in.nextLine());
			System.out.print("Behörighetsnivå: ");
			newContact.setContactClearanceLevel(in.nextLine());
			System.out.print("Klassificering: ");
			newContact.setContactClassification(in.nextLine());
			System.out.print("Kontaktbeskrivning: ");
			newContact.setContactComment(in.nextLine());

			System.out
					.print("Är du nöjd med din nya insättning av kontakt? (y/n): ");
			String yesOrNo = in.nextLine();
			// Om användaren skriver 'n' så avbryts skapandet av den nya kontakten
			if (yesOrNo.equals("n")) {
				System.out.println("Avbrutet.");
			} else if (yesOrNo.equals("y")) {
				// Lägger till den nya kontakten till databasen
				db.addToDB(newContact);
				System.out.println("Kontakt sparad.");
			} else {
				System.out.println("Felaktig inmatning.");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
