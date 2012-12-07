package server;

import gcm.SendAll;

import java.io.Console;
import java.util.List;
import java.util.Scanner;

import sip.InitSip;

import model.AuthenticationModel;
import model.Contact;
import model.ModelInterface;

import com.google.gson.Gson;

import database.Database;

/**
 * Ett kommando för att skapa en kontakt och skicka ut den till alla klienter
 * 
 * @author kristoffer
 * 
 */
public class CreateContactCommand implements CommandInterface {

	private Database db = new Database();
	private Scanner in = new Scanner(System.in);
	private Server server;
	private List<ModelInterface> list;
	private Console console;

	public CreateContactCommand(Server server) {
		this.server = server;
	}

	public void commandTask() {
		try {
			Contact newContact = new Contact();
			System.out.print("Kontakt namn: ");
			newContact.setContactName(in.nextLine());
			// System.out.print("Kontaktens IP: ");
			 newContact.setInetAddress("");
			 newContact.setGcmId("");
			String pw = readPw();
			System.out
					.print("Är du nöjd med din nya insättning av kontakt? (y/n): ");
			String yesOrNo = in.nextLine();
			// Om användaren skriver 'n' så avbryts skapandet av den nya
			// kontakten
			if (yesOrNo.equals("n")) {
				System.out.println("Avbrutet.");
			} else if (yesOrNo.equals("y")) {
				if(!checkIfContactAlreadyExist(newContact.getContactName())){
				String contact = new Gson().toJson(newContact);
				server.sendToAll(contact);
				new SendAll().sendAll();
				addToLogin(newContact, pw);
				System.out.println("Kontakt sparad.");
				
				// Uppdatera SIP-användarna när man har lagt till en ny användare
				if(InitSip.getIsStarted()){
					InitSip.setIsStarted(false);
				}
				InitSip.provisionUsers(db.getAllFromDB(new AuthenticationModel()));
				
				}else{
					System.out.println("En kontakt med det namnet finns redan.");
				}
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

	private void addToLogin(Contact c, String password) {
//		db.setReplicationStatus(true);
		db.addToDB(c);
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			if (c.getContactName().equals(cont.getContactName())) {
				db.addToDB(new AuthenticationModel(cont.getId(), cont.getContactName(), password));
			}
		}
//		db.setReplicationStatus(false);
	}
	
	private boolean checkIfContactAlreadyExist(String name){
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			if(name.equals(cont.getContactName())){
				return true;
			}
		}
		return false;
	}
	
	private String readPw(){
		char[] pw;
		if((console = System.console()) != null && (pw = console.readPassword("Password: ")) != null){
			return String.valueOf(pw);
		}
		return null;
	}
}
