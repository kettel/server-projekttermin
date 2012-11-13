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
	// En lista med koordinater
	private String wgs;
	// Användarnamnet på den person som skapade uppdraget.
	private String sender;
	// Säger om uppdraget ska skickas vidare till annan aktör
	private boolean externalMission;
	// Textbeskrivning av uppdraget
	private String assignmentDescription;
	// Tidsbeskrivning av hur lång tid uppdraget kommer ta (1 timme, 20
	// minuter...)
	private String timeSpan;
	// När uppdraget skapades
	private Long assignmentTimeStamp;
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
			String streetName, String siteName, boolean externalMission, Long assignmentTimeStamp, String wsg) {
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
		this.externalMission = externalMission;
		this.assignmentTimeStamp = assignmentTimeStamp;
		this.wgs = wsg;
	}

	public String getName() {
		return name;
	}

	public String getStreetName() {
		return streetName;
	}

	public String getSiteName() {
		return siteName;
	}

	public long getLat() {
		return lat;
	}

	public long getLon() {
		return lon;
	}

	public String getReceiver() {
		return receiver;
	}

	public String getSender() {
		return sender;
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

	public String getTimeSpan() {
		return timeSpan;
	}

	public String getAssignmentStatus() {
		return assignmentStatus;
	}

	public String getDatabaseRepresentation() {
		return databasetRepresentation;
	}

	public long getId() {
		return id;
	}
	
	public boolean getExternalMission(){
		return externalMission;
	}
	
	public String getWSG(){
		return wgs;
	}
	
	public Long getAssignmentTimeStamp(){
		return assignmentTimeStamp;
	}
}