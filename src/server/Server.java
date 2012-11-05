package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import model.Assignment;
import model.Contact;
import model.MessageModel;
import model.ModelInterface;

import database.Database;


/**
 * Servern som hanterar anslutningar mellan olika klienter
 * 
 * *******Fyll på*********
 * 
 * @author kristoffer & nikola
 * 
 */
public class Server {

	// Porten som används för anslutningar till servern
	private static final int port = 17234;
	// Tillåter klienter att ansluta till servern
	private static ServerSocket serverSocket = null;
	// En boolean som avgör om servern lyssnar på anslutningar
	private static boolean listening = true;

	public static void main(String[] args) {
		/*try {
			serverSocket = new ServerSocket(port);
			
			// Skapar en ny tråd som lyssnar på kommandon
			new ServerTerminal().start();
			// Lyssnar på anslutningar och skapar en ny tråd per anslutning så
			// länge servern lyssnar efter anslutningar
			while (listening) {
				new MultiServerThread(serverSocket.accept()).start();
			}
			// Stänger socketen, anslutningar är inte längre tillåtna
			serverSocket.close();
		} catch (IOException e) {
			System.out.println(e);
		}*/
		Database db = new Database();
		// Testa att lägga till i DB
		db.addToDB(new Contact("Nise",Long.valueOf("0130123"),"nisse@gdsasdf","s","A","Skön lirare","192.168.1.1"));
		byte[] fakeImage = null;
		db.addToDB(new Assignment("Katt i träd", Long.valueOf("12423423"),Long.valueOf("23423425"),"Kalle", "Nisse", "En katt i ett träd", "2 dagar", "Ej påbörjat", fakeImage, "Alstättersgata", "Lekplats"));
		db.addToDB(new MessageModel("Hejsan svejsan jättemycket!!!", "Kalle"));
		
		// Testa att hämta från databasen
		List<ModelInterface> testList = db.getAllFromDB(new MessageModel());
		for (ModelInterface m : testList) {
			// Hämta gammalt meddelande
			MessageModel mess = (MessageModel) m;
			
			// Skapa ett uppdaterat meddelande
			MessageModel messUpdate = new MessageModel(mess.getId(), "mjuhu!","höns",mess.getMessageTimeStamp());
			
			// Skriv det uppdaterade objektet till databasen
			db.updateModel(messUpdate);
		}
		
		testList = db.getAllFromDB(new Contact());
		for (ModelInterface m : testList) {
			Contact cont = (Contact) m;
			System.out.println("Kontakt: " + cont.getContactName());
		}
		
		testList = db.getAllFromDB(new Assignment());
		for (ModelInterface m : testList) {
			Assignment ass = (Assignment) m;
			System.out.println("Uppdrag: " + ass.getAssignmentDescription());
		}
		
		// Testa att uppdatera i databasen
		
	}
}
