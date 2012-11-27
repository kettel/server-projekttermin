package server;

import java.util.List;

import model.Contact;
import model.ModelInterface;
import database.Database;

/**
 * Ett kommando för att se alla kontakter i databasen
 * @author kristoffer
 *
 */
public class GetAllContactsCommand implements CommandInterface {

	Database db = new Database();
	private List<ModelInterface> list = null;

	@Override
	public void commandTask() {
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			System.out.println("Kontakt namn: " + cont.getContactName() + " - IP: " + cont.getInetAddress());
		}
	}

	public String commandLine() {
		return "allaKontakter";
	}
}
