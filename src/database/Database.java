package database;

import java.util.ArrayList;
import java.util.List;

import model.ModelInterface;
import model.Assignment;
import model.Contact;
import model.MessageModel;

public class Database {
	
	/**
	 * Lägg till ett uppdrag/kontakt/meddelande till rätt databas
	 * @param m			ModellInterface av objekt som ska läggas till
	 */
	public void addToDB(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			dha.addAssignment((Assignment)m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			dhc.addContact((Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			System.out.println("Försöker lägga till ett meddelande...");
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages();
			dhm.addMessage((MessageModel)m);
		}
	}
	
	/**
	 * Räkna antal poster i vald databas
	 * @param m			datatypen för den databas som ska räknas samman
	 * @return
	 */
	public int getDBCount(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		int returnCount = 0;
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			returnCount = dha.getAssignmentCount();
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			returnCount = dhc.getContactCount();
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages();
			returnCount = dhm.getMessageCount();
		}
		return returnCount;
	}
	
	/**
	 * Hämta alla objekt från databasen i en ArrayList
	 * @param m	ModelInterface	Den önskade returtypen
	 * @return	
	 */
	public List<ModelInterface> getAllFromDB(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			returnList = dha.getAllAssignments();
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			returnList = dhc.getAllContacts();
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages();
			returnList = dhm.getAllMessages();
		}
		return returnList;
	}
	
	/**
	 * Ta bort ett objekt från databasen
	 * @param m
	 * @param context
	 */
	public void deleteFromDB(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			dha.removeAssignment((Assignment)m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			dhc.removeContact((Contact)m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages();
			dhm.removeMessage((MessageModel)m);
		}
	}

}
