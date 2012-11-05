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
			dha.addModel(m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			dhc.addModel(m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages();
			dhm.addModel(m);
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
			returnCount = dha.getTotal(m.getDatabaseRepresentation());
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			returnCount = dhc.getTotal(m.getDatabaseRepresentation());
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages();
			returnCount = dhm.getTotal(m.getDatabaseRepresentation());
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
			returnList = dha.getAllModels(m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			returnList = dhc.getAllModels(m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages();
			returnList = dhm.getAllModels(m);
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
			dha.removeModel(m.getDatabaseRepresentation(), Long.toString(m.getId()));
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			dhc.removeModel(m.getDatabaseRepresentation(), Long.toString(m.getId()));
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages();
			dhm.removeModel(m.getDatabaseRepresentation(), Long.toString(m.getId()));
		}
	}
	
	public void updateModel(ModelInterface m){
		String dbRep = m.getDatabaseRepresentation();
		if (dbRep.equalsIgnoreCase("assignment")) {
			DatabaseHandlerAssignment dha = new DatabaseHandlerAssignment();
			dha.updateModel(m);
		}
		else if(dbRep.equalsIgnoreCase("contact")){
			DatabaseHandlerContacts dhc = new DatabaseHandlerContacts();
			dhc.updateModel(m);
		}
		else if(dbRep.equalsIgnoreCase("message")){
			DatabaseHandlerMessages dhm = new DatabaseHandlerMessages();
			dhm.updateModel(m);
		}
	}

}
