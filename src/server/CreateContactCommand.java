package server;

import java.util.Scanner;

import model.Contact;

import com.google.gson.Gson;

import database.Database;

public class CreateContactCommand implements CommandInterface {

	Database db = new Database();
	Scanner in = new Scanner(System.in);
	Server server;

	public CreateContactCommand(Server server) {
		this.server = server;
	}

	/**
	 * Skapar en ny kontakt med den information som en kontakt behöver
	 */
	@Override
	public void commandTask() {
	//	try {
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
			System.out.print("Kontaktens IP: ");
			newContact.setInetAddress(in.nextLine());

			System.out.print("Är du nöjd med din nya insättning av kontakt? (y/n): ");
			String yesOrNo = in.nextLine();
			// Om användaren skriver 'n' så avbryts skapandet av den nya
			// kontakten
			if (yesOrNo.equals("n")) {
				System.out.println("Avbrutet.");
			} else if (yesOrNo.equals("y")) {
				// Lägger till den nya kontakten till databasen
				db.addToDB(newContact);
				String contact = new Gson().toJson(newContact);
				System.out.println(server);
				server.sendToAll(contact);
				System.out.println("Kontakt sparad.");
			} else {
				System.out.println("Felaktig inmatning.");
			}
	//	} catch (Exception e) {
	//		System.out.println(e);
		//}

	}

	@Override
	public String commandLine() {
		// TODO Auto-generated method stub
		return "skapaKontakt";
	}

}
