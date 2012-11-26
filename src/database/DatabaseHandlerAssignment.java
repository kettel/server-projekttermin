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

public class DatabaseHandlerAssignment extends DatabaseHandler {
	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;

	@Override
	public void addModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till Contact
			Assignment ass = (Assignment) m;

			// Initiera en anslutning till databasen
			con = DriverManager.getConnection(url, user, password);

			// SQL-frågan
			pst = con
					.prepareStatement("INSERT INTO "
							+ m.getDatabaseRepresentation()
							+ "(Name , Latitude , Longitude , Region , Agents , ExternalMission , Sender , Description , Timespan , Status , Cameraimage , Streetname , Sitename , Timestamp) " +
							"VALUES (AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?)," +
									"AES_ENCRYPT(?,?))");

			// Sätt in rätt värden till rätt plats i frågan
			pst.setString(1, ass.getName());
			pst.setString(2, AES_PASSWORD);
			pst.setString(3, Double.toString(ass.getLat()));
			pst.setString(4, AES_PASSWORD);
			pst.setString(5, Double.toString(ass.getLon()));
			pst.setString(6, AES_PASSWORD);
			pst.setString(7, ass.getRegion());
			pst.setString(8, AES_PASSWORD);
			pst.setString(9, ass.getAgentsString());
			pst.setString(10, AES_PASSWORD);
			pst.setString(11, ass.getSender());
			pst.setString(12, AES_PASSWORD);
			pst.setString(13, Boolean.toString(ass.isExternalMission()));
			pst.setString(14, AES_PASSWORD);
			pst.setString(15, ass.getAssignmentDescription());
			pst.setString(16, AES_PASSWORD);
			pst.setString(17, ass.getTimeSpan());
			pst.setString(18, AES_PASSWORD);
			pst.setString(19, ass.getAssignmentStatus().toString());
			pst.setString(20, AES_PASSWORD);
			pst.setBytes(21, ass.getCameraImage());
			pst.setString(22, AES_PASSWORD);
			pst.setString(23, ass.getStreetName());
			pst.setString(24, AES_PASSWORD);
			pst.setString(25, ass.getSiteName());
			pst.setString(26, AES_PASSWORD);
			pst.setString(27, Long.toString(ass.getTimeStamp()));
			pst.setString(28, AES_PASSWORD);

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
			Assignment ass = (Assignment) m;

			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();

			// Sätt autocommit till falskt
			con.setAutoCommit(false);

			// Sätt in rätt värden till rätt plats i frågan och uppdatera dessa
			st.executeUpdate("UPDATE " + ass.getDatabaseRepresentation()
					+ " SET Name = \"" + ass.getName() + "\", Latitude = \""
					+ Double.toString(ass.getLat()) + "\", Longitude = \""
					+ Double.toString(ass.getLon()) + "\", Region = \""
					+ ass.getRegion() + "\", Agents = \""
					+ ass.getAgentsString() + "\", Sender = \""
					+ ass.getSender() + "\", ExternalMission = \""
					+ Boolean.toString(ass.isExternalMission())
					+ "\", Description = \"" + ass.getAssignmentDescription()
					+ "\", Timespan = \"" + ass.getAssignmentStatus()
					+ "\", Status = \"" + ass.getAssignmentStatus().toString()
					+ "\", Cameraimage = \"" + ass.getCameraImage()
					+ "\", Streetname = \"" + ass.getStreetName()
					+ "\", Sitename = \"" + ass.getSiteName()
					+ "\" WHERE Id = " + ass.getId());

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
			pst = con.prepareStatement("SELECT Id," + 
					" AES_DECRYPT(Name,?)," 
					+ "AES_DECRYPT(Latitude,?)," 
					+ "AES_DECRYPT(Longitude,?),"
					+ "AES_DECRYPT(Region,?),"
					+ "AES_DECRYPT(Agents,?),"
					+ "AES_DECRYPT(Sender,?),"
					+ "AES_DECRYPT(ExternalMission,?),"
					+ "AES_DECRYPT(Description,?),"
					+ "AES_DECRYPT(Timespan,?),"
					+ "AES_DECRYPT(Status,?),"
					+ "AES_DECRYPT(Cameraimage,?),"
					+ "AES_DECRYPT(Streetname,?),"
					+ "AES_DECRYPT(Sitename,?),"
					+ "AES_DECRYPT(Timestamp,?) FROM "
					+ m.getDatabaseRepresentation());
			for(int i = 1; i < 15; i++){
				pst.setString(i, AES_PASSWORD);
			}
			rs = pst.executeQuery();
			System.out.println("Storlek? "+rs.getFetchSize());
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
						AssignmentStatus.valueOf(rs.getString(11)), // Status
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

	private List<Contact> getAgentsFromString(String agentString) {
		// Gör om strängar med agenter på uppdrag till en lista
		List<Contact> agents = new ArrayList<Contact>();
		String[] agentArray = agentString.split("/");
		for (String agent : agentArray) {
			// Dela upp kontakten så man kommer åt namn och IP
			String[] contactArray = agent.split(":");
			agents.add(new Contact(contactArray[0], contactArray[1]));
		}
		return agents;
	}
}
