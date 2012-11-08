package server;

import java.util.List;

import model.Contact;
import model.ModelInterface;
import database.Database;

public class RemoveContactCommand implements CommandInterface {

	Database db = new Database();
	private List<ModelInterface> list = null;
	String contactToBeDeleted;

	public RemoveContactCommand(String contactToBeDeleted) {
		list = db.getAllFromDB(new Contact());
		this.contactToBeDeleted = contactToBeDeleted;
	}
	
	@Override
	public void commandTask() {
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			if(contactToBeDeleted.equals(cont.getContactName())){
				db.deleteFromDB(cont);
			}
		}
	}

	@Override
	public String commandLine() {
		return "raderaKontakt";
	}

}
