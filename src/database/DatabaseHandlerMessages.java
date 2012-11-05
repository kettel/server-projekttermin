package database;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;


import model.MessageModel;
import model.ModelInterface;

public class DatabaseHandlerMessages {
	Connection con = null;
    Statement st = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    String url = "jdbc:mysql://localhost:3306/assignments";
    String user = "serverUser";
    String password = "handdukMandel";
	
    /**
     * Lägg till ett meddelande till databasen.
     * @param m		MessageModel 	Meddelandet som ska läggas in i databasen.
     */
	public void addMessage(MessageModel m) {
		try {
			// Initiera en anslutning till databasen
            con = DriverManager.getConnection(url, user, password);
            
            // SQL-frågan
            pst = con.prepareStatement("INSERT INTO Messages(Content, Receiver, MessageTimestamp) VALUES(?,?,?)");
            
            // Sätt in rätt värden till rätt plats i frågan
            pst.setString(1,m.getMessageContent().toString());
            pst.setString(2, m.getReciever().toString());
            pst.setTimestamp(3, Timestamp.valueOf(Long.toString(m.getMessageTimeStamp())));
           
            // Utför frågan och lägg till objektet i databasen
            pst.executeUpdate();

        } catch (SQLException ex) {

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
	
	/**
	 * Returnerar antalet meddelanden
	 * @return int	Antal meddelanden i databasen.
	 */
	public int getMessageCount() {
		int nofRows = 0;
		try {
			// Initiera en anslutning till databasen
            con = DriverManager.getConnection(url, user, password);
            
            // Fråga efter allt från Messages
            pst = con.prepareStatement("SELECT * FROM Messages");
            
            // Utför frågan
            rs = pst.executeQuery();
            
            // Räkna antal rader
            while(rs.next()){
            	nofRows++;
            }
        } catch (SQLException ex) {

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
		return nofRows;
	}

	/**
	 * Returnerar alla meddelanden som en arraylist
	 * @return
	 */
	public List<ModelInterface> getAllMessages() {
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		try {
            con = DriverManager.getConnection(url, user, password);
            pst = con.prepareStatement("SELECT * FROM Messages");
            rs = pst.executeQuery();

            while (rs.next()) {
            	returnList.add((ModelInterface) new MessageModel(rs.getInt(0),
            						rs.getString(1),
            						rs.getString(2),
            						Long.valueOf(rs.getTimestamp(3).toString())));
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {

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
		return null;
	}

	public void removeMessage(MessageModel m) {
		// TODO Auto-generated method stub
		
	}

}
