package model;

public class Assignment implements ModelInterface {

	private String databasetRepresentation = "assignment";
	private String name;
	private long lat;
	private long lon;
	private String receiver;
	private String sender;
	private String assignmentDescription;
	private String timeSpan;
	private String assignmentStatus;
	private byte[] cameraImage;
	private String streetName;
	private String siteName;

	public Assignment() {

	}

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
}