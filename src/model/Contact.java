package model;

import java.util.LinkedList;
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
//		System.out.println("@Contact(75): " + s);
		unsentQueue.add(s);
//		System.out.println("@Contact(77): " + unsentQueue);
	}
	
	public synchronized void removeUnsentItem(String s){
		unsentQueue.remove(s);
	}
	
	public Queue<String> getUnsentQueue(){
		return unsentQueue;
	}
	
	public String getUnsentQueueString() {
		// Konkatenera alla agenter till en sträng
		String queueString = new String();
		Queue<String> unsentQueue = this.unsentQueue;
//		System.out.println("@Contact(92): " + unsentQueue);
		for (String unsent : unsentQueue) {
			System.out.println("@Contact(94): " + unsent);
			queueString.concat(/*contact.getContactName() + ":"
					+ */"hej"/*.getInetAddress()*/ + "/");
			System.out.println("@Contact(97): " + queueString);
		}
//		System.out.println("@Contact(98): " + queueString);
		return queueString;
	}
}
