package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import model.Assignment;
import model.Contact;
import model.MessageModel;

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
	private PrintWriter output = null;
	private Database db = null;

	/**
	 * Konstruktorn, tar emot en socket för porten vi lyssnar på
	 * 
	 * @param socket
	 *            Den socket som anslutningen sker genom
	 */
	public MultiServerThread(Socket socket) {
		super("MultiServerThread");
		this.socket = socket;
		db = new Database();
	}

	/**
	 * Koden för den tråd som skapas för en ny anslutning.
	 */
	public void run() {

		try {
			// Buffrar ihop flera tecken från InputStreamen till en sträng
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			// Läser den buffrade strängen
			inputLine = input.readLine();

			// Trace: Ett meddelande/assingment/kontakt har tagits emot.
			System.out.println(socket.getInetAddress() + ": " + inputLine);

			// Bestämmer vilken typ av input som kommer in. När det avgjorts
			// sparas och/eller skickas input:en vidare.
			handleTypeOfInput(inputLine);

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
	 * @param message
	 * Json-strängen av meddelandet
	 */
	private void handleMessage(String message) {
		System.out.println("Sending message to database and/or forwarding it.");
		// Gson konverterar json-strängen till MessageModel-objektet igen
		MessageModel msg = (new Gson()).fromJson(message, MessageModel.class);
		// Lägger in meddelandet i databasen
		db.addToDB(msg);
		// ******Skicka vidare till enhet!********
		
	}

	/**
	 * Hanterar json-strängen om den är ett uppdrag
	 * @param assignment
	 * Json-strängen av uppdraget
	 */
	private void handleAssignment(String assignment) {

		// Gson konverterar json-strängen till Assignment-objektet igen.
		Assignment assignmentFromJson = (new Gson()).fromJson(assignment,
				Assignment.class);
		// Lägger in kontakten i databasen
		db.addToDB(assignmentFromJson);
		// *****Skicka vidare till enhet och databas!***********
	}

	/**
	 * Hanterar json-strängen om den är en kontakt
	 * @param contact
	 * Json-strängen av kontakten
	 */
	private void handleContact(String contact) {

		// Gson konverterar json-strängen till MessageModel-objektet igen.
		Contact contactFromJson = (new Gson()).fromJson(contact, Contact.class);
		// Lägger in uppdraget i databasen
		db.addToDB(contactFromJson);

		// *****Skicka vidare till enhet och databas!***********
	}
}