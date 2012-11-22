package model;

public class LoginModel implements ModelInterface {
	private long id = -1;
	private String databaseRepresentation = "login";
	private long contactId = -1;
	private String userName;
	private String password = new String();
	private boolean isAccessGranted;

	public LoginModel() {}
	
	public LoginModel(long contactId, String password) {
		this.contactId = contactId;
		this.password = password;
	}

	public LoginModel(long id, long contactId, String password) {
		this.id = id;
		this.contactId = contactId;
		this.password = password;
	}

	@Override
	public String getDatabaseRepresentation() {
		return databaseRepresentation;
	}

	@Override
	public long getId() {
		return id;
	}

	public long getContactId() {
		return contactId;
	}

	public String getPassword() {
		return password;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public boolean getIsAccessGranted(){
		return isAccessGranted;
	}
	
	public void setIsAccessGranted(boolean b){
		isAccessGranted = b;
	}

}
