package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import model.Assignment;
import model.AuthenticationModel;
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

	private boolean connected = true;
	private Server server = null;
	private Contact thisContact = null;
	private List<ModelInterface> list;
	private List<ModelInterface> hashList;
	char keystorepass[] = "password".toCharArray();
	char keypassword[] = "password".toCharArray();
	char truststorepass[] = "password".toCharArray();
	SSLServerSocket Socket = null;
	SSLSocket client;
	private final String replicateServerIP = "/192.168.1.1";


	/**
	 * Konstruktorn, tar emot en socket för porten vi lyssnar på och en Server
	 * som kan skicka vidare data
	 * 
	 * @param socket
	 *            Den socket som anslutningen sker genom
	 * @param server
	 *            Servern som hanterar alla anslutningar och som kan skicka
	 *            vidare data
	 */
	public MultiServerThread(Socket socket, Server server) {
		super("MultiServerThread");
		
		try{
			KeyStore ts = KeyStore.getInstance("JKS");
			ts.load(new FileInputStream(new File(getClass().getClassLoader().getResource("cert/servertruststore.jks").getPath())),truststorepass);

			TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ts);
		
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(new File(getClass().getClassLoader().getResource("cert/server.jks").getPath())),keystorepass);
			KeyManagerFactory kmf =
					KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, keypassword);
			SSLContext sslcontext =
					SSLContext.getInstance("TLS");
			sslcontext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			ServerSocketFactory ssf = sslcontext.getServerSocketFactory();

			Socket = (SSLServerSocket)
					ssf.createServerSocket(Server.port);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not listen on port "+socket);
		} catch (KeyStoreException e) {
			System.out.println("Could not get key store");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("There is no algorithm in ks.load");
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			System.out.println("kmf.init() no key");
		} catch (KeyManagementException e) {
			System.out.println("sslcontext.init keymanagementexception");
		}
		try {
			//klart att skicka över om deta klara sig
			client = (SSLSocket) Socket.accept();
		}catch (IOException e) {
			System.out.println("Accept failed on "+Server.port);
		}
		db = new Database(); 
		if (socket.getInetAddress().toString().equals(replicateServerIP)) {
			db.setReplicationStatus(false);
		} else {
			db.setReplicationStatus(true);
		}
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
				while ((inputLine = input.readLine()) != null
						&& !inputLine.equals("close")) {
					System.out.println("<input from "
							+ socket.getInetAddress().toString() + ":"
							+ socket.getPort() + "> " + inputLine);
					handleTypeOfInput(inputLine);
				}
				// if (inputLine != null) {
				//
				// if (inputLine.equals("exit")) {
				// connected = false;
				// break;
				// }
				//
				// // Bestämmer vilken typ av input som kommer in. När det
				// // avgjorts
				// // sparas och/eller skickas input:en vidare.
				// handleTypeOfInput(inputLine);
				//
				// }


			// Tar bort kontakten från hashMapen med de anslutna klienterna
			server.removeClient(socket.getInetAddress().toString());
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
	 * @param input
	 *            Json strängen.
	 */
	private void handleTypeOfInput(String input) {
		if (input.contains("\"databaseRepresentation\":\"message\"")) {
			handleMessage(input);
		} else if (input.contains("\"databaseRepresentation\":\"assignment\"")) {
			handleAssignment(input);
		} else if (input.contains("\"databaseRepresentation\":\"contact\"")) {
			handleContact(input);
		} else if (input
				.contains("\"databaseRepresentation\":\"authentication\"")) {
			if (!handleLogin(input)) {
				connected = false;
			}
		} else if (input.equals("pull")) {
			server.sendUnsentItems(thisContact);
		} else if (input.equals("getAllContacts")) {
			handleContactRequest();
		} else {
			System.out.println("<" + socket.getInetAddress()
					+ "> Did not recognise inputtype.	" + inputLine);
		}
	}

	/**
	 * Hanterar json-strägen om den är ett meddelande och skickar denna till en
	 * specifik kontakt
	 * 
	 * @param message
	 *            Json-strängen av meddelandet
	 */
	private void handleMessage(String message) {

		MessageModel msg = new MessageModel();
		// Gson konverterar json-strängen till MessageModel-objektet igen
		try {
			msg = (new Gson()).fromJson(message, MessageModel.class);
			if (!socket.getInetAddress().toString().equals(replicateServerIP)) {
				server.send(message, msg.getReciever().toString());
			}
			// Lägger in meddelandet i databasen
			db.addToDB(msg);
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("<" + socket.getInetAddress() + ":"
					+ socket.getPort() + " " + sdf.format(cal.getTime())
					+ "> message from " + msg.getSender() + " to "
					+ msg.getReciever() + ": " + msg.getMessageContent());

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Hanterar json-strängen om den är ett uppdrag och skickar ut den till alla
	 * förutom den som skickade uppdraget
	 * 
	 * @param assignment
	 *            Json-strängen av uppdraget
	 */
	private void handleAssignment(String assignment) {
		// Gson konverterar json-strängen till Assignment-objektet igen.
		try {
			Assignment assignmentFromJson = (new Gson()).fromJson(assignment,
					Assignment.class);
			if (!socket.getInetAddress().toString().equals(replicateServerIP)) {
				server.sendToAllExceptTheSender(assignment, socket
						.getInetAddress().toString());
			}

			// Lägger in kontakten i databasen
			db.addToDB(assignmentFromJson);
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("<" + socket.getInetAddress() + ":"
					+ socket.getPort() + " " + sdf.format(cal.getTime())
					+ "> assignment from " + assignmentFromJson.getSender()
					+ ": " + assignmentFromJson.getName() + "	"
					+ assignmentFromJson.getAssignmentStatus());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Hanterar json-strängen om den är en kontakt och skickar ut denna till
	 * alla
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
			db.addToDB(contactFromJson);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private boolean handleLogin(String login) {
		try {
			System.out.println("Login request from: "
					+ socket.getInetAddress().toString());
			list = db.getAllFromDB(new Contact());
			AuthenticationModel loginFromJson = (new Gson().fromJson(login,
					AuthenticationModel.class));
			if (!socket.getInetAddress().toString().equals(replicateServerIP)) {
				hashList = db.getAllFromDB(new AuthenticationModel());
				for (ModelInterface m : list) {
					Contact cont = (Contact) m;
					if (loginFromJson.getUserName().equals(
							cont.getContactName())) {
						for (ModelInterface mi : hashList) {
							AuthenticationModel logMod = (AuthenticationModel) mi;
							if (cont.getId() == logMod.getContactId()
									&& loginFromJson.getPasswordHash().equals(
											logMod.getPasswordHash())) {
								cont.setInetAddress(socket.getInetAddress()
										.toString());
								db.updateModel(cont);
								loginFromJson.setIsAccessGranted(true);
								String response = new Gson()
										.toJson(loginFromJson);
								server.send(response, cont.getContactName());
								thisContact = cont;
								System.out.println("<"
										+ socket.getInetAddress().toString()
										+ "> " + cont.getContactName()
										+ " connected.");
								return true;
							}
						}
					}
				}

				try {
					PrintWriter pr = new PrintWriter(socket.getOutputStream(),
							true);
					pr.println(login);
					System.out.println("<" + socket.getInetAddress().toString()
							+ "> failed to login.");
				} catch (Exception e) {
					System.out.println(e);
				}
			} else {
				for (ModelInterface m : list) {
					Contact cont = (Contact) m;
					if (loginFromJson.getUserName().equals(
							cont.getContactName())) {
						db.addToDB(new AuthenticationModel(cont.getId(),
								loginFromJson.getPasswordHash()));
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private void handleContactRequest() {
		try {
			list = db.getAllFromDB(new Contact());
			for (ModelInterface m : list) {
				Contact cont = (Contact) m;
				String contact = new Gson().toJson(cont);
				server.send(contact, thisContact.getContactName());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
