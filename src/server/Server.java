package server;

import gcm.Datastore;
import gcm.HomeServlet;
import gcm.RegisterServlet;
import gcm.SendAll;
import gcm.UnregisterServlet;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import jetty.JettyServer;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import model.Contact;
import model.ModelInterface;
import model.QueueItem;
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
	public static int port = 0;
	// Tillåter klienter att ansluta till servern
	private static SSLServerSocket serverSocket = null;
	// En boolean som avgör om servern lyssnar på anslutningar
	private static boolean listening = true;
	// En ConcurrentHashMap som länkar ett IP till en OutputStream
	private ConcurrentHashMap<String, OutputStream> hashMap;
	private ConcurrentHashMap<String, String> gcmMap;
	private SSLSocket clientSocket = null;

	private List<ModelInterface> list = null;
	private Database db = null;
	private static int jettyPort = 0;
	char keystorepass[] = "password".toCharArray();
	char keypassword[] = "password".toCharArray();
	char truststorepass[] = "password".toCharArray();

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
		System.out.println("porten är"+port);
		try {
			db = new Database();
			hashMap = new ConcurrentHashMap<String, OutputStream>();
			gcmMap = new ConcurrentHashMap<String, String>();
			List<ModelInterface> contactList = db.getAllFromDB(new Contact());
			for (ModelInterface m : contactList) {
				Contact cont = (Contact) m;
				Datastore.register(cont.getGcmId());
			}
			KeyStore ts = KeyStore.getInstance("JKS");
			
			ts.load(new FileInputStream(new File(getClass().getClassLoader().getResource("cert/servertruststore.jks").getPath())),truststorepass);

			TrustManagerFactory tmf = TrustManagerFactory
            .getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ts);
	
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(new File(getClass().getClassLoader().getResource("cert/server.jks").getPath())),keystorepass);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, keypassword);
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
			SSLServerSocketFactory ssf = sslcontext.getServerSocketFactory();

			serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
			// Skapar en ny tråd som lyssnar på kommandon
			new ServerTerminal(this).start();
			// Lyssnar på anslutningar och skapar en ny tråd per anslutning så
			// länge servern lyssnar efter anslutningar
			while (listening) {
				clientSocket = (SSLSocket) serverSocket.accept();
				OutputStream out = clientSocket.getOutputStream();
				new MultiServerThread(clientSocket, this).start();
				hashMap.put(clientSocket.getInetAddress().toString(), out);
			}
			// Stänger socketen, anslutningar är inte längre tillåtna
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
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
					System.out.println("Skickar enbart till " + receiver);
					System.out.println("hashMap: " + hashMap.keySet());
					pr.println(stringToBeSent);
				} else {
					if (!stringToBeSent
							.contains("\"databaseRepresentation\":\"authentication\"")) {
						QueueItem qItem = new QueueItem(cont.getId(),
								stringToBeSent);
						db.addToDB(qItem);
						System.out.println("kollar gcmMap för skickning av notifikation");
						if (gcmMap.get(cont.getContactName()) != null) {
							System.out.println("Skickar en notifikation till " + cont.getContactName());
							System.out.println("gcmMap: " + gcmMap.keySet());
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
		if (gcmId.length() > 0) {
			if (!gcmMap.containsKey(name)) {
				System.out.println("Adding " + name + " to gcmMap");
				gcmMap.put(name, gcmId);
			} else if (!gcmMap.get(name).equals(gcmId)) {
				removeGcmClient(name);
				System.out.println("Adding " + name + " to gcmMap");
				gcmMap.put(name, gcmId);
			}
		}
	}

	public void removeGcmClient(String name) {
		if (gcmMap.containsKey(name)) {
			list = db.getAllFromDB(new Contact());
			for (ModelInterface m : list) {
				Contact cont = (Contact) m;
				if (name.equals(cont.getContactName())) {
					if (gcmMap.get(cont.getContactName()) != null) {
						System.out.println("Sending logout to "
								+ gcmMap.get(cont.getContactName()));
						new SendAll().sendLogout(gcmMap.get(cont
								.getContactName()));
					}
				}
			}
			System.out.println("Removing " + name + " from gcmMap");
			gcmMap.remove(name);
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
			System.out.println("@Server(255)");
			try {
				System.out.println("@Server(257)");
				list = db.getAllFromDB(new QueueItem(receiver.getId()));
				System.out.println("@Server(259)");
				if (!list.isEmpty()) {
					System.out.println("@Server(261)");
					System.out.println("keySet: " + hashMap.keySet() + " values: " + hashMap.values());
					PrintWriter pr = new PrintWriter(hashMap.get(receiver
							.getInetAddress()), true);
					System.out.println("@Server(264)");
					for (ModelInterface m : list) {
						System.out.println("@Server(266)");
						QueueItem qItem = (QueueItem) m;
						System.out.println("@Server(268)");
						pr.println(qItem.getJSON());
						System.out.println("@Server(270)");
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
