import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;

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

	/**
	 * Konstruktorn, tar emot en socket för porten vi lyssnar på
	 * 
	 * @param socket
	 *            Den socket som anslutningen sker genom
	 */
	public MultiServerThread(Socket socket) {
		super("MultiServerThread");
		this.socket = socket;
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
			System.out.println("Inkommande flygpost");

			// Bestämmer vilken typ av input som kommer in. När det avgjorts
			// sparas och/eller skickas input:en vidare.
			handleTypeOfInput(input);

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
	private void handleTypeOfInput(BufferedReader br) {

		String input = br.toString();
		String inputType = input.substring(27, 34);

		if (inputType.equals("message")) {
			System.out
					.println("Sending message to database and/or forwarding it.");

			// Gson konverterar json-strängen till MessageModel-objektet igen
			// och handleMsg skickar och sparar meddelandet.
			MessageModel msg = (new Gson()).fromJson(input, MessageModel.class);

		} else if (inputType.equals("contact")) {
			// Spara och/eller skicka vidare uppdraget.

			// Gson konverterar json-strängen till Assignment-objektet igen.
			Assignment assignmentFromJson = (new Gson()).fromJson(input,
					Assignment.class);

			// *****Skicka vidare till enhet och databas!***********
		} else if (inputType.equals("assignm")) {
			// Spara och/eller skicka vidare uppdraget.

			// Gson konverterar json-strängen till MessageModel-objektet igen.
			Contact contactFromJson = (new Gson()).fromJson(input,
					Contact.class);

			// *****Skicka vidare till enhet och databas!***********
		} else
			System.out.println("Did not recognise inputtype.");
	}

}
