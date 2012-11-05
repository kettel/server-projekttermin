package database;

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
	
	public void addMessage(MessageModel m) {
		try {
			// Initiera en anslutning till databasen
            con = DriverManager.getConnection(url, user, password);
            
            // SQL-frågan
            pst = con.prepareStatement("INSERT INTO Messages(Content, Receiver, MessageTimestamp) VALUES(?,?,?)");
            
            // Sätt in rätt värden till rätt 
            pst.setString(1,m.getMessageContent().toString());
            pst.setString(2, m.getReciever().toString());
            pst.setTimestamp(3, Timestamp.valueOf(Long.toString(m.getMessageTimeStamp())));
            pst.executeUpdate();

            if (rs.next()) {
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
	}

	public int getMessageCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<ModelInterface> getAllMessages() {
		try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
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

}
