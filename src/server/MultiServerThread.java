package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
import model.Contact;
import model.LoginModel;
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
	private Contact thisContact;
	private List<ModelInterface> list;
	private List<ModelInterface> hashList;
	char keystorepass[] = "password".toCharArray();
	char keypassword[] = "password".toCharArray();
	char truststorepass[] = "password".toCharArray();
	SSLServerSocket serverSocket = null;
	SSLSocket client;
	private static final int port = 17234;

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
		try {
			//L�ser truststoren och l�ser up den. kollar att clienten har r�tt cert
			KeyStore ts = KeyStore.getInstance("JKS");
			ts.load(MultiServerThread.class.getResourceAsStream("cert/servertruststore.jks"),truststorepass);
			//laddar truststoren s� vi kan anv�nda den
			TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);
			//Keystoren g�r samma sak som truststoren bara det �r det serven ger ut till clienten
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(MultiServerThread.class.getResourceAsStream("cert/server.jks"),keystorepass);
			KeyManagerFactory kmf =
				KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, keypassword);
			//st�ller in tls med keystoren och truststoren
			SSLContext sslcontext =
					SSLContext.getInstance("TLS");
			sslcontext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			//g�r en server socket med tls
			ServerSocketFactory ssf = sslcontext.getServerSocketFactory();

			serverSocket = (SSLServerSocket)
			ssf.createServerSocket(port);
			
			 // Alla dessa catch kan kastas om man vill men h�ller dom s� l�nge
	
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
			//klart att skicka �ver om deta klara sig
			client = (SSLSocket) serverSocket.accept();
		}catch (IOException e) {
			//b�r kunna kasta detta sen om det �r iv�gen
		System.out.println("Accept failed on "+port);
		}
		db = new Database();
		List<ModelInterface> m = db.getAllFromDB(new Contact());
		for (ModelInterface mi : m) {
			Contact cont = (Contact) mi;
			if (socket.getInetAddress().toString()
					.equals("/" + cont.getInetAddress())) {
				thisContact = cont;
			}
		}
	}

	/**
	 * Koden för den tråd som skapas för en ny anslutning.
	 */
	public void run() {

		try {
			server.sendUnsentItems(thisContact);

			while (connected) {
				// Buffrar ihop flera tecken från InputStreamen till en sträng
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				// Läser den buffrade strängen
				inputLine = input.readLine();
				if (inputLine != null) {

					if (inputLine.equals("exit")) {
						connected = false;
						break;
					}

					// Bestämmer vilken typ av input som kommer in. När det
					// avgjorts
					// sparas och/eller skickas input:en vidare.
					handleTypeOfInput(inputLine);
				}
			}
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
		} else if (input.contains("\"databaseRepresentation\":\"authentication\"")) {
			if(!handleLogin(input)){
				connected = false;
			}
		} else if (input.equals("Heart")) {
		} else {
			System.out.println("<" + socket.getInetAddress() + "> Did not recognise inputtype.	" + inputLine);
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
			server.send(message, msg.getReciever().toString());
			// Lägger in meddelandet i databasen
			db.addToDB(msg);
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("<" + socket.getInetAddress() + ":"
					+ socket.getPort() + "	" + sdf.format(cal.getTime())
					+ "> message from  " + msg.getSender() + " to "
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
			server.sendToAllExceptTheSender(assignment, socket.getInetAddress().toString());

			// Lägger in kontakten i databasen
			db.addToDB(assignmentFromJson);
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("<" + socket.getInetAddress() + ":"
					+ socket.getPort() + "	" + sdf.format(cal.getTime())
					+ "> assignment from  " + assignmentFromJson.getSender()
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
			server.sendToAllExceptTheSender(contact, socket.getInetAddress()
					.toString());
			// Lägger in uppdraget i databasen
			db.updateModel(contactFromJson);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private boolean handleLogin(String login) {
		try{
			LoginModel loginFromJson = (new Gson().fromJson(login, LoginModel.class));
			list = db.getAllFromDB(new Contact());
			hashList = db.getAllFromDB(new LoginModel());
			for (ModelInterface m : list) {
				Contact cont = (Contact) m;
				if(loginFromJson.getUserName().equals(cont.getContactName())){
					for (ModelInterface mi : hashList) {
						LoginModel logMod = (LoginModel) mi;
						if(loginFromJson.getPassword().equals(logMod.getPassword())){
							cont.setInetAddress(socket.getInetAddress().toString());
							loginFromJson.setIsAccessGranted(true);
							String response = new Gson().toJson(loginFromJson);
							server.send(response, cont.getContactName());
							return true;
						}
					}
				}
			}
			
		}catch(Exception e){
			System.out.println(e);
		}
		return false;
	}
}
