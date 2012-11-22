package database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.LoginModel;
import model.ModelInterface;

public class DatabaseHandlerLogin extends DatabaseHandler {

	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;

	public void addModel(ModelInterface m) {
		LoginModel login = (LoginModel) m;
		try {

			// Initiera en anslutning till databasen
			con = DriverManager.getConnection(url, user, password);
			
			// SQL-frågan
			pst = con
					.prepareStatement("INSERT INTO login(contact_Id, Password) VALUES(?,?)");

			pst.setString(1, Long.toString(login.getContactId()));
			pst.setString(2, hashPassword(login.getPassword()));

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
			LoginModel login = (LoginModel)m;
			
			con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            // Sätt autocommit till falskt
            con.setAutoCommit(false);
            
         // Sätt in rätt värden till rätt plats i frågan och uppdatera dessa
            st.executeUpdate("UPDATE login" + 
            		" SET contact_Id = \"" + Long.toString(login.getContactId()) +
            		"\", Password = \"" + hashPassword(login.getPassword()) + 
            		"\" WHERE Id = " + login.getId());
            
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
            	LoginModel tempLogin = new LoginModel(rs.getInt(1), // Id
						Long.valueOf(rs.getString(2)), // contact_Id
						rs.getString(3)); // Password
            	returnList.add((ModelInterface) tempLogin);
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
	
	/*
	 * Metoden skapar en hashrepresentation av de inmatade lösenordet med hjälp av SHA-2
	 */
	public String hashPassword(String password){
		StringBuffer hexString = new StringBuffer();
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.toString().getBytes());
	
			byte byteData[] = md.digest();
	
			//convert the byte to hex format method 2
			
			for (int i=0;i<byteData.length;i++) {
				String hex=Integer.toHexString(0xff & byteData[i]);
				if(hex.length()==1) hexString.append('0');
				hexString.append(hex);
			}
		}catch(Exception e){
			System.err.println("Kan inte hasha angivet lösenord.. Fel: " + e);
		}
		return hexString.toString();
	}
	
}
