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
	private Contact thisContact = null;
	private List<ModelInterface> list;
	private List<ModelInterface> hashList;
	private final String replicateServerIP = "/192.168.1.1";

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
	}

	/**
	 * Koden för den tråd som skapas för en ny anslutning.
	 */
	public void run() {

		try {
			while (connected) {
				// Buffrar ihop flera tecken från InputStreamen till en sträng
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				// Läser den buffrade strängen
				while ((inputLine = input.readLine()) != null
						&& !inputLine.equals("close")) {
//					System.out.println("<input from "
//							+ socket.getInetAddress().toString() + ":"
//							+ socket.getPort() + "> " + inputLine);
					handleTypeOfInput(inputLine);
				}
				connected = false;
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
		} else if (input.equals("logout")) {
			System.out.println("<" + thisContact.getContactName() + "> logout");
			handleLogout();
		} else if (input.equals("pull")) {
			System.out.println("<" + thisContact.getContactName() + "> pull");
			server.sendUnsentItems(thisContact);
			// Vid förfrågan skickas alla kontakter från databasen
		} else if (input.equals("getAllContacts")) {
			System.out.println("<" + thisContact.getContactName() + "> getAllContacts");
			handleContactRequest();
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
			if (!socket.getInetAddress().toString().equals(replicateServerIP)) {
				server.send(message, msg.getReciever().toString());
			}
			// Lägger in meddelandet i databasen
			db.addToDB(msg);
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("<" + socket.getInetAddress() + ":"
					+ socket.getPort() + " " + sdf.format(cal.getTime())
					+ "> message from " + msg.getSender() + " to "
					+ msg.getReciever() + ": " + msg.getMessageContent());

		} catch (Exception e) {
			System.out.println("catch: handleMessage");
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
			boolean alreadyExists = false;
			if (!socket.getInetAddress().toString().equals(replicateServerIP)) {
				list = db.getAllFromDB(new Assignment());
				if (list.size() > 0) {
					for (ModelInterface m : list) {
						Assignment ass = (Assignment) m;
						if (assignmentFromJson.getGlobalID().equals(
								ass.getGlobalID())) {
							alreadyExists = true;
							db.updateModel(assignmentFromJson);
							server.sendToAllExceptTheSender(assignment, socket
									.getInetAddress().toString());
						}
					}
					if (!alreadyExists) {
						db.addToDB(assignmentFromJson);
						server.sendToAllExceptTheSender(assignment, socket
								.getInetAddress().toString());
					}
				} else {
					db.addToDB(assignmentFromJson);
					server.sendToAllExceptTheSender(assignment, socket
							.getInetAddress().toString());
				}
			}
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("<" + socket.getInetAddress() + ":"
					+ socket.getPort() + " " + sdf.format(cal.getTime())
					+ "> assignment from " + assignmentFromJson.getSender()
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
			// Lägger in uppdraget i databasen
			if (!socket.getInetAddress().toString().equals(replicateServerIP)) {
				db.addToDB(contactFromJson);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Hanterar json-strängen om det är en login-request och skickar ett svar om
	 * det är ett korrekt login
	 * 
	 * @param login
	 *            Json-strängen av inloggningsförfrågan
	 * @return true om kontakten stämmer överens med befintlig kontakt ur
	 *         databasen, annars false
	 */
	private boolean handleLogin(String login) {
		try {
			System.out.println("Login request from: "
					+ socket.getInetAddress().toString());
			list = db.getAllFromDB(new Contact());
			AuthenticationModel loginFromJson = (new Gson().fromJson(login,
					AuthenticationModel.class));
			if (!socket.getInetAddress().toString().equals(replicateServerIP)) {
				hashList = db.getAllFromDB(new AuthenticationModel());
				for (ModelInterface m : list) {
					Contact cont = (Contact) m;
					if (loginFromJson.getUserName().equals(
							cont.getContactName())) {
						for (ModelInterface mi : hashList) {
							AuthenticationModel logMod = (AuthenticationModel) mi;
							if (cont.getId() == logMod.getContactId()
									&& loginFromJson.getPasswordHash().equals(
											logMod.getPasswordHash())) {
								cont.setInetAddress(socket.getInetAddress()
										.toString());
								cont.setGcmId(loginFromJson.getGcmId());
								server.addGcmClient(
										loginFromJson.getUserName(),
										loginFromJson.getGcmId());

								db.updateModel(cont);
								loginFromJson.setIsAccessGranted(true);
								String response = new Gson()
										.toJson(loginFromJson);
								server.send(response, cont.getContactName());
								thisContact = cont;
								System.out.println("<"
										+ socket.getInetAddress().toString()
										+ "> " + cont.getContactName()
										+ " connected.");
								return true;
							}
						}
					}
				}

				try {
					PrintWriter pr = new PrintWriter(socket.getOutputStream(),
							true);
					pr.println(login);
					System.out.println("<" + socket.getInetAddress().toString()
							+ "> failed to login.");
				} catch (Exception e) {
					System.out.println(e);
				}
			} else {
				for (ModelInterface m : list) {
					Contact cont = (Contact) m;
					if (loginFromJson.getUserName().equals(
							cont.getContactName())) {
						db.addToDB(new AuthenticationModel(cont.getId(),
								loginFromJson.getPasswordHash()));
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Skickar alla kontakter från databasen
	 */
	private void handleContactRequest() {
		try {
			list = db.getAllFromDB(new Contact());
			for (ModelInterface m : list) {
				Contact cont = (Contact) m;
				String contact = new Gson().toJson(cont);
				System.out.println("Sending contact " + cont.getContactName() + " to " + thisContact.getContactName());
				server.send(contact, thisContact.getContactName());
			}
		} catch (Exception e) {
			System.out.println("catch: handleContactRequest");
			System.out.println(e);
		}
	}
	
	private void handleLogout(){
		server.removeClient(socket.getInetAddress().toString());
		server.removeGcmClient(thisContact.getContactName());
	}
}
