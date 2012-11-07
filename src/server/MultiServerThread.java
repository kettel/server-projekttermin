package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import model.Assignment;
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
	private List<ModelInterface> list = null;
	private boolean connected = true;
	private ConcurrentHashMap<String, OutputStream> hashMap = null;

	/**
	 * Konstruktorn, tar emot en socket för porten vi lyssnar på
	 * 
	 * @param socket
	 *            Den socket som anslutningen sker genom
	 */
	public MultiServerThread(Socket socket,
			ConcurrentHashMap<String, OutputStream> hashMap) {
		super("MultiServerThread");
		this.socket = socket;
		this.hashMap = hashMap;
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
				inputLine = input.readLine();
				if(inputLine.equals("exit")){
					connected = false;
				}

				// Trace: Ett meddelande/assingment/kontakt har tagits emot.
				System.out.println(socket.getInetAddress() + " "
						+ socket.getPort() + ": " + inputLine);

				// Bestämmer vilken typ av input som kommer in. När det avgjorts
				// sparas och/eller skickas input:en vidare.
				handleTypeOfInput(inputLine);
			}
			hashMap.remove(socket.getInetAddress().toString(), socket.getOutputStream());
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
	 * @param br
	 *            Den buffrade strängen.
	 */
	private void handleTypeOfInput(String input) {

		if (input.contains("\"databaseRepresentation\":\"message\"")) {
			handleMessage(input);
		} else if (input.contains("\"databasetRepresentation\":\"assignment\"")) {
			handleAssignment(input);
		} else if (input.contains("\"databasetRepresentation\":\"contact\"")) {
			handleContact(input);
		} else
			System.out.println("Did not recognise inputtype.");
	}

	/**
	 * Hanterar json-strägen om den är ett meddelande
	 * 
	 * @param message
	 *            Json-strängen av meddelandet
	 */
	private void handleMessage(String message) {
		System.out.println("Sending message to database and/or forwarding it.");
		System.out.println("hashMap empty: " + hashMap.isEmpty() + " keySet: " + hashMap.keySet());
		System.out.println("socket.inetAddress = " + socket.getInetAddress());
		// Gson konverterar json-strängen till MessageModel-objektet igen
		MessageModel msg = (new Gson()).fromJson(message, MessageModel.class);
		// Lägger in meddelandet i databasen
		db.addToDB(msg);
		// ******Skicka vidare till enhet!********
		list = db.getAllFromDB(new Contact());
		
		//for (ModelInterface m : list) {
			//Contact cont = (Contact) m;
			if (/*cont.getContactName().equals(msg.getReciever())
					&& */(hashMap.keySet().contains(socket.getInetAddress().toString()))) {
				send(message, hashMap.get(socket.getInetAddress().toString()));
				System.out.println();
				System.out.println("**********Sending: " + message);
				System.out.println("**********To: " + socket.getInetAddress());
				System.out.println("hashMap empty: " + hashMap.isEmpty());
				System.out.println();
			}
		//}
	}

	/**
	 * Hanterar json-strängen om den är ett uppdrag
	 * 
	 * @param assignment
	 *            Json-strängen av uppdraget
	 */
	private void handleAssignment(String assignment) {

		// Gson konverterar json-strängen till Assignment-objektet igen.
		Assignment assignmentFromJson = (new Gson()).fromJson(assignment,
				Assignment.class);
		// Lägger in kontakten i databasen
		db.addToDB(assignmentFromJson);
		// *****Skicka vidare till enhet och databas!***********
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			Server.send(assignment, hashMap.get(cont.getInetAddress()));
		}
	}

	/**
	 * Hanterar json-strängen om den är en kontakt
	 * 
	 * @param contact
	 *            Json-strängen av kontakten
	 */
	private void handleContact(String contact) {

		// Gson konverterar json-strängen till MessageModel-objektet igen.
		Contact contactFromJson = (new Gson()).fromJson(contact, Contact.class);
		// Lägger in uppdraget i databasen
		db.updateModel(contactFromJson);

		// *****Skicka vidare till enhet och databas!***********
	}
	
	private void send(String msg, OutputStream output) {
		System.out.println("SKICKARÅÅÅÅÅÅÅÅÅ");
		PrintWriter pr = new PrintWriter(output, true);
		pr.println(msg);
	}
}