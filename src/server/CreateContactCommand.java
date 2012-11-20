package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Contact;
import model.ModelInterface;

import com.google.gson.Gson;

import database.Database;

/**
 * Ett kommando för att skapa en kontakt och skicka ut den till alla klienter
 * @author kristoffer
 *
 */
public class CreateContactCommand implements CommandInterface {

	private Database db = new Database();
	private Scanner in = new Scanner(System.in);
	private Server server;
	private List<ModelInterface> list;
	private boolean alreadyExists = false; 

	public CreateContactCommand(Server server) {
		this.server = server;
	}

	@Override
	public void commandTask() {
		try {
			Contact newContact = new Contact();
			System.out.print("Kontakt namn: ");
			newContact.setContactName(in.nextLine());
			System.out.print("Kontaktens IP: ");
			newContact.setInetAddress(in.nextLine());

			System.out
					.print("Är du nöjd med din nya insättning av kontakt? (y/n): ");
			String yesOrNo = in.nextLine();
			// Om användaren skriver 'n' så avbryts skapandet av den nya
			// kontakten
			if (yesOrNo.equals("n")) {
				System.out.println("Avbrutet.");
			} else if (yesOrNo.equals("y")) {
				String contact = new Gson().toJson(newContact);
				server.sendToAll(contact);
				list = db.getAllFromDB(new Contact());
				for (ModelInterface m : list) {
					Contact cont = (Contact) m;
					if(newContact.getContactName().equals(cont.getContactName())){
						cont.setInetAddress(newContact.getInetAddress());
						db.updateModel(cont);
						alreadyExists = true;
					}
				}
				if(alreadyExists == false){
					// Lägger till den nya kontakten till databasen
				db.addToDB(newContact);
				}
				
				System.out.println("Kontakt sparad.");
			} else {
				System.out.println("Felaktig inmatning.");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public String commandLine() {
		// TODO Auto-generated method stub
		return "skapaKontakt";
	}

}
