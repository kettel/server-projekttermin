package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.AuthenticationModel;
import model.ModelInterface;

public class DatabaseHandlerLogin extends DatabaseHandler {

	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;

	public void addModel(ModelInterface m) {
		try {
			AuthenticationModel auth = (AuthenticationModel) m;

			// Initiera en anslutning till databasen
			con = DriverManager.getConnection(url, user, password);

			// SQL-frågan
			pst = con
					.prepareStatement("INSERT INTO message(Username, PasswordHash) VALUES(?,?)");

			pst.setString(1, auth.getUserName().toString());
			pst.setString(2, auth.getPasswordHash().toString());

			pst.executeUpdate();

		} catch (SQLException e) {

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
	public void updateModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till MessageModel
			AuthenticationModel auth = (AuthenticationModel)m;
			
			con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            // Sätt autocommit till falskt
            con.setAutoCommit(false);
            
         // Sätt in rätt värden till rätt plats i frågan och uppdatera dessa
            st.executeUpdate("UPDATE " + auth.getDatabaseRepresentation() + 
            		" SET Username = \"" + auth.getUserName() +
            		"\", Password = \"" + auth.getPasswordHash() + 
            		"\", IsAccessGranted = \"" + Boolean.toString(auth.isAccessGranted()) +
            		"\" WHERE Id = " + auth.getId());
            
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

	@Override
	public List<ModelInterface> getAllModels(ModelInterface m) {
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		try {
            con = DriverManager.getConnection(url, user, password);
            pst = con.prepareStatement("SELECT * FROM message");
            rs = pst.executeQuery();
            
            while (rs.next()) {
            	AuthenticationModel tempAuth = new AuthenticationModel(rs.getInt(1), // Id
						rs.getString(2), // Username
						rs.getString(3), // Password
						Boolean.parseBoolean(rs.getString(6))); // isAccessGranted
            	returnList.add((ModelInterface) tempAuth);
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

}
