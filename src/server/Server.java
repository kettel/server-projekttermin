package server;
import java.io.IOException;
import java.net.ServerSocket;

import model.MessageModel;

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
		db.addToDB(new MessageModel());
	}
}
