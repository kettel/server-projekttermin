package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Contact implements ModelInterface {

	// Typen av modell
	private String databaseRepresentation = "contact";
	// Id för modellen (Sätts av databasen så pilla inte)
	private long id = -1;
	// Användarnamnet på konakten
	private String contactName;
	// Kontaktens ip
	private String inetAddress;
	private Queue<String> unsentQueue = new LinkedList<String>();

	/**
	 * Tom konstruktor for Contact
	 */
	public Contact() {

	}

	/**
	 * Konstruktor för att skapa en ny kontakt som inte finns i databasen.
	 * 
	 * @param contactName
	 * @param inetAdress
	 */
	public Contact(String contactName, String inetAdress) {
		this.contactName = contactName;
		this.inetAddress = inetAdress;
	}

	/**
	 * Konstruktor för att återskapa en kontakt från databasen då ett Id finns.
	 * 
	 * @param id
	 * @param contactName
	 * @param inetAddress
	 */
	public Contact(long id, String contactName, String inetAddress, Queue<String> unsentQueue) {
		this.id = id;
		this.contactName = contactName;
		this.inetAddress = inetAddress;
		this.unsentQueue = unsentQueue;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String nameToBeSet) {
		this.contactName = nameToBeSet;
	}

	public String getDatabaseRepresentation() {
		return databaseRepresentation;
	}

	public long getId() {
		return id;
	}

	public String getInetAddress() {
		return inetAddress;
	}

	public void setInetAddress(String inetAddress) {
		this.inetAddress = inetAddress;
	}
	
	public synchronized void addUnsentItem(String s){
		unsentQueue.add(s);
	}
	
	public synchronized void removeUnsentItem(String s){
		unsentQueue.remove(s);
	}
	
	public Queue<String> getUnsentQueue(){
		unsentQueue.add("hej");
		return unsentQueue;
	}
	
	public String getUnsentQueueString() {
		// Konkatenera alla agenter till en sträng
		String queue = new String();
		Queue<String> unsentQueue = this.unsentQueue;
		for (String unsent : unsentQueue) {
			queue.concat(/*contact.getContactName() + ":"
					+ */unsent/*.getInetAddress()*/ + "/");
		}
		return queue;
	}
}
