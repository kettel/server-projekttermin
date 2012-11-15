package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Assignment;
import model.ModelInterface;
import model.Contact;
import model.AssignmentStatus;

public class DatabaseHandlerAssignment extends DatabaseHandler{
	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;
	
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
            pst.setString(2, Double.toString(ass.getLat()));
            pst.setString(3, Double.toString(ass.getLon()));
            pst.setString(4, ass.getRegion());
            pst.setString(5, ass.getAgentsString());
            pst.setString(6, ass.getSender());
            pst.setString(7, Boolean.toString(ass.isExternalMission()));
            pst.setString(8, ass.getAssignmentDescription());
            pst.setString(9, ass.getTimeSpan());
            pst.setString(10, ass.getAssignmentStatus().toString());
//            pst.setBytes(11, ass.getCameraImage());
            pst.setBytes(11, null);
            pst.setString(12, ass.getStreetName());
            pst.setString(13, ass.getSiteName());
            pst.setString(14, Long.toString(ass.getTimeStamp()));
          
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
            		" SET Name = \"" + ass.getName() +
            		"\", Latitude = \"" + Double.toString(ass.getLat()) + 
            		"\", Longitude = \"" + Double.toString(ass.getLon()) + 
            		"\", Region = \"" + ass.getRegion() + 
            		"\", Agents = \"" + ass.getAgentsString() + 
            		"\", Sender = \"" + ass.getSender() + 
            		"\", ExternalMission = \"" + Boolean.toString(ass.isExternalMission()) +
            		"\", Description = \"" + ass.getAssignmentDescription() +
            		"\", Timespan = \"" + ass.getAssignmentStatus() +
            		"\", Status = \"" + ass.getAssignmentStatus().toString() +
//            		"\", Cameraimage = \"" + ass.getCameraImage() + 
            		"\", Cameraimage = \"" + null +
            		"\", Streetname = \"" + ass.getStreetName() +
            		"\", Sitename = \"" + ass.getSiteName() +
            		"\" WHERE Id = " + ass.getId());
            
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
            	returnList.add((ModelInterface) new Assignment(rs.getInt(1), // Id
            						rs.getString(2),// Namn
            						Double.valueOf(rs.getString(3)), // Lat
            						Double.valueOf(rs.getString(4)), // Lon
            						rs.getString(5), // Region
            						getAgentsFromString(rs.getString(6)), // Agents
            						rs.getString(7), // Sender
            						Boolean.parseBoolean(rs.getString(8)), // ExternalMission
            						rs.getString(9), // Desc
            						rs.getString(10), // Timespan
            						/*AssignmentStatus.valueOf(*/rs.getString(11)/*)*/, // Status
            						rs.getBytes(12), // CameraImage
            						rs.getString(13), // Streetname
            						rs.getString(14), // Sitename
            						Long.valueOf(rs.getString(15)))); // Timestamp
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
	
	private List<String> getAgentsFromString(String agentString){
		// Gör om strängar med agenter på uppdrag till en lista
		List <String> agents = new ArrayList<String>();
		String[] agentArray = agentString.split("/");
		for (String agent : agentArray) {
			// Dela upp kontakten så man kommer åt namn och IP
			String[] contactArray = agent.split(":");
			agents.add(new Contact(contactArray[0],contactArray[1]).getContactName());
		}
		return agents;
	}
	
//	private String[] getAgentsFromString(String agentString){
//		// Gör om strängar med agenter på uppdrag till en lista
//		List <String> agents = new ArrayList<String>();
//		String[] agentArray = agentString.split("/");
//		for (String agent : agentArray) {
//			// Dela upp kontakten så man kommer åt namn och IP
//			String[] contactArray = agent.split(":");
//			agents.add(new Contact(contactArray[0],contactArray[1]).getContactName());
//		}
//		return agentArray;
//	}


}
