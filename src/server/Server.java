package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import model.Contact;
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
	private static ConcurrentHashMap<String, OutputStream> hashMap;
	private static Socket client = null;
	private List<ModelInterface> list = null;
	private Database db = null;

	public static void main(String[] args) {
		new Server();
	}

	public Server() {
		try {
			db = new Database();
			hashMap = new ConcurrentHashMap<String, OutputStream>();
			serverSocket = new ServerSocket(port);

			// Skapar en ny tråd som lyssnar på kommandon
			new ServerTerminal(this).start();
			// Lyssnar på anslutningar och skapar en ny tråd per anslutning så
			// länge servern lyssnar efter anslutningar
			while (listening) {
				client = serverSocket.accept();
				OutputStream out = client.getOutputStream();
				// skapa output
				// länka med ip med client.getoutput?
				new MultiServerThread(client, this).start();
				hashMap.put(client.getInetAddress().toString(), out);
			}
			// Stänger socketen, anslutningar är inte längre tillåtna
			serverSocket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void send(String msg, String reciever) {
		System.out.println("hashMap empty: " + hashMap.isEmpty() + " keySet: "
				+ hashMap.keySet());
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			if (reciever.equals(cont.getContactName()) && hashMap.keySet().contains("/" + cont.getInetAddress().toString())) {
				PrintWriter pr = new PrintWriter(hashMap.get("/"+cont.getInetAddress()), true);
				pr.println(msg);
			}
		}
	}

	public void sendToAll(String msg) {
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			if (hashMap.keySet().contains("/"+cont.getInetAddress().toString())) {
				PrintWriter pr = new PrintWriter(hashMap.get("/"+cont
						.getInetAddress()), true);
				pr.println(msg);
			}
		}
	}

	public void removeClient(String string) {
		hashMap.remove(string);
	}
}
