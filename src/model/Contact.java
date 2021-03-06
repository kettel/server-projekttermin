package model;


public class Contact implements ModelInterface {

	// Typen av modell
	private String databaseRepresentation = "contact";
	// Id för modellen (Sätts av databasen så pilla inte)
	private long id = -1;
	// Användarnamnet på konakten
	private String contactName;
	// Kontaktens ip
	private String inetAddress;
	private String gcmId;

	/**
	 * Tom konstruktor for Contact
	 */
	public Contact() {

	}
	
	public Contact(String contactName){
		this.contactName = contactName;
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
	public Contact(long id, String contactName, String inetAddress, String gcmId) {
		this.id = id;
		this.contactName = contactName;
		this.inetAddress = inetAddress;
		this.gcmId = gcmId;
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
	
	public void setGcmId(String id){
		gcmId = id;
	}
	
	public String getGcmId(){
		return gcmId;
	}
	
}
