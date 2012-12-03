package server;

import gcm.HomeServlet;
import gcm.RegisterServlet;
import gcm.SendAll;
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
	private static int port = 0;
	// Tillåter klienter att ansluta till servern
	private static ServerSocket serverSocket = null;
	// En boolean som avgör om servern lyssnar på anslutningar
	private static boolean listening = true;
	// En ConcurrentHashMap som länkar ett IP till en OutputStream
	private ConcurrentHashMap<String, OutputStream> hashMap;
	private ConcurrentHashMap<String, String> gcmMap;
	private Socket clientSocket = null;
	private List<ModelInterface> list = null;
	private Database db = null;
	private static int jettyPort = 0;

	public static void main(String[] args) {
		int i = 0;
		for (String s : args) {
			if (i == 0) {
				port = Integer.parseInt(s);
			} else {
				jettyPort = Integer.parseInt(s);
			}
			i++;
		}
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
		final JettyServer jettyServer = new JettyServer(jettyPort);
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
			gcmMap = new ConcurrentHashMap<String, String>();
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
						System.out.println("HÄR BLIR DET NULL? " + gcmMap.get(cont.getContactName()));
						if (gcmMap.get(cont.getContactName()) != null) {
							System.out.println("gcmMap get: "
									+ gcmMap.get(cont.getContactName()));
							new SendAll().singleSend(gcmMap.get(cont
									.getContactName()));
						}
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
	public void removeClient(String usersIP) {
		hashMap.remove(usersIP);
	}

	public void addGcmClient(String name, String gcmId) {
		System.out.println(gcmMap.keySet() + " contains " + name +"?");
		if (!gcmMap.containsKey(name)) {
			System.out.println("Adding " + name + " to gcmMap");
			gcmMap.put(name, gcmId);
		} else if(!gcmMap.containsValue(gcmId)) {
			System.out.println("remove!!!");
			removeGcmClient(name);
			System.out.println("Adding " + name + " to gcmMap");
			gcmMap.put(name, gcmId);
		}
		System.out.println(gcmMap.keySet());
	}

	public void removeGcmClient(String name) {
		System.out.println("@Server(219)");
		if (gcmMap.containsKey(name)) {
			System.out.println("@Server(221)");
			list = db.getAllFromDB(new Contact());
			System.out.println("@Server(223)");
			for (ModelInterface m : list) {
				Contact cont = (Contact) m;
				if (name.equals(cont.getContactName())) {
					System.out.println("@Server(227)");
					if (gcmMap.get(cont.getContactName()) != null) {
						System.out.println("Sending logout to " + gcmMap.get(cont.getContactName()));
						new SendAll().sendLogout(gcmMap.get(cont.getContactName()));
					}
				}
			}
			System.out.println("Removing " + name + " from gcmMap");
			gcmMap.remove(name);
			System.out.println(gcmMap.keySet());
		}
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
						System.out
								.println("Sending " + qItem.getJSON()
										+ " from queue to "
										+ receiver.getContactName());
					}
				}
			} catch (Exception e) {
				System.out.println("catch: sendUnsentItems");
				System.err.println(e);
			}
		}
	}
}
