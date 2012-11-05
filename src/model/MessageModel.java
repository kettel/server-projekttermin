package model;

import java.util.Calendar;

public class MessageModel implements ModelInterface {

	private String databaseRepresentation = "message";
	private long id = -1;
	
	private String messageContent;
	private String reciever;
	private long messageTimeStamp;

	public MessageModel() {

	}

	// Mesage borde döpas om till något annat liknande
	public MessageModel(CharSequence messageContent, CharSequence reciever) {
		this.messageContent = (String) messageContent;
		this.reciever = (String) reciever;
		this.messageTimeStamp = Calendar.getInstance().getTimeInMillis();
	}

	public MessageModel(long id, CharSequence messageContent, CharSequence reciever,
			long timeStamp) {
		this.id = id;
		this.messageContent = (String) messageContent;
		this.reciever = (String) reciever;
		this.messageTimeStamp = timeStamp;
	}

	public CharSequence getMessageContent() {
		return messageContent;
	}

	public CharSequence getReciever() {
		return (CharSequence) reciever;
	}

	public long getMessageTimeStamp() {
		return messageTimeStamp;
	}

	public String getDatabaseRepresentation() {
		// TODO Auto-generated method stub
		return databaseRepresentation;
	}

	@Override
	public long getId() {
		return id;
	}
}
