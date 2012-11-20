package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.Contact;
import model.ModelInterface;

public class DatabaseHandlerContacts extends DatabaseHandler{
	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;
	
	@Override
	public void addModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till Contact
			Contact contact = (Contact)m;
			
			// Initiera en anslutning till databasen
            con = DriverManager.getConnection(url, user, password);
            
            // SQL-frågan
            pst = con.prepareStatement("INSERT INTO contact(Name, InetAddress, UnsentQueue) VALUES(?,?,?)");
            
            // Sätt in rätt värden till rätt plats i frågan
            pst.setString(1, contact.getContactName());
            pst.setString(2, contact.getInetAddress());
            pst.setString(3, contact.getUnsentQueueString());
           
            // Utför frågan och lägg till objektet i databasen
            pst.executeUpdate();

        } catch (SQLException ex) {
        	System.out.println("Fel: " + ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
	}

	@Override
	public List<ModelInterface> getAllModels(ModelInterface m) {
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		try {
            con = DriverManager.getConnection(url, user, password);
            pst = con.prepareStatement("SELECT * FROM "+m.getDatabaseRepresentation());
            rs = pst.executeQuery();

            while (rs.next()) {
            	// Hämta och skapa ett nytt Contact-objekt samt lägg
            	// till det i returnList
            	returnList.add((ModelInterface) new Contact(rs.getInt(1), // Id
            						rs.getString(2), // Name
            						rs.getString(3), // Inetaddress
            						getUnsentQueueFromString(rs.getString(4)))); 
            						
            }

        } catch (SQLException ex) {
        	System.out.println("Fel: " + ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
		return returnList;
	}

	@Override
	public void updateModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till MessageModel
			Contact contact= (Contact)m;
			
			con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            // Sätt autocommit till falskt
            con.setAutoCommit(false);
            
            // Sätt in rätt värden till rätt plats i frågan och uppdatera dessa
            st.executeUpdate("UPDATE " + contact.getDatabaseRepresentation() + 
            		" SET Name = \"" + contact.getContactName() +
            		"\", InetAddress = \"" + contact.getInetAddress() +
            		"\", UnsentQueue = \"" + contact.getUnsentQueueString() +
            		"\" WHERE Id = " + contact.getId());
            
            // Commita db-uppdateringarna (?)
            con.commit();
            
        } catch (SQLException ex) {
        	System.out.println("Fel: " + ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
		
	}
	
	private Queue<String> getUnsentQueueFromString(String unsentQueueString){
		// Gör om strängar med agenter på uppdrag till en lista
		Queue<String> unsentQueue = new LinkedList<String>();
		String[] unsentArray = unsentQueueString.split("/");
		for (String unsent : unsentArray) {
			// Dela upp kontakten så man kommer åt namn och IP
			String[] array = unsent.split(":");
			unsentQueue.add(new Contact(array[0],array[1]).getContactName());
		}
		return unsentQueue;
	}
}
