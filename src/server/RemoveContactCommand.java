package server;

import java.util.List;
import java.util.Scanner;

import model.Contact;
import model.ModelInterface;
import database.Database;

public class RemoveContactCommand implements CommandInterface {

	Database db = new Database();
	private List<ModelInterface> list = null;
	Scanner in = new Scanner(System.in);
	

	public RemoveContactCommand() {
		list = db.getAllFromDB(new Contact());
	}
	
	@Override
	public void commandTask() {
		System.out.print("Namn på kontakt som ska tas bort: ");
		String contactToBeDeleted = in.nextLine();
		System.out.print("Är du säker på att du vill ta bort " + contactToBeDeleted + " (y/n): ");
		String yesOrNo = in.nextLine();
		if(yesOrNo.equals("n")){
			System.out.println("Kontakten blev inte borttagen.");
		}else if(yesOrNo.equals("y")){
			for (ModelInterface m : list) {
				Contact cont = (Contact) m;
				if(contactToBeDeleted.equals(cont.getContactName())){
					db.deleteFromDB(cont);
					System.out.println("Kontakten " + contactToBeDeleted + " har blivit borttagen.");
				}
			}
		}else{
			System.out.println("Felaktig inmatning.");
		}
	}

	@Override
	public String commandLine() {
		return "raderaKontakt";
	}

}
