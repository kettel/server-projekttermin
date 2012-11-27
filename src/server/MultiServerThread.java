package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import model.Assignment;
import model.AuthenticationModel;
import model.Contact;
import model.LoginModel;
import model.MessageModel;
import model.ModelInterface;

import com.google.gson.Gson;

import database.Database;

/**
 * Tråden för en enskild anslutning till servern
 * 
 * @author kristoffer & nikola
 * 
 */
public class MultiServerThread extends Thread {

	private Socket socket = null;
	private BufferedReader input = null;
	private String inputLine;
	private Database db = null;

	private boolean connected = true;
	private Server server = null;
	private Contact thisContact;
	private List<ModelInterface> list;
	private List<ModelInterface> hashList;

	/**
	 * Konstruktorn, tar emot en socket för porten vi lyssnar på och en Server
	 * som kan skicka vidare data
	 * 
	 * @param socket
	 *            Den socket som anslutningen sker genom
	 * @param server
	 *            Servern som hanterar alla anslutningar och som kan skicka
	 *            vidare data
	 */
	public MultiServerThread(Socket socket, Server server) {
		super("MultiServerThread");
		this.socket = socket;
		this.server = server;
		db = new Database();
		if(socket.getInetAddress().toString().equals("/192.168.1.1")){
			db.setReplicationStatus(false);
		}
		else {
			db.setReplicationStatus(true);
		}
	}

	/**
	 * Koden för den tråd som skapas för en ny anslutning.
	 */
	public void run() {

		try {
			server.sendUnsentItems(thisContact);

			while (connected) {
				// Buffrar ihop flera tecken från InputStreamen till en sträng
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				// Läser den buffrade strängen
				inputLine = input.readLine();
				if (inputLine != null) {

					if (inputLine.equals("exit")) {
						connected = false;
						break;
					}

					// Bestämmer vilken typ av input som kommer in. När det
					// avgjorts
					// sparas och/eller skickas input:en vidare.
					handleTypeOfInput(inputLine);
				}
			}
			// Tar bort kontakten från hashMapen med de anslutna klienterna
			server.removeClient(socket.getInetAddress().toString());
			// Stänger buffern
			input.close();
			// Stänger anslutningen
			socket.close();

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * Bestämmer vilken typ av objekt som kommer in och hanterar den efter
	 * preferenser den anger.
	 * 
	 * @param input
	 *            Json strängen.
	 */
	private void handleTypeOfInput(String input) {
		if (input.contains("\"databaseRepresentation\":\"message\"")) {
			handleMessage(input);
		} else if (input.contains("\"databaseRepresentation\":\"assignment\"")) {
			handleAssignment(input);
		} else if (input.contains("\"databaseRepresentation\":\"contact\"")) {
			handleContact(input);
		} else if (input
				.contains("\"databaseRepresentation\":\"authentication\"")) {
			if (!handleLogin(input)) {
				connected = false;
			}
		} else if (input.equals("Heart")) {
		} else {
			System.out.println("<" + socket.getInetAddress()
					+ "> Did not recognise inputtype.	" + inputLine);
		}
	}

	/**
	 * Hanterar json-strägen om den är ett meddelande och skickar denna till en
	 * specifik kontakt
	 * 
	 * @param message
	 *            Json-strängen av meddelandet
	 */
	private void handleMessage(String message) {

		MessageModel msg = new MessageModel();
		// Gson konverterar json-strängen till MessageModel-objektet igen
		try {
			msg = (new Gson()).fromJson(message, MessageModel.class);
			server.send(message, msg.getReciever().toString());
			// Lägger in meddelandet i databasen
			db.addToDB(msg);
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("<" + socket.getInetAddress() + ":"
					+ socket.getPort() + "	" + sdf.format(cal.getTime())
					+ "> message from  " + msg.getSender() + " to "
					+ msg.getReciever() + ": " + msg.getMessageContent());

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Hanterar json-strängen om den är ett uppdrag och skickar ut den till alla
	 * förutom den som skickade uppdraget
	 * 
	 * @param assignment
	 *            Json-strängen av uppdraget
	 */
	private void handleAssignment(String assignment) {
		// Gson konverterar json-strängen till Assignment-objektet igen.
		try {
			Assignment assignmentFromJson = (new Gson()).fromJson(assignment,
					Assignment.class);
			server.sendToAllExceptTheSender(assignment, socket.getInetAddress()
					.toString());

			// Lägger in kontakten i databasen
			db.addToDB(assignmentFromJson);
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("<" + socket.getInetAddress() + ":"
					+ socket.getPort() + "	" + sdf.format(cal.getTime())
					+ "> assignment from  " + assignmentFromJson.getSender()
					+ ": " + assignmentFromJson.getName() + "	"
					+ assignmentFromJson.getAssignmentStatus());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Hanterar json-strängen om den är en kontakt och skickar ut denna till
	 * alla
	 * 
	 * @param contact
	 *            Json-strängen av kontakten
	 */
	private void handleContact(String contact) {

		// Gson konverterar json-strängen till MessageModel-objektet igen.
		try {
			Contact contactFromJson = (new Gson()).fromJson(contact,
					Contact.class);
			server.sendToAllExceptTheSender(contact, socket.getInetAddress()
					.toString());
			// Lägger in uppdraget i databasen
			db.updateModel(contactFromJson);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private boolean handleLogin(String login) {
//		try {
			AuthenticationModel loginFromJson = (new Gson().fromJson(login,
					AuthenticationModel.class));
			System.out.println("MST 218");
			list = db.getAllFromDB(new Contact());
			System.out.println("MST 220");
			hashList = db.getAllFromDB(new LoginModel());
			System.out.println("MST 222");
			for (ModelInterface m : list) {
				System.out.println("MST 224");
				Contact cont = (Contact) m;
				System.out.println("MST 226");
				if (loginFromJson.getUserName().equals(cont.getContactName())) {
					System.out.println("MST 228");
					for (ModelInterface mi : hashList) {
						System.out.println("MST 230");
						LoginModel logMod = (LoginModel) mi;
						System.out.println("MST 232");
						if (loginFromJson.getPasswordHash().equals(
								logMod.getPassword())) {
							System.out.println("MST 235");
							cont.setInetAddress(socket.getInetAddress()
									.toString());
							System.out.println("MST 238");
							db.updateModel(cont);
							System.out.println("MST 240");
							loginFromJson.setIsAccessGranted(true);
							System.out.println("MST 242");
							String response = new Gson().toJson(loginFromJson);
							System.out.println("MST 244");
							server.send(response, cont.getContactName());
							System.out
									.println("response to login: " + response);
							return true;
						}
					}
				}
			}

//		} catch (Exception e) {
//			System.out.println(e);
//		}
		try {
			PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
			pr.println(login);
			System.out.println(socket.getInetAddress().toString() + " failed to login.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
}
