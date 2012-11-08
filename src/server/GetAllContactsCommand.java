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
		System.out.println(list);
	}

	@Override
	public String commandLine() {
		return "allaKontakter";
	}

}
