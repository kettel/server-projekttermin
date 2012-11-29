package server;

import gcm.HomeServlet;
import gcm.RegisterServlet;
import gcm.UnregisterServlet;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import jetty.JettyServer;
import model.Contact;
import model.ModelInterface;
import model.QueueItem;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

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

	public static void main(String[] args) {

		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new HomeServlet()), "/home");
		context.addServlet(new ServletHolder(new RegisterServlet()),
				"/register");
		context.addServlet(new ServletHolder(new UnregisterServlet()),
				"/unregister");
		// context.addServlet(new ServletHolder(new SendAllMessagesServlet()),
		// "/sendAll");
		final JettyServer jettyServer = new JettyServer();
		jettyServer.getServer().setHandler(context);
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				try {
					jettyServer.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		EventQueue.invokeLater(runner);
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
		list = db.getAllFromDB(new Contact());
		for (ModelInterface m : list) {
			Contact cont = (Contact) m;
			if (receiver.equals(cont.getContactName())) {
				// Om mottagaren är ansluten så skickas strängen
				if (hashMap.keySet().contains(cont.getInetAddress())) {
					PrintWriter pr = new PrintWriter(hashMap.get(cont
							.getInetAddress()), true);
					pr.println(stringToBeSent);
				} else {
					if (!stringToBeSent
							.contains("\"databaseRepresentation\":\"authentication\"")) {
						QueueItem qItem = new QueueItem(cont.getId(),
								stringToBeSent);
						db.addToDB(qItem);
					}
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
			if (hashMap.keySet().contains(cont.getInetAddress())) {
				PrintWriter pr = new PrintWriter(hashMap.get(cont
						.getInetAddress()), true);
				pr.println(stringToBeSent);
			} else {
				QueueItem qItem = new QueueItem(cont.getId(), stringToBeSent);
				db.addToDB(qItem);
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
			if (!sendersIP.equals(cont.getInetAddress())) {
				if (hashMap.keySet().contains(cont.getInetAddress())) {
					PrintWriter pr = new PrintWriter(hashMap.get(cont
							.getInetAddress()), true);
					pr.println(stringToBeSent);
				} else {
					QueueItem qItem = new QueueItem(cont.getId(),
							stringToBeSent);
					db.addToDB(qItem);
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
	
	/**
	 * Återsänder data som inte kommat fram till en viss mottagare
	 * 
	 * @param receiver
	 *            Kontakten som datan sänds till
	 */
	public void sendUnsentItems(Contact receiver) {
		if (receiver != null) {
			try {
				list = db.getAllFromDB(new QueueItem(receiver.getId()));
				if (!list.isEmpty()) {
					PrintWriter pr = new PrintWriter(hashMap.get(receiver
							.getInetAddress()), true);
					for (ModelInterface m : list) {
						QueueItem qItem = (QueueItem) m;
						pr.println(qItem.getJSON());
						db.deleteFromDB(qItem);
						System.out.println("Sending " + qItem.getJSON() + " from queue to " + receiver.getContactName());
					}
				}
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
}
