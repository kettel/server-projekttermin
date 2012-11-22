package model;

public class QueueItem {
	private long id = -1;
	
	private long contactId = -1;
	
	private String json = new String();

	public QueueItem(){}
	
	public QueueItem(long contactId, String jSON) {
		this.contactId = contactId;
		this.json = jSON;
	}

	public QueueItem(long id, long contactId, String jSON) {
		this.id = id;
		this.contactId = contactId;
		this.json = jSON;
	}

	public long getContactId() {
		return contactId;
	}

	public String getJSON() {
		return json;
	}
	
}
