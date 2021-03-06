package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
            pst = con.prepareStatement("INSERT INTO contact(Name, InetAddress, GCM_ID) VALUES (AES_ENCRYPT(?,?)," + "AES_ENCRYPT(?,?), AES_ENCRYPT(?,?))");
            
            // Sätt in rätt värden till rätt plats i frågan
            pst.setString(1, contact.getContactName());
            pst.setString(2, AES_PASSWORD);
            pst.setString(3, contact.getInetAddress());
            pst.setString(4, AES_PASSWORD);
            pst.setString(5, contact.getGcmId());
            pst.setString(6, AES_PASSWORD);
            
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
            pst = con.prepareStatement("SELECT Id, AES_DECRYPT(Name,?),"
            + "AES_DECRYPT(InetAddress,?), AES_DECRYPT(GCM_ID,?) FROM " + m.getDatabaseRepresentation());
            for(int i = 1; i < 4; i ++){
            	pst.setString(i, AES_PASSWORD);
            }
            rs = pst.executeQuery();

            while (rs.next()) {
            	// Hämta och skapa ett nytt Contact-objekt samt lägg
            	// till det i returnList
            	returnList.add((ModelInterface) new Contact(rs.getInt(1), // Id
            						rs.getString(2), // Name
            						rs.getString(3), // Inetaddress
            						rs.getString(4))); //GCM_ID 
            						 
            						
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
            String update = "UPDATE " + contact.getDatabaseRepresentation() + 
            		" SET Name = AES_ENCRYPT(\"" + contact.getContactName() + "\",\""+AES_PASSWORD+"\"), " +
            		" InetAddress = AES_ENCRYPT(\"" + contact.getInetAddress() + "\",\""+AES_PASSWORD+"\"), " +
            		" GCM_ID = AES_ENCRYPT(\"" + contact.getGcmId() + "\",\""+AES_PASSWORD+"\")" +
            		" WHERE Id = " + contact.getId();
            // Sätt in rätt värden till rätt plats i frågan och uppdatera dessa
            st.executeUpdate(update);
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
