package database;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import model.MessageModel;
import model.ModelInterface;

public class DatabaseHandlerMessages {
	Connection con = null;
    Statement st = null;
    ResultSet rs = null;

    String url = "jdbc:mysql://localhost:3306/assignments";
    String user = "serverUser";
    String password = "handdukMandel";
	
	public void addMessage(MessageModel m) {
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
	}

	public int getMessageCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<ModelInterface> getAllMessages() {
		// TODO Auto-generated method stub
		return null;
	}

}
