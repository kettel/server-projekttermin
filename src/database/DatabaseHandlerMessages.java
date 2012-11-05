package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import model.MessageModel;
import model.ModelInterface;

public class DatabaseHandlerMessages extends DatabaseHandler{
	
	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;
	
	@Override
	public void addModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till MessageModel
			MessageModel message = (MessageModel)m;
			
			// Initiera en anslutning till databasen
            con = DriverManager.getConnection(url, user, password);
            
            // SQL-frågan
            pst = con.prepareStatement("INSERT INTO message(Content, Receiver, MessageTimestamp) VALUES(?,?,?)");
            
            // Sätt in rätt värden till rätt plats i frågan
            pst.setString(1, message.getMessageContent().toString());
            pst.setString(2, message.getReciever().toString());
            pst.setString(3, Long.toString(message.getMessageTimeStamp()));
           
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
            pst = con.prepareStatement("SELECT * FROM message");
            rs = pst.executeQuery();
            
            while (rs.next()) {
            	MessageModel tempMess = new MessageModel(rs.getInt(1),
						rs.getString(2),
						rs.getString(3),
						Long.valueOf(rs.getString(4)));
            	returnList.add((ModelInterface) tempMess);
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
			MessageModel message = (MessageModel)m;
			
			con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            // Sätt autocommit till falskt
            con.setAutoCommit(false);
            
            // Sätt in rätt värden till rätt plats i frågan och uppdatera dessa
            st.executeUpdate("UPDATE " + message.getDatabaseRepresentation() + 
            		" SET Content = " + message.getMessageContent() +
            		", Receiver = " + message.getReciever() + 
            		// Tiden ska nog inte ändras...
            		//", MessageTimestamp = " + Long.toString(message.getMessageTimeStamp()) + 
            		" WHERE Id = " + message.getId());
            				
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
