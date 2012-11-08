package server;

import java.util.List;

import model.Contact;
import model.ModelInterface;
import database.Database;

public class GetAllContactsCommand implements CommandInterface {

	Database db = new Database();
	private List<ModelInterface> list = null;

	public GetAllContactsCommand() {
		list = db.getAllFromDB(new Contact());
	}

	@Override
	public void commandTask() {
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			System.out.println("Kontakt namn: " + cont.getContactName());
		}
	}

	@Override
	public String commandLine() {
		return "allaKontakter";
	}

}
