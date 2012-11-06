package model;

public class Contact implements ModelInterface {

	// Typen av modell
	private String databasetRepresentation = "contact";
	// Id för modellen (Sätts av databasen så pilla inte)
	private long id = -1;
	// Användarnamnet på konakten
	private String contactName;
	// Telefonnumret till kontakten
	private long contactPhoneNumber;
	// E-mailadressen till kontakten
	private String contactEmail;
	// Kontaktens behörighetsnivå för att komma åt filer
	private String contactClearanceLevel;
	// Kontaktens titel (t.ex. chef,städare,kung,terrorist)
	private String contactClassification;
	// Kommentar om kontakten
	private String contactComment;
	// Kontaktens ip
	private String inetAddress;

	/**
	 * Tom konstruktor for Contact
	 */
	public Contact() {

	}

	/**
	 * Konstruktor för att skapa en ny kontakt som inte finns i databasen.
	 * 
	 * @param contactName
	 * @param contactPhoneNumber
	 * @param contactEmail
	 * @param contactClearanceLevel
	 * @param contactClassification
	 * @param contactComment
	 */
	public Contact(String contactName, Long contactPhoneNumber,
			String contactEmail, String contactClearanceLevel,
			String contactClassification, String contactComment,
			String inetAdress) {
		this.contactName = contactName;
		this.contactPhoneNumber = contactPhoneNumber;
		this.contactEmail = contactEmail;
		this.contactClearanceLevel = contactClearanceLevel;
		this.contactClassification = contactClassification;
		this.contactComment = contactComment;
		this.inetAddress = inetAdress;
	}

	/**
	 * Konstruktor för att återskapa en kontakt från databasen då ett Id finns.
	 * 
	 * @param id
	 * @param contactName
	 * @param contactPhoneNumber
	 * @param contactEmail
	 * @param contactClearanceLevel
	 * @param contactClassification
	 * @param contactComment
	 */
	public Contact(long id, String contactName, Long contactPhoneNumber,
			String contactEmail, String contactClearanceLevel,
			String contactClassification, String contactComment,
			String inetAddress) {
		this.id = id;
		this.contactName = contactName;
		this.contactPhoneNumber = contactPhoneNumber;
		this.contactEmail = contactEmail;
		this.contactClearanceLevel = contactClearanceLevel;
		this.contactClassification = contactClassification;
		this.contactComment = contactComment;
		this.inetAddress = inetAddress;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String nameToBeSet) {
		this.contactName = nameToBeSet;
	}

	public Long getContactPhoneNumber() {
		return contactPhoneNumber;
	}

	public void setContactPhoneNumber(Long contactPhoneNumberToBeSet) {
		this.contactPhoneNumber = contactPhoneNumberToBeSet;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmailToBeSet) {
		this.contactEmail = contactEmailToBeSet;
	}

	public String getContactClearanceLevel() {
		return contactClearanceLevel;
	}

	public void setContactClearanceLevel(String clearanceLevelToBeSet) {
		this.contactClearanceLevel = clearanceLevelToBeSet;
	}

	public String getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(String contactClassificationToBeSet) {
		this.contactClassification = contactClassificationToBeSet;
	}

	public String getContactComment() {
		return contactComment;
	}

	public void setContactComment(String contactCommentToBeSet) {
		this.contactComment = contactCommentToBeSet;
	}

	public String getDatabaseRepresentation() {
		return databasetRepresentation;
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

}
