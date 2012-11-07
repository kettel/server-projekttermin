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
				System.out.println("Input: " + inputLine);
				if (inputLine.equals("exit")) {
					connected = false;
					break;
				}

				// Trace: Ett meddelande/assingment/kontakt har tagits emot.
				System.out.println(socket.getInetAddress() + " "
						+ socket.getPort() + ": " + inputLine);

				// Bestämmer vilken typ av input som kommer in. När det avgjorts
				// sparas och/eller skickas input:en vidare.
				handleTypeOfInput(inputLine);
			}

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
		} else {
			try {
				send("Did not recognise inputtype.", socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Did not recognise inputtype.");
		}
	}

	/**
	 * Hanterar json-strägen om den är ett meddelande
	 * 
	 * @param message
	 *            Json-strängen av meddelandet
	 */
	private void handleMessage(String message) {
		System.out.println("hashMap empty: " + hashMap.isEmpty() + " keySet: "
				+ hashMap.keySet());
		System.out.println("socket.inetAddress = " + socket.getInetAddress());
		MessageModel msg = null;
		// Gson konverterar json-strängen till MessageModel-objektet igen
		try {
			msg = (new Gson()).fromJson(message,
					MessageModel.class);
			// Lägger in meddelandet i databasen
			db.addToDB(msg);
		} catch (Exception e) {
			System.out.println(e);
		}

		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			System.out.println("cont name: " + cont.getContactName() + "    msg reciever: " + msg.getReciever() + "     cont inet: " + cont.getInetAddress());
			if (cont.getContactName().equals(msg.getReciever().toString())
					&& (hashMap.keySet().contains(cont.getInetAddress().toString()))) {
				System.out.println("Nu skickar den vidare skiten");
				send(message, hashMap.get(cont.getInetAddress().toString()));
			}
		}
	}

	/**
	 * Hanterar json-strängen om den är ett uppdrag
	 * 
	 * @param assignment
	 *            Json-strängen av uppdraget
	 */
	private void handleAssignment(String assignment) {

		// Gson konverterar json-strängen till Assignment-objektet igen.
		try {
			Assignment assignmentFromJson = (new Gson()).fromJson(assignment,
					Assignment.class);
			// Lägger in kontakten i databasen
			db.addToDB(assignmentFromJson);
		} catch (Exception e) {
			System.out.println(e);
		}

		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			// Skickar uppdraget till alla som är anslutna i
			// systemet förutom den som skickade uppdraget
			if (hashMap.keySet().contains(cont.getInetAddress().toString())
					&& !cont.getInetAddress().equals(
							socket.getInetAddress().toString())) {
				send(assignment, hashMap.get(cont.getInetAddress()));
			}
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
		try {
			Contact contactFromJson = (new Gson()).fromJson(contact,
					Contact.class);
			// Lägger in uppdraget i databasen
			db.updateModel(contactFromJson);
		} catch (Exception e) {
			System.out.println(e);
		}

		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			// Skickar den uppdaterade kontakten till alla som är anslutna i
			// systemet förutom den som skickade kontakten
			if (hashMap.keySet().contains(cont.getInetAddress().toString())
					&& !cont.getInetAddress().equals(
							socket.getInetAddress().toString()))
				send(contact, hashMap.get(cont.getInetAddress()));
		}
	}

	/**
	 * Skickar en sträng till en viss OutputStream
	 * 
	 * @param msg
	 *            Meddelandet som ska skickas
	 * @param output
	 *            Den OutputStream som meddelandet ska skickas till
	 */
	private void send(String msg, OutputStream output) {
		PrintWriter pr = new PrintWriter(output, true);
		pr.println(msg);
	}
}