package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import model.Contact;
import model.ModelInterface;
import model.QueueItem;

public class DatabaseHandlerQueue extends DatabaseHandler{

	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;
	
	public void push(QueueItem q){
		try {
			// Initiera en anslutning till databasen
            con = DriverManager.getConnection(url, user, password);
            
            // SQL-frågan
            pst = con.prepareStatement("INSERT INTO queue(ContactID, JSON) VALUES(?,?)");
            
            // Sätt in rätt värden till rätt plats i frågan
            pst.setString(1, Long.toString(q.getContactId()));
            pst.setString(2, q.getJSON());
           
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
	
	public QueueItem pop(){
		QueueItem poppedItem = null;
		try {
            con = DriverManager.getConnection(url, user, password);
            // H�mta det sista objektet i tabellen queue.
            pst = con.prepareStatement("SELECT * FROM queue ORDER BY Id DESC LIMIT 1");
            rs = pst.executeQuery();
            
            long lastId = -1;
            
            
            // G� igenom det sista objektet
            while (rs.next()) {
            	lastId = Long.valueOf(rs.getInt(1));
            	poppedItem = new QueueItem(Long.valueOf(rs.getInt(1)), // Id
            			  Long.valueOf(rs.getString(2)), // ContactId
            						rs.getString(3)); // JSON-str�ng
            }
            pst = con.prepareStatement("DELETE FROM queue WHERE Id = " + Long.toString(lastId));
            rs = pst.executeQuery();

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
		return poppedItem;
	}
	
	
	@Override
	public void addModel(ModelInterface m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateModel(ModelInterface m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ModelInterface> getAllModels(ModelInterface m) {
		// TODO Auto-generated method stub
		return null;
	}

}
