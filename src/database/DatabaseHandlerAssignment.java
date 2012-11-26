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
							+ "(Name , Latitude , Longitude , Region , Agents , ExternalMission , Sender , Description , Timespan , Status , Cameraimage , Streetname , Sitename , Timestamp) "
							+ "VALUES (AES_ENCRYPT(?,?)," + "AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?)," + "AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?)," + "AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?)," + "AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?)," + "AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?)," + "AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?)," + "AES_ENCRYPT(?,?))");

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
			pst = con.prepareStatement("SELECT Id," + " AES_DECRYPT(Name,?),"
					+ "AES_DECRYPT(Latitude,?)," + "AES_DECRYPT(Longitude,?),"
					+ "AES_DECRYPT(Region,?)," + "AES_DECRYPT(Agents,?),"
					+ "AES_DECRYPT(Sender,?),"
					+ "AES_DECRYPT(ExternalMission,?),"
					+ "AES_DECRYPT(Description,?),"
					+ "AES_DECRYPT(Timespan,?)," + "AES_DECRYPT(Status,?),"
					+ "AES_DECRYPT(Cameraimage,?),"
					+ "AES_DECRYPT(Streetname,?)," + "AES_DECRYPT(Sitename,?),"
					+ "AES_DECRYPT(Timestamp,?) FROM "
					+ m.getDatabaseRepresentation());
			for (int i = 1; i < 15; i++) {
				pst.setString(i, AES_PASSWORD);
			}
			rs = pst.executeQuery();

			while (rs.next()) {
				// Hämta och skapa ett nytt Contact-objekt samt lägg
				// till det i returnList
				System.out.println("Bild? " + rs.getBytes(12));
				long id = Long.valueOf(rs.getInt(1));
				System.out.println("Id: " + id);
				String name = rs.getString(2);
				System.out.println("name: " + name);
				double lat = Double.valueOf(rs.getString(3));
				System.out.println("lat: " + lat);
				double lon = Double.valueOf(rs.getString(4));
				System.out.println("lon: " + lon);
				String region = rs.getString(5);
				System.out.println("region: " + region);
				List<Contact> agents = getAgentsFromString(rs.getString(6));
				System.out.println("agents: " + agents.toString());
				String sender = rs.getString(7);
				System.out.println("sender: " + sender);
				boolean extMission = Boolean.parseBoolean(rs.getString(8));
				System.out.println("extMission: " + extMission);
				String desc = rs.getString(9);
				System.out.println("desc: " + desc);
				String timespan = rs.getString(10);
				System.out.println("timespan: " + timespan);
				AssignmentStatus astatus = AssignmentStatus.valueOf(rs.getString(11));
				System.out.println("assignment status: " + astatus);
				byte[] camImg = rs.getBytes(12);
				System.out.println("camImg: " + camImg.toString());
				String strName = rs.getString(13);
				System.out.println("strName: " + strName);
				String siteName = rs.getString(14);
				System.out.println("siteName: " + siteName);
				Long timestamp = Long.valueOf(rs.getString(15));
				System.out.println("timestamp: " + timestamp);
				returnList.add((ModelInterface) new Assignment(id,name,lat,lon,region,agents,sender,extMission,desc,timespan,astatus,camImg,strName,siteName,timestamp));
				System.out.println("Hit borde den inte komma");
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
		if (agentString != null || agentString.length() > 0) {
			System.out.println("agentString: " + agentString);
			String[] agentArray = agentString.split("\\");
			for (String agent : agentArray) {
				// Dela upp kontakten så man kommer åt namn och IP
				String[] contactArray = agent.split(":");
				System.out.println("contactArray[0]: " + contactArray[0]);
				System.out.println("contactArray[1]: " + contactArray[1]);
				agents.add(new Contact(contactArray[0], contactArray[1]));
			}
		}
		return agents;
	}
}
