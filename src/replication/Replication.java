package replication;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.Gson;

import database.Database;

import model.AuthenticationModel;
import model.ModelInterface;
import model.Assignment;
import model.Contact;
import model.MessageModel;
import model.QueueItem;

/**
 * En klass som sköter sickandet och motagndet av data mellan klienten och
 * serven.
 * 
 */

public class Replication extends Thread {

	private String target_ip = "94.254.72.38";
	private int target_port = 0;
	private Socket requestSocet = null;
	private Queue<String> transmissonQueue = new LinkedList<String>();
	private PrintWriter output = null;
	private boolean sendData = false;
	private boolean connected = false;
	private int waitTime = 1;
	private BufferedReader input;
	private String inputString;
	private Database database = new Database();
	private Gson gson = new Gson();

	/**
	 * en tom konstruktor
	 */
	public Replication(int target_port) {
		this.target_port = target_port;
	}

	/**
	 * Används för att förhindra att data sickas
	 * 
	 * @param enabel
	 */
	private synchronized void sendData(boolean enabel) {
		sendData = enabel;
	}

	/**
	 * används av CommunicationService för att sicka Gson strängar.
	 * 
	 * @param transmisson
	 *            en Gson sträng
	 */
	public synchronized void sendReplicationData(ModelInterface data) {
		this.transmissonQueue.add(gson.toJson(data));
		sendData = true;
	}

	/**
	 * Om en kontakt med serven kan etablras sätts denna till true.
	 * 
	 * @param enabel
	 */
	private synchronized void setConnetion(boolean enabel) {
		connected = enabel;
	}

	/**
	 * jag tror ni fattar
	 * 
	 * @return connected.
	 */
	public synchronized boolean isConnection() {
		return connected;
	}

	/**
	 * väntar till reconnect, samt ökar väntetiden kontiueligt upp till en
	 * minut.
	 */
	private synchronized void timeToWait() {
		if (waitTime < 60000) {
			waitTime = waitTime + 50;
		}
		try {
			this.wait(waitTime);
		} catch (Exception e) {
		}

	}

	/**
	 * nollställer väntetiden till en reconnect
	 */
	private void resetTimeToWait() {
		waitTime = 1;
	}

	public void run() {
		while (true) {
			try {
				requestSocet = new Socket(target_ip, target_port);
				output = new PrintWriter(requestSocet.getOutputStream(), true);
				setConnetion(true);
			} catch (Exception e) {
				setConnetion(false);
				timeToWait();
			}
			while (isConnection()) {
				resetTimeToWait();
				try {
					if (input.ready()) {
						inputString = input.readLine();
						if (inputString
								.contains("\"databaseRepresentation\":\"message\"")) {
							MessageModel message = gson.fromJson(inputString,
									MessageModel.class);
							database.addToDB(message);
						} else if (inputString
								.contains("\"databaseRepresentation\":\"assignment\"")) {
							Assignment assignment = gson.fromJson(inputString,
									Assignment.class);
							System.out.println("geson here: "
									+ assignment.getName());
							assignment.getCameraImage();
							database.addToDB(assignment);
						} else if (inputString
								.contains("\"databaseRepresentation\":\"contact\"")) {
							Contact contact = gson.fromJson(inputString,
									Contact.class);
							database.addToDB(contact);
						} else if (inputString
								.contains("\"databaseRepresentation\":\"authentication\"")) {
							AuthenticationModel am = gson.fromJson(inputString,
									AuthenticationModel.class);
							database.addToDB(am);
						} else if (inputString
								.contains("\"databaseRepresentation\":\"queueItem\"")) {
							QueueItem qi = gson.fromJson(inputString,
									QueueItem.class);
							database.addToDB(qi);
						} else {
						}
					}
				} catch (Exception e) {
				}
				if (sendData && isConnection()) {
					if (!this.transmissonQueue.isEmpty()) {
						for (int i = 0; i < this.transmissonQueue.size(); i++) {
							output.println(this.transmissonQueue.poll());
						}
					}
					;
					if (output.checkError()) {
						setConnetion(false);
					}
					sendData(false);
				}
			}
		}
	}
}