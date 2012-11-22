package model;

public class QueueItem implements ModelInterface{
	private long id = -1;
	private String databaseRepresentation = "queueItem";
	
	private long contactId = -1;
	
	private String json = new String();

	public QueueItem(){}
	
	public QueueItem(long contactId){
		this.contactId = contactId;
	}
	
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

	@Override
	public String getDatabaseRepresentation() {
		return databaseRepresentation;
	}

	@Override
	public long getId() {
		return id;
	}
	
}
