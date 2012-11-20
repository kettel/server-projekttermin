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
	// En ConcurrentHashMap som länkar ett IP till en OutputStream
	private static ConcurrentHashMap<String, OutputStream> hashMap;
	private static Socket clientSocket = null;
	private List<ModelInterface> list = null;
	private Database db = null;
//	private static List<ModelInterface> unsentList = null;

	public static void main(String[] args) {
		new Server();
	}

	public Server() {
		try {
			db = new Database();
//			unsentList = new ArrayList<ModelInterface>();
			hashMap = new ConcurrentHashMap<String, OutputStream>();
			serverSocket = new ServerSocket(port);

			// Skapar en ny tråd som lyssnar på kommandon
			new ServerTerminal(this).start();
			// Lyssnar på anslutningar och skapar en ny tråd per anslutning så
			// länge servern lyssnar efter anslutningar
			while (listening) {
				clientSocket = serverSocket.accept();
				OutputStream out = clientSocket.getOutputStream();
				new MultiServerThread(clientSocket, this).start();
				hashMap.put(clientSocket.getInetAddress().toString(), out);
			}
			// Stänger socketen, anslutningar är inte längre tillåtna
			serverSocket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * Skickar till en specifik klient
	 * 
	 * @param stringToBeSent
	 *            Strängen som ska skickas
	 * @param receiver
	 *            Mottagarens namn
	 */
	public void send(String stringToBeSent, String receiver) {
		System.out.println("keySet: " + hashMap.keySet());
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			if (receiver.equals(cont.getContactName())) {
				// Om mottagaren är ansluten så skickas strängen
				if (hashMap.keySet().contains("/" + cont.getInetAddress())) {
					PrintWriter pr = new PrintWriter(hashMap.get("/"
							+ cont.getInetAddress()), true);
					pr.println(stringToBeSent);
				} else {
					cont.addUnsentItem(stringToBeSent);		
					db.addToDB(cont);
				}
			}
		}
	}

	/**
	 * Skickar till alla som är anslutna i systemet
	 * 
	 * @param stringToBeSent
	 *            Strängen som ska skickas
	 */
	public void sendToAll(String stringToBeSent) {
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			if (hashMap.keySet().contains("/" + cont.getInetAddress())) {
				PrintWriter pr = new PrintWriter(hashMap.get("/"
						+ cont.getInetAddress()), true);
				pr.println(stringToBeSent);
			} else {
				cont.addUnsentItem(stringToBeSent);
			}
		}
	}

	/**
	 * Skickar till alla som är anslutna i systemet förutom den som skickade
	 * strängen
	 * 
	 * @param stringToBeSent
	 *            Strängen som ska skickas
	 * @param sendersIP
	 *            IP:t på användaren som skickade strängen
	 */
	public void sendToAllExceptTheSender(String stringToBeSent, String sendersIP) {
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			if (!sendersIP.equals("/" + cont.getInetAddress())) {
				if (hashMap.keySet().contains("/" + cont.getInetAddress())) {
					PrintWriter pr = new PrintWriter(hashMap.get("/"
							+ cont.getInetAddress()), true);
					pr.println(stringToBeSent);
				} else {
					cont.addUnsentItem(stringToBeSent);
				}
			}
		}
	}

	/**
	 * Tar bort en användare från hashMapen
	 * 
	 * @param string
	 *            IP:t på användaren
	 */
	public synchronized void removeClient(String usersIP) {
		hashMap.remove(usersIP);
	}

	/*
	 * public synchronized void addUnsentItem(ModelInterface m) {
	 * unsentList.add(m); }
	 */

	public void sendUnsentItems(Contact receiver) {
		if(receiver != null){
		System.out.println(receiver.getContactName() + " " + receiver.getUnsentQueue());
		}
		if (receiver != null && !receiver.getUnsentQueue().isEmpty()) {
			PrintWriter pr = new PrintWriter(hashMap.get("/"
					+ receiver.getInetAddress()), true);
			for (String s : receiver.getUnsentQueue()) {
				pr.println(s);
				receiver.removeUnsentItem(s);
			}
		}

		/*
		 * if (!unsentList.isEmpty()) { for (ModelInterface m : unsentList) { if
		 * (m.getDatabaseRepresentation().equals("message")) { MessageModel msg
		 * = (MessageModel) m; if (msg.getReciever().toString()
		 * .equals(reciever.getContactName())) {
		 * System.out.println("sending old data"); pr.println(new
		 * Gson().toJson(msg)); unsentList.remove(msg); } } } }
		 */
	}
}
