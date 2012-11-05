package database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Assignment;
import model.ModelInterface;

public class DatabaseHandlerAssignment extends DatabaseHandler{

	@Override
	public void addModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till Contact
			Assignment ass = (Assignment)m;
			
			// Initiera en anslutning till databasen
            con = DriverManager.getConnection(url, user, password);
            
            // SQL-frågan
            pst = con.prepareStatement("INSERT INTO "+m.getDatabaseRepresentation()+"(Name , Latitude , Longitude , Receiver , Sender , Description , Timespan , Status , Cameraimage , Streetname , Sitename) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            
            // Sätt in rätt värden till rätt plats i frågan
            pst.setString(1, ass.getName());
            pst.setString(2, Long.toString(ass.getLat()));
            pst.setString(3, Long.toString(ass.getLon()));
            pst.setString(4, ass.getReceiver());
            pst.setString(5, ass.getSender());
            pst.setString(6, ass.getAssignmentDescription());
            pst.setString(7, ass.getTimeSpan());
            pst.setString(8, ass.getAssignmentStatus());
            pst.setBytes(9, ass.getCameraImage());
            pst.setString(10, ass.getStreetName());
            pst.setString(11, ass.getSiteName());
            
            // Kan krascha. Osäker på om SetBytes -> Blob (blir visst vara VARBINARY)
            pst.setBytes(9, ass.getCameraImage());
            
            pst.setString(10, ass.getStreetName());
            pst.setString(11, ass.getSiteName());
           
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
	public void updateModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till MessageModel
			Assignment ass= (Assignment)m;
			
			con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            // Sätt autocommit till falskt
            con.setAutoCommit(false);
            
            // Sätt in rätt värden till rätt plats i frågan och uppdatera dessa
            st.executeUpdate("UPDATE " + ass.getDatabaseRepresentation() + 
            		" SET Name = " + ass.getName() +
            		", Latitude = " + Long.toString(ass.getLat()) + 
            		", Longitude = " + Long.toString(ass.getLon()) + 
            		", Receiver = " + ass.getReceiver() + 
            		", Sender = " + ass.getSender() + 
            		", Description = " + ass.getAssignmentDescription() +
            		", Timespan = " + ass.getAssignmentStatus() +
            		", Cameraimage = " + ass.getCameraImage() + 
            		", Streetname = " + ass.getStreetName() +
            		", Sitename = " + ass.getSiteName() +
            		" WHERE Id = " + ass.getId());
            
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

	@Override
	public List<ModelInterface> getAllModels(ModelInterface m) {
		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		try {
            con = DriverManager.getConnection(url, user, password);
            pst = con.prepareStatement("SELECT * FROM " + m.getDatabaseRepresentation());
            rs = pst.executeQuery();

            while (rs.next()) {
            	// Hämta och skapa ett nytt Contact-objekt samt lägg
            	// till det i returnList
            	returnList.add((ModelInterface) new Assignment(rs.getInt(0),
            						rs.getString(1),
            						Long.valueOf(rs.getString(2)),
            						Long.valueOf(rs.getString(2)),
            						rs.getString(3),
            						rs.getString(4),
            						rs.getString(5),
            						rs.getString(6),
            						rs.getString(7),
            						// Lyckad konvertering för bilden?
            						rs.getBytes(8),
            						rs.getString(9),
            						rs.getString(10)));
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
