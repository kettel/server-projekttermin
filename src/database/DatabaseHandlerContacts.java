package database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Contact;
import model.ModelInterface;

public class DatabaseHandlerContacts extends DatabaseHandler{

	@Override
	public void addModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till MessageModel
			Contact contact = (Contact)m;
			
			// Initiera en anslutning till databasen
            con = DriverManager.getConnection(url, user, password);
            
            // SQL-frågan
            pst = con.prepareStatement("INSERT INTO contact(Name, PhoneNumber, Email, ClearenceLevel, Classification, Comment) VALUES(?,?,?,?)");
            
            // Sätt in rätt värden till rätt plats i frågan
            pst.setString(1, contact.getContactName());
            pst.setString(2, Long.toString(contact.getContactPhoneNumber()));
            pst.setString(3, contact.getContactEmail());
            pst.setString(4, contact.getContactClearanceLevel());
            pst.setString(5, contact.getContactClassification());
            pst.setString(6, contact.getContactComment());
           
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
            pst = con.prepareStatement("SELECT * FROM Messages");
            rs = pst.executeQuery();

            while (rs.next()) {
            	// Hämta och skapa ett nytt Contact-objekt samt lägg
            	// till det i returnList
            	returnList.add((ModelInterface) new Contact(rs.getInt(0),
            						rs.getString(1),
            						Long.valueOf(rs.getString(2)),
            						rs.getString(2),
            						rs.getString(3),
            						rs.getString(4),
            						rs.getString(5)));
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
            		" SET Name = " + contact.getContactName() +
            		", PhoneNumber = " + contact.getContactPhoneNumber() + 
            		", Email = " + contact.getContactEmail() + 
            		", ClearanceLevel = " + contact.getContactClearanceLevel() + 
            		", Classification = " + contact.getContactClassification() + 
            		", Comment = " + contact.getContactComment() +
            		" WHERE Id = " + contact.getId());
            
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
}
