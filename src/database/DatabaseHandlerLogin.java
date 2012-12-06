package database;

import java.security.MessageDigest;
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
		AuthenticationModel login = (AuthenticationModel) m;
		try {

			// Initiera en anslutning till databasen
			con = DriverManager.getConnection(url, user, password);
			
			// SQL-frågan
			pst = con
					.prepareStatement("INSERT INTO login(contact_Id, Password) VALUES(?,AES_ENCRYPT(?,?))");

			pst.setString(1, Long.toString(login.getContactId()));
			pst.setString(2, hashPassword(login.getPasswordHash()));
			pst.setString(3, AES_PASSWORD);

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
			AuthenticationModel login = (AuthenticationModel)m;
			
			con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            // Sätt autocommit till falskt
            con.setAutoCommit(false);
            
         // Sätt in rätt värden till rätt plats i frågan och uppdatera dessa
            st.executeUpdate("UPDATE login" + 
            		" SET contact_Id = \"" + Long.toString(login.getContactId()) +
            		"\", Password = AES_ENCRYPT(\"" + hashPassword(login.getPasswordHash()) + "\",\""+AES_PASSWORD+"\")" +
            		"WHERE Id = " + login.getId());
            
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
            pst = con.prepareStatement("SELECT login.Id, login.contact_Id, AES_DECRYPT(contact.Name,?), AES_DECRYPT(login.Password,?) FROM login,contact WHERE login.contact_Id = contact.Id");
            pst.setString(1, AES_PASSWORD);
            pst.setString(2, AES_PASSWORD);
            rs = pst.executeQuery();
            
            while (rs.next()) {
            	AuthenticationModel tempLogin = new AuthenticationModel(rs.getInt(1), // Id
						Long.valueOf(rs.getString(2)), // contact_Id
						rs.getString(3), // Name
						rs.getString(4)); // Password
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
