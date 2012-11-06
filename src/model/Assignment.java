package model;

public class Assignment implements ModelInterface {

	// Typen av modell
	private String databasetRepresentation = "assignment";
	// Id för modellen (Sätts av databasen så pilla inte)
	private long id = -1;
	// Namnet på uppdraget
	private String name;
	// Latitud för uppdragspositionen
	private long lat;
	// Longitud för uppdragspositionen
	private long lon;
	// Användarnamnet på mottagaren för ett uppdrag (Om man vill specificera
	// det)
	private String receiver;
	// Användarnamnet på den person som skapade uppdraget.
	private String sender;
	// Textbeskrivning av uppdraget
	private String assignmentDescription;
	// Tidsbeskrivning av hur lång tid uppdraget kommer ta (1 timme, 20
	// minuter...)
	private String timeSpan;
	// Textbeskrivning av uppdragets nuvarande status (Icke påbörjat, Påbörjat,
	// Behöver hjälp)
	private String assignmentStatus;
	// Bild kopplat till uppdraget
	private byte[] cameraImage;
	// Gatunamn för platsen där uppdraget utspelas
	private String streetName;
	// Platsnamn där uppdraget utspelas
	private String siteName;

	/**
	 * Tom konstruktor for Assignment
	 */
	public Assignment() {

	}

	/**
	 * 
	 * @param name
	 *            String Namn på uppdrag
	 * @param lat
	 *            long Latitud för uppdraget
	 * @param lon
	 *            long Longitud för uppdraget
	 * @param receiver
	 *            String Mottagare av uppgradet
	 * @param sender
	 *            String Sändare av uppdraget
	 * @param assignmentDescription
	 *            String Beskrivning av uppdraget
	 * @param timeSpan
	 *            String Hur lång tid uppdraget väntas ta
	 * @param assignmentStatus
	 *            String Status för uppdraget
	 * @param cameraImage
	 *            Bitmap En bifogad bild på uppdragsplatsen
	 * @param streetName
	 *            String Gatunamn
	 * @param siteName
	 *            String Platsnamn
	 */
	public Assignment(String name, long lat, long lon, String receiver,
			String sender, String assignmentDescription, String timeSpan,
			String assignmentStatus, byte[] cameraImage, String streetName,
			String siteName) {
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.receiver = receiver;
		this.sender = sender;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.cameraImage = cameraImage;
		this.streetName = streetName;
		this.siteName = siteName;
	}

	/**
	 * Konstruktor för att återskapa ett meddelande från databasen med ett Id
	 * som hämtas från databasen
	 * 
	 * @param id
	 * @param name
	 * @param lat
	 * @param lon
	 * @param receiver
	 * @param sender
	 * @param assignmentDescription
	 * @param timeSpan
	 * @param assignmentStatus
	 * @param cameraImage
	 * @param streetName
	 * @param siteName
	 */

	public Assignment(long id, String name, long lat, long lon,
			String receiver, String sender, String assignmentDescription,
			String timeSpan, String assignmentStatus, byte[] cameraImage,
			String streetName, String siteName) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.receiver = receiver;
		this.sender = sender;
		this.assignmentDescription = assignmentDescription;
		this.timeSpan = timeSpan;
		this.assignmentStatus = assignmentStatus;
		this.cameraImage = cameraImage;
		this.streetName = streetName;
		this.siteName = siteName;
	}

	public String getName() {
		return name;
	}

	public void setName(String nameToBeSet) {
		this.name = nameToBeSet;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetNameToBeSet) {
		this.streetName = streetNameToBeSet;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteNameToBeSet) {
		this.siteName = siteNameToBeSet;
	}

	public long getLat() {
		return lat;
	}

	public void setLat(long latToBeSet) {
		this.lat = latToBeSet;
	}

	public long getLon() {
		return lon;
	}

	public void setLon(long lonToBeSet) {
		this.lon = lonToBeSet;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiverToBeSet) {
		this.receiver = receiverToBeSet;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String senderToBeSet) {
		this.sender = senderToBeSet;
	}

	public byte[] getCameraImage() {
		return cameraImage;
	}

	public void captureCameraImage(byte[] cameraImageToBeSet) {
		this.cameraImage = cameraImageToBeSet;
	}

	public String getAssignmentDescription() {
		return assignmentDescription;
	}

	public void setAssignmentDescription(String assignmentDescriptionToBeSet) {
		this.assignmentDescription = assignmentDescriptionToBeSet;
	}

	public String getTimeSpan() {
		return timeSpan;
	}

	public void setTimeSpan(String timeSpanToBeSet) {
		this.timeSpan = timeSpanToBeSet;
	}

	public String getAssignmentStatus() {
		return assignmentStatus;
	}

	public void setAssignmentStatus(String assignmentStatusToBeSet) {
		this.assignmentStatus = assignmentStatusToBeSet;
	}

	public String getDatabaseRepresentation() {
		return databasetRepresentation;
	}

	public long getId() {
		return id;
	}
}