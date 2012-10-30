import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
	 * Den socket som anslutningen sker genom
	 */
	public MultiServerThread(Socket socket) {
		super("MultiServerThread");
		this.socket = socket;
	}

	/**
	 * Den kod som körs när tråden har skapats för en ny anslutning
	 */
	public void run() {

		try {
			// Buffrar ihop flera tecken från InputStreamen till en sträng
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			// Läser den buffrade strängen
			inputLine = input.readLine();
			System.out.println("Message: " + inputLine);

			// Stänger buffern
			input.close();
			// Stänger anslutningen
			socket.close();

		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
